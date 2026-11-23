import { createClient } from '@supabase/supabase-js';

const SUPABASE_URL =
  (import.meta as any).env?.VITE_SUPABASE_URL || 'https://easqjokyjulfuozdxiyp.supabase.co';
const SUPABASE_ANON_KEY =
  (import.meta as any).env?.VITE_SUPABASE_ANON_KEY ||
  'eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6ImVhc3Fqb2t5anVsZnVvemR4aXlwIiwicm9sZSI6ImFub24iLCJpYXQiOjE3OTUzNDc0OTMsImV4cCI6MjExMDkyMzQ5M30.Xt9-SBVQxCcmzpUdZ8frQhVSyGeLS7Dy0zPiOzfgnmU';

export const supabase = createClient(SUPABASE_URL, SUPABASE_ANON_KEY);

export async function fetchLiveReleases() {
  try {
    const { data, error } = await supabase
      .from('app_releases')
      .select('*')
      .order('version_code', { ascending: false });

    if (error) {
      console.warn('Could not load releases from Supabase:', error.message);
      return null;
    }
    return data;
  } catch (err) {
    console.warn('Supabase fetch failed:', err);
    return null;
  }
}

export async function publishApkRelease(releasePayload: {
  flavor: string;
  version_code: number;
  version_name: string;
  min_supported_version_code: number;
  apk_url: string;
  sha256_checksum: string;
  file_size_bytes: number;
  release_notes_en: string;
  release_notes_ml: string;
  rollout_percentage: number;
}) {
  try {
    const { data, error } = await supabase
      .from('app_releases')
      .upsert(releasePayload, { onConflict: 'flavor,version_code' })
      .select();

    if (error) throw error;
    return data;
  } catch (err) {
    console.warn('Supabase publish release error:', err);
    throw err;
  }
}
