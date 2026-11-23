import { Injectable, NotFoundException, BadRequestException, Logger } from '@nestjs/common';
import { SupabaseService } from '../../common/supabase.service';

@Injectable()
export class ShipmentsService {
  private readonly logger = new Logger(ShipmentsService.name);

  // Allowed transitions
  private readonly validTransitions: Record<string, string[]> = {
    created: ['assigned', 'cancelled'],
    assigned: ['accepted', 'rejected', 'assigned', 'cancelled'],
    accepted: ['reached_pickup', 'cancelled'],
    reached_pickup: ['loaded', 'cancelled'],
    loaded: ['in_transit', 'cancelled'],
    in_transit: ['reached_drop', 'failed', 'cancelled'],
    reached_drop: ['delivered', 'failed'],
    delivered: [],
    cancelled: [],
    failed: ['assigned'],
  };

  constructor(private supabaseService: SupabaseService) {}

  // List shipments with role awareness
  async getShipments(userId: string, role: string, status?: string) {
    const supabase = this.supabaseService.getClient();
    let query = supabase
      .from('shipments')
      .select(`
        *,
        driver:driver_id (id, name, phone),
        vehicle:vehicle_id (id, reg_no, type, capacity_kg)
      `)
      .order('scheduled_at', { ascending: false });

    if (role === 'driver') {
      query = query.eq('driver_id', userId);
    }
    if (status) {
      query = query.eq('status', status);
    }

    const { data, error } = await query;
    if (error) throw new BadRequestException(error.message);
    return data;
  }

  // Get shipment by ID with event timeline and proof
  async getShipmentById(id: string) {
    const supabase = this.supabaseService.getClient();
    const { data: shipment, error } = await supabase
      .from('shipments')
      .select(`
        *,
        driver:driver_id (id, name, phone),
        vehicle:vehicle_id (id, reg_no, type, capacity_kg),
        events:shipment_events (*),
        proof:proofs_of_delivery (*)
      `)
      .eq('id', id)
      .single();

    if (error || !shipment) throw new NotFoundException('Shipment not found');
    return shipment;
  }

  // Create Shipment (Admin)
  async createShipment(dto: any, creatorId: string) {
    const supabase = this.supabaseService.getClient();
    const code = `TC-${new Date().getFullYear()}-${Math.floor(100000 + Math.random() * 900000)}`;

    const { data: shipment, error } = await supabase
      .from('shipments')
      .insert({
        code,
        status: 'created',
        pickup_address: dto.pickupAddress,
        pickup_contact_name: dto.pickupContactName,
        pickup_contact_phone: dto.pickupContactPhone,
        drop_address: dto.dropAddress,
        drop_contact_name: dto.dropContactName,
        drop_contact_phone: dto.dropContactPhone,
        goods_description: dto.goodsDescription,
        weight_kg: dto.weightKg,
        scheduled_at: dto.scheduledAt || new Date().toISOString(),
        expected_at: dto.expectedAt,
        driver_id: dto.driverId || null,
        vehicle_id: dto.vehicleId || null,
        created_by: creatorId,
      })
      .select()
      .single();

    if (error) throw new BadRequestException(error.message);

    // Record created event
    await this.recordEvent(shipment.id, 'created', creatorId, 'Shipment created');
    return shipment;
  }

  // Assign Driver & Vehicle
  async assignShipment(id: string, driverId: string, vehicleId: string, actorId: string) {
    const supabase = this.supabaseService.getClient();
    const shipment = await this.getShipmentById(id);

    this.validateTransition(shipment.status, 'assigned');

    const { data: updated, error } = await supabase
      .from('shipments')
      .update({
        driver_id: driverId,
        vehicle_id: vehicleId,
        status: 'assigned',
        updated_at: new Date().toISOString(),
      })
      .eq('id', id)
      .select()
      .single();

    if (error) throw new BadRequestException(error.message);

    await this.recordEvent(id, 'assigned', actorId, `Assigned to driver ${driverId}`);
    return updated;
  }

