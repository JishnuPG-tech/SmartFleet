import {
  Injectable,
  UnauthorizedException,
  BadRequestException,
  Logger,
} from '@nestjs/common';
import { JwtService } from '@nestjs/jwt';
import { ConfigService } from '@nestjs/config';
import * as bcrypt from 'bcrypt';
import { SupabaseService } from '../../common/supabase.service';
import { AdminLoginDto } from './dto/admin-login.dto';
import { DriverLoginDto } from './dto/driver-login.dto';
import { DriverRegisterDto } from './dto/driver-register.dto';

@Injectable()
export class AuthService {
  private readonly logger = new Logger(AuthService.name);

  constructor(
    private supabaseService: SupabaseService,
    private jwtService: JwtService,
    private configService: ConfigService,
  ) {}

  // 1. Admin Login (Email + Password)
  async loginAdmin(dto: AdminLoginDto) {
    const supabase = this.supabaseService.getClient();

    const { data: user, error } = await supabase
      .from('users')
      .select('id, role, name, email, phone, password_hash, status, language, theme')
      .eq('email', dto.email.toLowerCase().trim())
      .single();

    if (error || !user) {
      throw new UnauthorizedException('Invalid email or password');
    }

    if (user.role !== 'super_admin' && user.role !== 'dispatcher') {
      throw new UnauthorizedException('Access restricted to Admin and Dispatcher');
    }

    if (user.status !== 'active') {
      throw new UnauthorizedException('Account is not active');
    }

    const isMatch = await bcrypt.compare(dto.password, user.password_hash);
    if (!isMatch) {
      throw new UnauthorizedException('Invalid email or password');
    }

    // Register or update device
    if (dto.fcmToken) {
      await this.bindDevice(user.id, dto.fcmToken, dto.deviceModel);
    }

    const tokens = this.generateTokens(user);
    return {
      user: {
        id: user.id,
        name: user.name,
        email: user.email,
        phone: user.phone,
        role: user.role,
        language: user.language,
        theme: user.theme,
      },
      ...tokens,
    };
  }

  // 2. Driver Login (Phone + 4-digit PIN)
  async loginDriver(dto: DriverLoginDto) {
    const supabase = this.supabaseService.getClient();

    const { data: user, error } = await supabase
      .from('users')
      .select('id, role, name, phone, pin_hash, pin_failed_attempts, pin_locked_until, status, language, theme')
      .eq('phone', dto.phone.trim())
      .single();

    if (error || !user) {
      throw new UnauthorizedException('Phone number not registered. Please register first.');
    }

    if (user.role !== 'driver') {
      throw new UnauthorizedException('Account is not a driver profile');
    }

    // Check account status
    if (user.status === 'pending_verification') {
      throw new UnauthorizedException(
        'Account is pending verification by Fleet Admin. You will be notified once approved.',
      );
    }

    if (user.status !== 'active') {
      throw new UnauthorizedException('Driver account is inactive or suspended');
    }

    // Check lockout
    if (user.pin_locked_until && new Date(user.pin_locked_until) > new Date()) {
      throw new UnauthorizedException('Account temporarily locked due to failed attempts. Try again later.');
    }

    // Verify PIN
    const isMatch = await bcrypt.compare(dto.pin, user.pin_hash);
    if (!isMatch) {
      const attempts = (user.pin_failed_attempts || 0) + 1;
      let lockUntil = null;
      if (attempts >= 5) {
        lockUntil = new Date(Date.now() + 15 * 60 * 1000).toISOString(); // 15 min lock
      }
      await supabase
        .from('users')
        .update({ pin_failed_attempts: attempts, pin_locked_until: lockUntil })
        .eq('id', user.id);

      throw new UnauthorizedException(
        attempts >= 5 ? 'Account locked for 15 minutes due to 5 failed attempts' : `Invalid PIN. ${5 - attempts} attempts remaining`,
      );
    }

    // Reset failed attempts on success
    await supabase
      .from('users')
      .update({ pin_failed_attempts: 0, pin_locked_until: null })
      .eq('id', user.id);

    // Register or update device
    if (dto.fcmToken) {
      await this.bindDevice(user.id, dto.fcmToken, dto.deviceModel);
    }

    // Fetch driver profile details
    const { data: driverProfile } = await supabase
      .from('drivers')
      .select('licence_no, licence_type, availability')
      .eq('user_id', user.id)
      .single();

    const tokens = this.generateTokens(user);
    return {
      user: {
        id: user.id,
        name: user.name,
        phone: user.phone,
        role: user.role,
        language: user.language,
        theme: user.theme,
        driver: driverProfile || {},
      },
      ...tokens,
    };
  }

