import { IsNotEmpty, Matches, Length } from 'class-validator';
import { ApiProperty } from '@nestjs/swagger';

export class DriverLoginDto {
  @ApiProperty({ example: '+919847012345', description: 'Indian mobile number starting with +91' })
  @IsNotEmpty()
  @Matches(/^\+91[6-9]\d{9}$/, { message: 'Phone must be a valid 10-digit Indian mobile number prefixed with +91' })
  phone: string;

  @ApiProperty({ example: '1234', description: '4 to 6 digit security PIN' })
  @IsNotEmpty()
  @Length(4, 6, { message: 'PIN must be between 4 and 6 digits' })
  pin: string;

  @ApiProperty({ required: false, example: 'Samsung Galaxy M14' })
  deviceModel?: string;

  @ApiProperty({ required: false, example: 'fcm_token_123' })
  fcmToken?: string;
}
