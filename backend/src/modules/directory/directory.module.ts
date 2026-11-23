import { Module } from '@nestjs/common';
import { DirectoryService } from './directory.service';
import { DirectoryController } from './directory.controller';
import { SupabaseService } from '../../common/supabase.service';

@Module({
  controllers: [DirectoryController],
  providers: [DirectoryService, SupabaseService],
  exports: [DirectoryService],
})
export class DirectoryModule {}
