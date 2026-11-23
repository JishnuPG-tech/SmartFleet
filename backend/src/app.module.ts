import { Module } from '@nestjs/common';
import { ConfigModule } from '@nestjs/config';
import { AuthModule } from './modules/auth/auth.module';
import { DirectoryModule } from './modules/directory/directory.module';
import { ShipmentsModule } from './modules/shipments/shipments.module';
import { TrackingModule } from './modules/tracking/tracking.module';
import { ReleasesModule } from './modules/releases/releases.module';
import { SupabaseService } from './common/supabase.service';

@Module({
  imports: [
    ConfigModule.forRoot({
      isGlobal: true,
      envFilePath: ['.env', '../.env'],
    }),
    AuthModule,
    DirectoryModule,
    ShipmentsModule,
    TrackingModule,
    ReleasesModule,
  ],
  providers: [SupabaseService],
})
export class AppModule {}
