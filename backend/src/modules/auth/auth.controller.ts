import {
  Controller,
  Post,
  Body,
  HttpCode,
  HttpStatus,
  UseGuards,
  Get,
  Request,
} from '@nestjs/common';
import { ApiTags, ApiOperation, ApiResponse, ApiBearerAuth } from '@nestjs/swagger';
import { AuthService } from './auth.service';
import { AdminLoginDto } from './dto/admin-login.dto';
import { DriverLoginDto } from './dto/driver-login.dto';
import { DriverRegisterDto } from './dto/driver-register.dto';
import { AuthGuard } from '@nestjs/passport';

@ApiTags('Authentication')
@Controller('auth')
export class AuthController {
  constructor(private readonly authService: AuthService) {}

  @Post('login')
  @HttpCode(HttpStatus.OK)
  @ApiOperation({ summary: 'Admin and Dispatcher Email/Password Sign-In' })
  @ApiResponse({ status: 200, description: 'Authenticated successfully' })
  @ApiResponse({ status: 401, description: 'Invalid credentials or inactive account' })
  async loginAdmin(@Body() dto: AdminLoginDto) {
    return this.authService.loginAdmin(dto);
  }

  @Post('driver/login-pin')
  @HttpCode(HttpStatus.OK)
  @ApiOperation({ summary: 'Driver Phone and PIN Sign-In' })
  @ApiResponse({ status: 200, description: 'Driver authenticated successfully' })
  @ApiResponse({ status: 401, description: 'Invalid PIN or account pending verification' })
  async loginDriver(@Body() dto: DriverLoginDto) {
    return this.authService.loginDriver(dto);
  }

  @Post('driver/register')
  @HttpCode(HttpStatus.CREATED)
  @ApiOperation({ summary: 'Driver Self-Registration' })
  @ApiResponse({ status: 201, description: 'Driver registration submitted for Admin verification' })
  @ApiResponse({ status: 400, description: 'Validation failed or phone number already in use' })
  async registerDriver(@Body() dto: DriverRegisterDto) {
    return this.authService.registerDriver(dto);
  }

  @Get('me')
  @UseGuards(AuthGuard('jwt'))
  @ApiBearerAuth()
  @ApiOperation({ summary: 'Get current user profile from token' })
  async getProfile(@Request() req: any) {
    return { user: req.user };
  }
}
