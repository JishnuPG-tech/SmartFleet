import { Injectable, BadRequestException, Logger } from '@nestjs/common';
import { SupabaseService } from '../../common/supabase.service';

export interface LocationPingDto {
  lat: number;
  lng: number;
  speedKmh?: number;
  accuracyMeters?: number;
  batteryPct?: number;
  recordedAt: string;
  shipmentId?: string;
}

@Injectable()
export class TrackingService {
  private readonly logger = new Logger(TrackingService.name);

  constructor(private supabaseService: SupabaseService) {}

  // Ingest batch location pings from driver device
  async ingestPings(driverId: string, pings: LocationPingDto[]) {
    if (!pings || pings.length === 0) return { received: 0 };

    const supabase = this.supabaseService.getClient();

    const rows = pings.map((p) => ({
      driver_id: driverId,
      shipment_id: p.shipmentId || null,
      geo_point: `POINT(${p.lng} ${p.lat})`,
      speed_kmh: p.speedKmh,
      accuracy_meters: p.accuracyMeters,
      battery_pct: p.batteryPct,
      recorded_at: p.recordedAt || new Date().toISOString(),
    }));

    // 1. Insert into location_pings
    const { error: pingError } = await supabase.from('location_pings').insert(rows);
    if (pingError) {
      this.logger.error('Failed to insert location pings', pingError);
      throw new BadRequestException(pingError.message);
    }

    // 2. Update driver's latest location in drivers table
    const latest = pings[pings.length - 1];
    await supabase
      .from('drivers')
      .update({
        current_location: `POINT(${latest.lng} ${latest.lat})`,
        last_location_at: latest.recordedAt || new Date().toISOString(),
      })
      .eq('user_id', driverId);

    return { received: pings.length, status: 'synced' };
  }

  // Get live tracking overview of all active vehicles for Admin map
  async getLiveFleet() {
    const supabase = this.supabaseService.getClient();
    const { data, error } = await supabase
      .from('drivers')
      .select(`
        user_id, availability, current_location, last_location_at,
        users:user_id (id, name, phone),
        vehicle:preferred_vehicle_id (id, reg_no, type)
      `)
      .in('availability', ['on_trip', 'available']);

    if (error) throw new BadRequestException(error.message);
    return data;
  }
}
