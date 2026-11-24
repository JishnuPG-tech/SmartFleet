import { createClient } from '@supabase/supabase-js';

const SUPABASE_URL =
  (import.meta as any).env?.VITE_SUPABASE_URL || 'https://easqjokyjulfuozdxiyp.supabase.co';
const SUPABASE_KEY =
  (import.meta as any).env?.VITE_SUPABASE_SERVICE_ROLE_KEY ||
  (import.meta as any).env?.VITE_SUPABASE_ANON_KEY ||
  'eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6ImVhc3Fqb2t5anVsZnVvemR4aXlwIiwicm9sZSI6InNlcnZpY2Vfcm9sZSIsImlhdCI6MTc5NTM0NzQ5MywiZXhwIjoyMTEwOTIzNDkzfQ.K2CsGCBI8FuphkYW69QvtPdYd2eL2GmMc-5huncj0BE';

export const supabase = createClient(SUPABASE_URL, SUPABASE_KEY);

export const API_BASE =
  (import.meta as any).env?.VITE_API_URL ||
  'https://easqjokyjulfuozdxiyp.supabase.co/functions/v1/api';

export function getAuthToken(): string | null {
  return typeof localStorage !== 'undefined' ? localStorage.getItem('tc_token') : null;
}

export function setAuthToken(token: string) {
  if (typeof localStorage !== 'undefined') {
    localStorage.setItem('tc_token', token);
  }
}

export function clearAuthToken() {
  if (typeof localStorage !== 'undefined') {
    localStorage.removeItem('tc_token');
  }
}

async function requestApi(path: string, options: RequestInit = {}) {
  const token = getAuthToken();
  const headers: Record<string, string> = {
    'Content-Type': 'application/json',
    ...(options.headers as Record<string, string>),
  };
  if (token) {
    headers['Authorization'] = `Bearer ${token}`;
  }

  const url = `${API_BASE}${path.startsWith('/') ? path : '/' + path}`;
  const res = await fetch(url, { ...options, headers });
  if (!res.ok) {
    const errorBody = await res.json().catch(() => ({ message: res.statusText }));
    throw new Error(errorBody.message || `Request failed with status ${res.status}`);
  }
  return res.json();
}

// 1. Admin Authentication API
export async function loginAdminApi(email: string, pass: string) {
  try {
    const data = await requestApi('/auth/login', {
      method: 'POST',
      body: JSON.stringify({ email, password: pass }),
    });
    if (data.accessToken) {
      setAuthToken(data.accessToken);
    }
    return data;
  } catch (err) {
    console.warn('API login failed, checking fallback direct login:', err);
    throw err;
  }
}

// 2. Live Shipments API
export async function fetchLiveShipments() {
  try {
    const data = await requestApi('/shipments');
    return data;
  } catch {
    // Direct Supabase fallback
    const { data } = await supabase
      .from('shipments')
      .select(`
        *,
        driver:driver_id (id, name, phone),
        vehicle:vehicle_id (id, reg_no, type, capacity_kg)
      `)
      .order('created_at', { ascending: false });
    return data || [];
  }
}

export async function createShipmentApi(payload: any) {
  try {
    return await requestApi('/shipments', {
      method: 'POST',
      body: JSON.stringify(payload),
    });
  } catch {
    const code = `TC-${new Date().getFullYear()}-${Math.floor(100000 + Math.random() * 900000)}`;
    const { data, error } = await supabase
      .from('shipments')
      .insert({
        code,
        status: payload.status?.toLowerCase() || 'created',
        pickup_address: payload.pickupAddress || `${payload.from} Industrial Estate`,
        drop_address: payload.dropAddress || `${payload.to} Market Yard`,
        goods_description: payload.goodsDescription || payload.goods,
        weight_kg: Math.round((payload.weightKg || payload.wt || 5) * 1000),
        scheduled_at: payload.scheduledAt || payload.sched || new Date().toISOString(),
        driver_id: payload.driverId || payload.driver || null,
        vehicle_id: payload.vehicleId || payload.vehicle || null,
        pickup_contact_name: 'Dispatch Manager',
        pickup_contact_phone: '+919999999999',
        drop_contact_name: payload.consignee || 'Consignee',
        drop_contact_phone: payload.cphone || '+919847000000',
      })
      .select()
      .single();
    if (error) throw error;
    return data;
  }
}

