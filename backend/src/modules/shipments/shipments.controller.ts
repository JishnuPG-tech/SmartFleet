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
import { ShipmentsService } from './shipments.service';
import { AuthGuard } from '@nestjs/passport';
import { RolesGuard } from '../../common/guards/roles.guard';
import { Roles } from '../../common/decorators/roles.decorator';

@ApiTags('Shipments')
@Controller('shipments')
@UseGuards(AuthGuard('jwt'), RolesGuard)
@ApiBearerAuth()
export class ShipmentsController {
  constructor(private readonly shipmentsService: ShipmentsService) {}

  @Get()
  @ApiOperation({ summary: 'List shipments' })
  async getShipments(@Request() req: any, @Query('status') status?: string) {
    return this.shipmentsService.getShipments(req.user.id, req.user.role, status);
  }

  @Get(':id')
  @ApiOperation({ summary: 'Get shipment details and timeline' })
  async getShipment(@Param('id') id: string) {
    return this.shipmentsService.getShipmentById(id);
  }

  @Post()
  @Roles('super_admin', 'dispatcher')
  @ApiOperation({ summary: 'Create new shipment' })
  async createShipment(@Body() dto: any, @Request() req: any) {
    return this.shipmentsService.createShipment(dto, req.user.id);
  }

  @Post(':id/assign')
  @Roles('super_admin', 'dispatcher')
  @ApiOperation({ summary: 'Assign driver and vehicle to shipment' })
  async assignShipment(
    @Param('id') id: string,
    @Body('driverId') driverId: string,
    @Body('vehicleId') vehicleId: string,
    @Request() req: any,
  ) {
    return this.shipmentsService.assignShipment(id, driverId, vehicleId, req.user.id);
  }

  @Post(':id/accept')
  @Roles('driver')
  @ApiOperation({ summary: 'Driver accepts job assignment' })
  async acceptShipment(@Param('id') id: string, @Request() req: any) {
    return this.shipmentsService.acceptShipment(id, req.user.id);
  }

  @Post(':id/reject')
  @Roles('driver')
  @ApiOperation({ summary: 'Driver rejects job assignment with reason' })
  async rejectShipment(
    @Param('id') id: string,
    @Body('reason') reason: string,
    @Request() req: any,
  ) {
    return this.shipmentsService.rejectShipment(id, req.user.id, reason);
  }

  @Post(':id/milestone')
  @Roles('driver')
  @ApiOperation({ summary: 'Driver updates trip milestone (reached_pickup, loaded, in_transit, reached_drop)' })
  async updateMilestone(
    @Param('id') id: string,
    @Body('milestone') milestone: string,
    @Body('note') note: string,
    @Request() req: any,
  ) {
    return this.shipmentsService.updateMilestone(id, milestone, req.user.id, note);
  }

  @Post(':id/complete')
  @Roles('driver')
  @ApiOperation({ summary: 'Complete delivery with Proof of Delivery (photos, receiver name)' })
  async completeDelivery(
    @Param('id') id: string,
    @Body() pod: { photoUrls: string[]; receiverName: string; signatureUrl?: string },
    @Request() req: any,
  ) {
    return this.shipmentsService.completeDelivery(id, req.user.id, pod);
  }
}
