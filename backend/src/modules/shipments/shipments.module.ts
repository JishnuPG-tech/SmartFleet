import { Module } from '@nestjs/common';
import { ShipmentsService } from './shipments.service';
import { ShipmentsController } from './shipments.controller';
import { SupabaseService } from '../../common/supabase.service';

@Module({
  controllers: [ShipmentsController],
  providers: [ShipmentsService, SupabaseService],
  exports: [ShipmentsService],
})
export class ShipmentsModule {}
