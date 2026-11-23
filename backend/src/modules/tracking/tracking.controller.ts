import { Controller, Post, Get, Body, UseGuards, Request } from '@nestjs/common';
import { ApiTags, ApiOperation, ApiBearerAuth } from '@nestjs/swagger';
import { TrackingService, LocationPingDto } from './tracking.service';
import { AuthGuard } from '@nestjs/passport';
import { RolesGuard } from '../../common/guards/roles.guard';
import { Roles } from '../../common/decorators/roles.decorator';

@ApiTags('Live Tracking')
@Controller('tracking')
@UseGuards(AuthGuard('jwt'), RolesGuard)
@ApiBearerAuth()
export class TrackingController {
  constructor(private readonly trackingService: TrackingService) {}

  @Post('pings')
  @Roles('driver')
  @ApiOperation({ summary: 'Batch upload driver location pings' })
  async ingestPings(@Request() req: any, @Body() body: { pings: LocationPingDto[] }) {
    return this.trackingService.ingestPings(req.user.id, body.pings);
  }

  @Get('live')
  @Roles('super_admin', 'dispatcher')
  @ApiOperation({ summary: 'Get live coordinates of active fleet for Admin map' })
  async getLiveFleet() {
    return this.trackingService.getLiveFleet();
  }
}