  // Driver Accept
  async acceptShipment(id: string, driverId: string) {
    const shipment = await this.getShipmentById(id);
    if (shipment.driver_id !== driverId) {
      throw new BadRequestException('You are not the assigned driver for this shipment');
    }
    this.validateTransition(shipment.status, 'accepted');

    const supabase = this.supabaseService.getClient();
    const { data: updated, error } = await supabase
      .from('shipments')
      .update({ status: 'accepted', updated_at: new Date().toISOString() })
      .eq('id', id)
      .select()
      .single();

    if (error) throw new BadRequestException(error.message);
    await this.recordEvent(id, 'accepted', driverId, 'Driver accepted assignment');
    return updated;
  }

  // Driver Reject
  async rejectShipment(id: string, driverId: string, reason: string) {
    const shipment = await this.getShipmentById(id);
    if (shipment.driver_id !== driverId) {
      throw new BadRequestException('You are not the assigned driver for this shipment');
    }
    this.validateTransition(shipment.status, 'rejected');

    const supabase = this.supabaseService.getClient();
    const { data: updated, error } = await supabase
      .from('shipments')
      .update({ status: 'rejected', updated_at: new Date().toISOString() })
      .eq('id', id)
      .select()
      .single();

    if (error) throw new BadRequestException(error.message);
    await this.recordEvent(id, 'rejected', driverId, `Rejected: ${reason}`);
    return updated;
  }

  // Milestone Update (reached_pickup, loaded, in_transit, reached_drop)
  async updateMilestone(id: string, milestone: string, driverId: string, note?: string) {
    const shipment = await this.getShipmentById(id);
    if (shipment.driver_id !== driverId) {
      throw new BadRequestException('Unauthorized driver for this trip');
    }
    this.validateTransition(shipment.status, milestone);

    const supabase = this.supabaseService.getClient();
    const { data: updated, error } = await supabase
      .from('shipments')
      .update({ status: milestone, updated_at: new Date().toISOString() })
      .eq('id', id)
      .select()
      .single();

    if (error) throw new BadRequestException(error.message);
    await this.recordEvent(id, milestone, driverId, note || `Milestone: ${milestone}`);
    return updated;
  }

  // Complete Delivery with Proof (POD)
  async completeDelivery(id: string, driverId: string, pod: { photoUrls: string[]; receiverName: string; signatureUrl?: string }) {
    const shipment = await this.getShipmentById(id);
    if (shipment.driver_id !== driverId) {
      throw new BadRequestException('Unauthorized driver');
    }
    this.validateTransition(shipment.status, 'delivered');

    const supabase = this.supabaseService.getClient();

    // 1. Record Proof of Delivery
    await supabase.from('proofs_of_delivery').insert({
      shipment_id: id,
      photo_urls: pod.photoUrls,
      receiver_name: pod.receiverName,
      signature_url: pod.signatureUrl || null,
    });

    // 2. Update status to delivered
    const { data: updated, error } = await supabase
      .from('shipments')
      .update({ status: 'delivered', updated_at: new Date().toISOString() })
      .eq('id', id)
      .select()
      .single();

    if (error) throw new BadRequestException(error.message);
    await this.recordEvent(id, 'delivered', driverId, `Delivered to ${pod.receiverName}`);
    return updated;
  }

  // Helper: Transition Validator
  private validateTransition(current: string, next: string) {
    const allowed = this.validTransitions[current] || [];
    if (!allowed.includes(next)) {
      throw new BadRequestException(`Invalid state transition from '${current}' to '${next}'`);
    }
  }

  // Helper: Event Logger
  private async recordEvent(shipmentId: string, status: string, actorId: string, note: string) {
    const supabase = this.supabaseService.getClient();
    await supabase.from('shipment_events').insert({
      shipment_id: shipmentId,
      status,
      actor_id: actorId,
      note,
    });
  }
}
