import {
  Controller,
  Get,
  Post,
  Param,
  Body,
  Query,
  UseGuards,
  Request,
} from '@nestjs/common';
import { ApiTags, ApiOperation, ApiResponse, ApiBearerAuth } from '@nestjs/swagger';
import { DirectoryService } from './directory.service';
import { AuthGuard } from '@nestjs/passport';
import { RolesGuard } from '../../common/guards/roles.guard';
import { Roles } from '../../common/decorators/roles.decorator';

@ApiTags('Directory')
@Controller()
@UseGuards(AuthGuard('jwt'), RolesGuard)
@ApiBearerAuth()
export class DirectoryController {
  constructor(private readonly directoryService: DirectoryService) {}

  @Get('drivers')
  @Roles('super_admin', 'dispatcher')
  @ApiOperation({ summary: 'List all drivers with optional status and search filter' })
  async getDrivers(
    @Query('availability') availability?: string,
    @Query('search') search?: string,
  ) {
    return this.directoryService.getDrivers(availability, search);
  }

  @Get('drivers/:id')
  @ApiOperation({ summary: 'Get driver profile and compliance documents' })
  async getDriver(@Param('id') id: string) {
    return this.directoryService.getDriverById(id);
  }

  @Get('admin/drivers/pending')
  @Roles('super_admin', 'dispatcher')
  @ApiOperation({ summary: 'List drivers awaiting verification approval' })
  async getPendingApprovals() {
    return this.directoryService.getPendingApprovals();
  }

  @Post('admin/drivers/:id/approve')
  @Roles('super_admin', 'dispatcher')
  @ApiOperation({ summary: 'Approve a self-registered driver' })
  async approveDriver(@Param('id') id: string, @Request() req: any) {
    return this.directoryService.approveDriver(id, req.user.id);
  }

  @Post('admin/drivers/:id/reject')
  @Roles('super_admin', 'dispatcher')
  @ApiOperation({ summary: 'Reject a pending driver with reason' })
  async rejectDriver(
    @Param('id') id: string,
    @Body('reason') reason: string,
    @Request() req: any,
  ) {
    return this.directoryService.rejectDriver(id, req.user.id, reason);
  }

  @Get('vehicles')
  @Roles('super_admin', 'dispatcher')
  @ApiOperation({ summary: 'List fleet vehicles' })
  async getVehicles(@Query('status') status?: string) {
    return this.directoryService.getVehicles(status);
  }
}
