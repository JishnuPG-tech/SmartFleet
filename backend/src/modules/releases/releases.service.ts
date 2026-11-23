import { Injectable, NotFoundException } from '@nestjs/common';
import { SupabaseService } from '../../common/supabase.service';

@Injectable()
export class ReleasesService {
  constructor(private supabaseService: SupabaseService) {}

  async getLatestVersion(flavor: string, currentVersionCode?: number) {
    const supabase = this.supabaseService.getClient();

    const { data: release, error } = await supabase
      .from('app_releases')
      .select('*')
      .eq('flavor', flavor || 'direct')
      .order('version_code', { ascending: false })
      .limit(1)
      .single();

    if (error || !release) {
      // Default initial version if table empty
      return {
        flavor: flavor || 'direct',
        versionCode: 1,
        versionName: '1.0.0',
        minSupportedVersionCode: 1,
        apkUrl: '',
        sha256Checksum: '',
        fileSizeBytes: 0,
        releaseNotes: {
          en: 'Initial release',
          ml: 'ആദ്യ പതിപ്പ്',
        },
        hasUpdate: false,
        isForced: false,
      };
    }

    const current = currentVersionCode || 0;
    const hasUpdate = release.version_code > current;
    const isForced = current < release.min_supported_version_code;

    return {
      flavor: release.flavor,
      versionCode: release.version_code,
      versionName: release.version_name,
      minSupportedVersionCode: release.min_supported_version_code,
      apkUrl: release.apk_url,
      sha256Checksum: release.sha256_checksum,
      fileSizeBytes: release.file_size_bytes,
      releaseNotes: {
        en: release.release_notes_en,
        ml: release.release_notes_ml,
      },
      hasUpdate,
      isForced,
    };
  }

  async reportInstalledVersion(userId: string, versionCode: number, versionName: string) {
    const supabase = this.supabaseService.getClient();
    await supabase
      .from('devices')
      .update({ app_version: `${versionName} (${versionCode})`, last_seen: new Date().toISOString() })
      .eq('user_id', userId);

    return { success: true };
  }
}
