import { IsNotEmpty, Matches, Length, IsEnum, IsDateString, IsOptional, IsBoolean } from 'class-validator';
import { ApiProperty } from '@nestjs/swagger';

export class DriverRegisterDto {
  @ApiProperty({ example: 'Ravi Kumar' })
  @IsNotEmpty()
  name: string;

  @ApiProperty({ example: '+919847012345' })
  @IsNotEmpty()
  @Matches(/^\+91[6-9]\d{9}$/, { message: 'Phone must be a valid Indian mobile number (+91)' })
  phone: string;

  @ApiProperty({ example: '1234' })
  @IsNotEmpty()
  @Length(4, 6, { message: 'PIN must be between 4 and 6 digits' })
  pin: string;

  @ApiProperty({ example: 'KL07 20180012345' })
  @IsNotEmpty()
  licenceNo: string;

  @ApiProperty({ example: 'HMV', enum: ['HMV', 'LMV-Transport'] })
  @IsNotEmpty()
  licenceType: string;

  @ApiProperty({ example: '2029-12-31' })
  @IsDateString()
  licenceExpiry: string;

  @ApiProperty({ example: 'Suma Ravi' })
  @IsNotEmpty()
  emergencyContactName: string;

  @ApiProperty({ example: '+919447099887' })
  @IsNotEmpty()
  emergencyContactPhone: string;

  @ApiProperty({ example: 'Spouse' })
  @IsNotEmpty()
  emergencyContactRelation: string;

  @ApiProperty({ example: 'company_fleet', enum: ['company_fleet', 'owner_operator'] })
  @IsOptional()
  vehicleOwnership?: string;

  @ApiProperty({ required: false, example: 'KL 07 AB 1234' })
  @IsOptional()
  vehicleRegNo?: string;

  @ApiProperty({ example: true, description: 'DPDP Act location consent' })
  @IsBoolean()
  locationConsent: boolean;

  @ApiProperty({ required: false })
  @IsOptional()
  licenceFrontPhotoUrl?: string;

  @ApiProperty({ required: false })
  @IsOptional()
  licenceBackPhotoUrl?: string;
}