  // 3. Driver Self-Registration
  async registerDriver(dto: DriverRegisterDto) {
    const supabase = this.supabaseService.getClient();

    // Check if phone already registered
    const { data: existing } = await supabase
      .from('users')
      .select('id, status')
      .eq('phone', dto.phone.trim())
      .single();

    if (existing) {
      if (existing.status === 'pending_verification') {
        throw new BadRequestException('A registration with this phone number is already pending review.');
      }
      throw new BadRequestException('Phone number is already registered. Please sign in with your PIN.');
    }

    const pinHash = await bcrypt.hash(dto.pin, 10);

    // Insert user with pending_verification
    const { data: newUser, error: userError } = await supabase
      .from('users')
      .insert({
        role: 'driver',
        name: dto.name.trim(),
        phone: dto.phone.trim(),
        pin_hash: pinHash,
        status: 'pending_verification',
        language: 'ml',
        theme: 'indigo',
      })
      .select('id, name, phone, role, status')
      .single();

    if (userError || !newUser) {
      this.logger.error('Failed to create user during driver registration', userError);
      throw new BadRequestException('Registration failed: ' + (userError?.message || 'Unknown error'));
    }

    // Insert driver profile
    const { error: driverError } = await supabase
      .from('drivers')
      .insert({
        user_id: newUser.id,
        licence_no: dto.licenceNo.trim(),
        licence_type: dto.licenceType,
        licence_expiry: dto.licenceExpiry,
        emergency_contact_name: dto.emergencyContactName.trim(),
        emergency_contact_phone: dto.emergencyContactPhone.trim(),
        emergency_contact_relation: dto.emergencyContactRelation.trim(),
        vehicle_ownership: dto.vehicleOwnership || 'company_fleet',
        approval_status: 'pending',
        availability: 'offline',
      });

    if (driverError) {
      this.logger.error('Failed to create driver record', driverError);
      // Rollback user
      await supabase.from('users').delete().eq('id', newUser.id);
      throw new BadRequestException('Registration failed: ' + driverError.message);
    }

    // Insert licence photos into documents table if provided
    if (dto.licenceFrontPhotoUrl) {
      await supabase.from('documents').insert({
        owner_type: 'driver',
        owner_id: newUser.id,
        kind: 'driving_licence_front',
        file_url: dto.licenceFrontPhotoUrl,
        expiry_date: dto.licenceExpiry,
      });
    }

    if (dto.licenceBackPhotoUrl) {
      await supabase.from('documents').insert({
        owner_type: 'driver',
        owner_id: newUser.id,
        kind: 'driving_licence_back',
        file_url: dto.licenceBackPhotoUrl,
        expiry_date: dto.licenceExpiry,
      });
    }

    return {
      message: 'Registration submitted successfully. Pending Admin verification.',
      userId: newUser.id,
      status: 'pending_verification',
    };
  }

  // Helper: Device Binding
  private async bindDevice(userId: string, fcmToken: string, deviceModel?: string) {
    const supabase = this.supabaseService.getClient();
    await supabase.from('devices').upsert(
      {
        user_id: userId,
        fcm_token: fcmToken,
        device_model: deviceModel || 'Unknown Device',
        last_seen: new Date().toISOString(),
        revoked: false,
      },
      { onConflict: 'user_id, fcm_token' },
    );
  }

  // Helper: Token Minting with Supabase JWT Secret
  private generateTokens(user: { id: string; role: string }) {
    const payload = {
      sub: user.id,
      role: 'authenticated', // Required for Supabase Realtime RLS
      user_role: user.role,
    };

    const accessToken = this.jwtService.sign(payload, { expiresIn: '7d' });
    const refreshToken = this.jwtService.sign(
      { sub: user.id, type: 'refresh' },
      { expiresIn: '30d' },
    );

    return {
      accessToken,
      refreshToken,
      expiresIn: 7 * 24 * 60 * 60,
    };
  }
}