export async function assignShipmentApi(shipmentId: string, driverId: string, vehicleId: string) {
  try {
    return await requestApi(`/shipments/${shipmentId}/assign`, {
      method: 'POST',
      body: JSON.stringify({ driverId, vehicleId }),
    });
  } catch {
    const { data, error } = await supabase
      .from('shipments')
      .update({
        driver_id: driverId,
        vehicle_id: vehicleId,
        status: 'assigned',
        updated_at: new Date().toISOString(),
      })
      .eq('id', shipmentId)
      .select()
      .single();
    if (error) throw error;
    return data;
  }
}

// 3. Live Drivers & Vehicles API
export async function fetchLiveDrivers() {
  try {
    return await requestApi('/drivers');
  } catch {
    const { data } = await supabase.from('drivers').select(`
      user_id, licence_no, licence_type, licence_expiry, availability, approval_status,
      users:user_id (id, name, phone, status, language, theme)
    `);
    return data || [];
  }
}

export async function fetchLiveVehicles() {
  try {
    return await requestApi('/vehicles');
  } catch {
    const { data } = await supabase.from('vehicles').select('*');
    return data || [];
  }
}

export async function fetchPendingApprovalsApi() {
  try {
    return await requestApi('/admin/drivers/pending');
  } catch {
    const { data } = await supabase
      .from('drivers')
      .select(`
        user_id, licence_no, licence_type, licence_expiry, emergency_contact_name,
        emergency_contact_phone, vehicle_ownership, created_at,
        users:user_id (id, name, phone)
      `)
      .eq('approval_status', 'pending');
    return data || [];
  }
}

export async function approveDriverApi(driverId: string) {
  try {
    return await requestApi(`/admin/drivers/${driverId}/approve`, { method: 'POST' });
  } catch {
    const { data, error } = await supabase
      .from('drivers')
      .update({ approval_status: 'approved', availability: 'available' })
      .eq('user_id', driverId);
    if (error) throw error;
    return data;
  }
}

export async function rejectDriverApi(driverId: string, reason: string) {
  try {
    return await requestApi(`/admin/drivers/${driverId}/reject`, {
      method: 'POST',
      body: JSON.stringify({ reason }),
    });
  } catch {
    const { data, error } = await supabase
      .from('drivers')
      .update({ approval_status: 'rejected', rejection_reason: reason })
      .eq('user_id', driverId);
    if (error) throw error;
    return data;
  }
}

// 4. Live Releases & APK Updates
export async function fetchLiveReleases() {
  try {
    const { data, error } = await supabase
      .from('app_releases')
      .select('*')
      .order('version_code', { ascending: false });

    if (error) {
      console.warn('Could not load releases from Supabase:', error.message);
      return [];
    }
    return data || [];
  } catch (err) {
    console.warn('Supabase fetch failed:', err);
    return [];
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

// 5. Supabase Realtime Listener Setup
export function setupSupabaseRealtime(onTableChange: (table: string, payload: any) => void) {
  const channel = supabase
    .channel('public:fleet_realtime')
    .on('postgres_changes', { event: '*', schema: 'public', table: 'shipments' }, (payload) => {
      onTableChange('shipments', payload);
    })
    .on('postgres_changes', { event: '*', schema: 'public', table: 'drivers' }, (payload) => {
      onTableChange('drivers', payload);
    })
    .on('postgres_changes', { event: '*', schema: 'public', table: 'vehicles' }, (payload) => {
      onTableChange('vehicles', payload);
    })
    .on('postgres_changes', { event: '*', schema: 'public', table: 'app_releases' }, (payload) => {
      onTableChange('app_releases', payload);
    })
    .on('postgres_changes', { event: '*', schema: 'public', table: 'location_pings' }, (payload) => {
      onTableChange('location_pings', payload);
    })
    .subscribe();

  return () => {
    supabase.removeChannel(channel);
  };
}
