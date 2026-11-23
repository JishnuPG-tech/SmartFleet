import { Module } from '@nestjs/common';
import { TrackingService } from './tracking.service';
import { TrackingController } from './tracking.controller';
import { SupabaseService } from '../../common/supabase.service';

@Module({
  controllers: [TrackingController],
  providers: [TrackingService, SupabaseService],
  exports: [TrackingService],
})
export class TrackingModule {}
