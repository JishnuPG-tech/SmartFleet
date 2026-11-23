import { Injectable, NotFoundException, BadRequestException, Logger } from '@nestjs/common';
import { SupabaseService } from '../../common/supabase.service';

@Injectable()
export class DirectoryService {
  private readonly logger = new Logger(DirectoryService.name);

  constructor(private supabaseService: SupabaseService) {}

  // List all drivers with filters
  async getDrivers(availability?: string, search?: string) {
    const supabase = this.supabaseService.getClient();
    let query = supabase
      .from('drivers')
      .select(`
        user_id, licence_no, licence_type, licence_expiry, availability, approval_status,
        users:user_id (id, name, phone, status, language, theme)
      `);

    if (availability) {
      query = query.eq('availability', availability);
    }
    if (search) {
      query = query.ilike('users.name', `%${search}%`);
    }

    const { data, error } = await query;
    if (error) throw new BadRequestException(error.message);
    return data;
  }

  // Get driver details
  async getDriverById(userId: string) {
    const supabase = this.supabaseService.getClient();
    const { data: driver, error } = await supabase
      .from('drivers')
      .select(`
        *,
        users:user_id (id, name, phone, email, status, language, theme),
        documents:documents!owner_id (*)
      `)
      .eq('user_id', userId)
      .single();

    if (error || !driver) throw new NotFoundException('Driver not found');
    return driver;
  }

  // List pending driver approvals for Admin
  async getPendingApprovals() {
    const supabase = this.supabaseService.getClient();
    const { data, error } = await supabase
      .from('drivers')
      .select(`
        user_id, licence_no, licence_type, licence_expiry, emergency_contact_name,
        emergency_contact_phone, emergency_contact_relation, vehicle_ownership, created_at,
        users:user_id (id, name, phone, created_at)
      `)
      .eq('approval_status', 'pending');

    if (error) throw new BadRequestException(error.message);
    return data;
  }

  // Admin approves a driver
  async approveDriver(userId: string, adminId: string) {
    const supabase = this.supabaseService.getClient();

    // 1. Update driver record
    const { error: driverErr } = await supabase
      .from('drivers')
      .update({
        approval_status: 'approved',
        approved_by: adminId,
        approved_at: new Date().toISOString(),
        availability: 'available',
      })
      .eq('user_id', userId);

    if (driverErr) throw new BadRequestException(driverErr.message);

    // 2. Activate user account
    const { error: userErr } = await supabase
      .from('users')
      .update({ status: 'active' })
      .eq('id', userId);

    if (userErr) throw new BadRequestException(userErr.message);

    this.logger.log(`Driver ${userId} approved by admin ${adminId}`);
    return { success: true, message: 'Driver approved successfully' };
  }

  // Admin rejects a driver
  async rejectDriver(userId: string, adminId: string, reason: string) {
    const supabase = this.supabaseService.getClient();

    const { error: driverErr } = await supabase
      .from('drivers')
      .update({
        approval_status: 'rejected',
        rejection_reason: reason || 'Requirements not met',
        approved_by: adminId,
        approved_at: new Date().toISOString(),
      })
      .eq('user_id', userId);

    if (driverErr) throw new BadRequestException(driverErr.message);

    await supabase
      .from('users')
      .update({ status: 'inactive' })
      .eq('id', userId);

    this.logger.log(`Driver ${userId} rejected by admin ${adminId}: ${reason}`);
    return { success: true, message: 'Driver rejected' };
  }

  // List all vehicles
  async getVehicles(status?: string) {
    const supabase = this.supabaseService.getClient();
    let query = supabase.from('vehicles').select('*');
    if (status) query = query.eq('status', status);

    const { data, error } = await query;
    if (error) throw new BadRequestException(error.message);
    return data;
  }
}
