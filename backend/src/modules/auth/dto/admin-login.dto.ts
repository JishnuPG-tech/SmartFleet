import { IsEmail, IsNotEmpty, MinLength, IsOptional, IsString } from 'class-validator';
import { ApiProperty } from '@nestjs/swagger';

export class AdminLoginDto {
  @ApiProperty({ example: 'admin@truckconnect.in' })
  @IsEmail()
  email: string;

  @ApiProperty({ example: 'Admin@123' })
  @IsNotEmpty()
  @MinLength(6)
  password: string;

  @ApiProperty({ required: false, example: 'Pixel 7 Pro' })
  @IsOptional()
  @IsString()
  deviceModel?: string;

  @ApiProperty({ required: false, example: 'fcm_token_xyz' })
  @IsOptional()
  @IsString()
  fcmToken?: string;
}
