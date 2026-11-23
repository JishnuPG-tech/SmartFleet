import { Module } from '@nestjs/common';
import { ReleasesService } from './releases.service';
import { ReleasesController } from './releases.controller';
import { SupabaseService } from '../../common/supabase.service';

@Module({
  controllers: [ReleasesController],
  providers: [ReleasesService, SupabaseService],
  exports: [ReleasesService],
})
export class ReleasesModule {}
