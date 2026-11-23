import {
  Driver,
  Vehicle,
  Shipment,
  FleetAlert,
  AuditLogItem,
  PendingApproval,
  AppRelease,
  TeamMember,
  UserSession,
} from './types';
import {
  INITIAL_DRIVERS,
  INITIAL_VEHICLES,
  INITIAL_SHIPMENTS,
  INITIAL_ALERTS,
  INITIAL_APPROVALS,
  INITIAL_AUDIT,
  INITIAL_RELEASES,
  INITIAL_TEAM,
} from './mockData';
import { publishApkRelease, fetchLiveReleases } from './supabase';

class FleetStore {
  drivers: Driver[] = [...INITIAL_DRIVERS];
  vehicles: Vehicle[] = [...INITIAL_VEHICLES];
  shipments: Shipment[] = [...INITIAL_SHIPMENTS];
  alerts: FleetAlert[] = [...INITIAL_ALERTS];
  approvals: PendingApproval[] = [...INITIAL_APPROVALS];
  auditLogs: AuditLogItem[] = [...INITIAL_AUDIT];
  releases: AppRelease[] = [...INITIAL_RELEASES];
  team: TeamMember[] = [
    {
      id: 'admin-jishnu',
      name: 'Jishnu PG',
      email: 'jishnupg2005@gmail.com',
      phone: '+91 99999 99999',
      role: 'super_admin',
      isActive: true,
      lastLogin: new Date().toISOString(),
      createdAt: '2026-01-01T00:00:00Z',
    },
    ...INITIAL_TEAM,
  ];

  currentUser: UserSession | null = {
    email: 'jishnupg2005@gmail.com',
    name: 'Jishnu PG',
    roleTitle: 'Owner · Super Admin',
    role: 'super_admin',
    isOwner: true,
  };

  private listeners: Set<() => void> = new Set();

  constructor() {
    this.syncLiveReleasesFromSupabase();
  }

  // Sync live releases directly from Supabase database
  async syncLiveReleasesFromSupabase() {
    const live = await fetchLiveReleases();
    if (live && live.length > 0) {
      this.releases = live.map((r: any) => ({
        id: r.id,
        flavor: r.flavor,
        versionCode: r.version_code,
        versionName: r.version_name,
        minSupportedVersionCode: r.min_supported_version_code,
        fileSizeMb: +(r.file_size_bytes / (1024 * 1024)).toFixed(1),
        sha256: r.sha256_checksum,
        releaseNotesEn: r.release_notes_en || 'Update available',
        releaseNotesMl: r.release_notes_ml || 'പുതിയ പതിപ്പ് ലഭ്യമാണ്',
        rolloutPercentage: r.rollout_percentage || 100,
        isHalted: false,
        createdAt: r.created_at,
      }));
      this.notify();
    }
  }

  subscribe(listener: () => void) {
    this.listeners.add(listener);
    return () => {
      this.listeners.delete(listener);
    };
  }

  notify() {
    this.listeners.forEach((l) => l());
  }

  log(action: string, entity: string, detail: string = '') {
    const item: AuditLogItem = {
      id: 'aud-' + Date.now(),
      t: new Date().toISOString(),
      who: this.currentUser ? this.currentUser.name : 'System',
      a: action,
      e: entity,
      d: detail,
    };
    this.auditLogs.unshift(item);
    this.notify();
  }

  // Auth
  login(email: string, role: 'super_admin' | 'dispatcher'): UserSession {
    const isOwner = role === 'super_admin';
    const isJishnu = email.toLowerCase().includes('jishnupg');
    const user: UserSession = {
      email,
      name: isJishnu ? 'Jishnu PG' : isOwner ? 'Anil Kumar' : 'Divya S',
      roleTitle: isOwner ? 'Owner · Super Admin' : 'Dispatcher',
      role,
      isOwner,
    };
    this.currentUser = user;
    this.log('Signed in', 'Operations Console', `Role: ${user.roleTitle}`);
    this.notify();
    return user;
  }

  logout() {
    if (this.currentUser) {
      this.log('Signed out', 'Operations Console');
    }
    this.currentUser = null;
    this.notify();
  }

  // Shipments
  createShipment(data: Omit<Shipment, 'id' | 'prog' | 'version'>): Shipment {
    const id = `TC-2026-${String(this.shipments.length + 131).padStart(6, '0')}`;
    const newShipment: Shipment = {
      ...data,
      id,
      prog: 0,
      version: 1,
    };
    this.shipments.unshift(newShipment);
    this.log('Created shipment', id, `${newShipment.from} → ${newShipment.to}, ${newShipment.wt} t`);
    this.notify();
    return newShipment;
  }

  updateShipment(id: string, updates: Partial<Shipment>, expectedVersion?: number): Shipment {
    const idx = this.shipments.findIndex((s) => s.id === id);
    if (idx === -1) throw new Error(`Shipment ${id} not found`);

    const current = this.shipments[idx];
    if (expectedVersion !== undefined && current.version !== expectedVersion) {
      const err: any = new Error('Conflict detected');
      err.status = 409;
      err.current = current;
      throw err;
    }

    const updated: Shipment = {
      ...current,
      ...updates,
      version: current.version + 1,
    };
    this.shipments[idx] = updated;

    this.log(
      'Updated shipment',
      id,
      Object.keys(updates)
        .map((k) => `${k}: ${(updates as any)[k]}`)
        .join(', ')
    );
    this.notify();
    return updated;
  }

  cancelShipment(id: string, reason: string): Shipment {
    const s = this.shipments.find((x) => x.id === id);
    if (!s) throw new Error(`Shipment ${id} not found`);
    s.status = 'Cancelled';
    s.note = reason;
    s.version += 1;
    this.log('Cancelled shipment', id, reason);
    this.notify();
    return s;
  }

  reassignShipment(id: string, driverId: string, vehicleId: string, reason?: string): Shipment {
    const s = this.shipments.find((x) => x.id === id);
    if (!s) throw new Error(`Shipment ${id} not found`);

    const prevDriver = s.driver;
    s.driver = driverId;
    s.vehicle = vehicleId;
    if (['Created', 'Rejected'].includes(s.status)) {
      s.status = 'Assigned';
    }
    s.version += 1;

    // Resolve any pending reject alert for this shipment
    const alert = this.alerts.find((a) => a.sid === id);
    if (alert) alert.done = true;

    this.log(
      'Reassigned shipment',
      id,
      `Driver: ${prevDriver || 'None'} → ${driverId}${reason ? ` (${reason})` : ''}`
    );
    this.notify();
    return s;
  }

  // Approvals
  approveDriver(applicantId: string, verificationNote: string): Driver {
    const pIdx = this.approvals.findIndex((p) => p.id === applicantId);
    if (pIdx === -1) throw new Error('Applicant not found');
    const p = this.approvals[pIdx];

    const newDriver: Driver = {
      id: 'D' + (this.drivers.length + 1),
      name: p.name,
      phone: p.phone,
      lic: p.lic,
      licExp: new Date(p.exp).toISOString(),
      status: 'Available',
      onTime: 92,
      trips: 0,
      vehicle: 'V' + ((this.drivers.length % this.vehicles.length) + 1),
      version: '2.1.0',
      emergencyContact: p.emg,
    };

    this.drivers.push(newDriver);
    this.approvals.splice(pIdx, 1);
    this.log('Approved driver', p.name, `Note: ${verificationNote || 'Licence verified'}`);
    this.notify();
    return newDriver;
  }

  rejectDriver(applicantId: string, reason: string) {
    const pIdx = this.approvals.findIndex((p) => p.id === applicantId);
    if (pIdx === -1) return;
    const p = this.approvals[pIdx];
    this.approvals.splice(pIdx, 1);
    this.log('Rejected driver', p.name, `Reason: ${reason}`);
    this.notify();
  }

  // Credentials
  resetDriverPin(driverId: string): string {
    const d = this.drivers.find((x) => x.id === driverId);
    if (!d) throw new Error('Driver not found');
    const pin = Math.floor(1000 + Math.random() * 9000).toString();
    this.log('Reset driver PIN', d.name, `New PIN generated`);
    this.notify();
    return pin;
  }

  revokeDriverDevice(driverId: string) {
    const d = this.drivers.find((x) => x.id === driverId);
    if (!d) throw new Error('Driver not found');
    this.log('Revoked device session', d.name);
    this.notify();
  }

  // Releases: synced directly to Supabase and broadcasts to Android APK
  async publishNewApkRelease(rel: {
    flavor: 'direct' | 'play';
    versionCode: number;
    versionName: string;
    minSupportedVersionCode: number;
    apkUrl: string;
    sha256: string;
    fileSizeMb: number;
    releaseNotesEn: string;
    releaseNotesMl: string;
    rolloutPercentage: number;
  }) {
    const newRelease: AppRelease = {
      id: 'rel-' + Date.now(),
      flavor: rel.flavor,
      versionCode: rel.versionCode,
      versionName: rel.versionName,
      minSupportedVersionCode: rel.minSupportedVersionCode,
      fileSizeMb: rel.fileSizeMb,
      sha256: rel.sha256,
      releaseNotesEn: rel.releaseNotesEn,
      releaseNotesMl: rel.releaseNotesMl,
      rolloutPercentage: rel.rolloutPercentage,
      isHalted: false,
      createdAt: new Date().toISOString(),
    };

    this.releases.unshift(newRelease);
    this.log('Published APK release', rel.versionName, `Build: ${rel.versionCode}`);
    this.notify();

    // Push to Supabase app_releases table so Android devices detect it
    try {
      await publishApkRelease({
        flavor: rel.flavor,
        version_code: rel.versionCode,
        version_name: rel.versionName,
        min_supported_version_code: rel.minSupportedVersionCode,
        apk_url: rel.apkUrl,
        sha256_checksum: rel.sha256,
        file_size_bytes: Math.round(rel.fileSizeMb * 1024 * 1024),
        release_notes_en: rel.releaseNotesEn,
        release_notes_ml: rel.releaseNotesMl,
        rollout_percentage: rel.rolloutPercentage,
      });
    } catch (e) {
      console.warn('Could not sync to Supabase table:', e);
    }
  }

  updateRollout(releaseId: string, percentage: number) {
    const rel = this.releases.find((r) => r.id === releaseId);
    if (!rel) return;
    rel.rolloutPercentage = percentage;
    this.log('Updated app rollout', rel.versionName, `New target: ${percentage}%`);
    this.notify();
  }

  toggleHaltRelease(releaseId: string) {
    const rel = this.releases.find((r) => r.id === releaseId);
    if (!rel) return;
    rel.isHalted = !rel.isHalted;
    this.log(rel.isHalted ? 'Emergency halted release' : 'Resumed release', rel.versionName);
    this.notify();
  }

  // Team
  addTeamMember(member: Omit<TeamMember, 'id' | 'createdAt' | 'lastLogin'>) {
    const newMember: TeamMember = {
      ...member,
      id: 'user-' + (this.team.length + 1),
      createdAt: new Date().toISOString(),
      lastLogin: 'Never',
    };
    this.team.push(newMember);
    this.log('Created team member', newMember.name, `Role: ${newMember.role}`);
    this.notify();
  }

  toggleTeamMember(id: string) {
    const m = this.team.find((x) => x.id === id);
    if (!m) return;
    m.isActive = !m.isActive;
    this.log(m.isActive ? 'Activated team member' : 'Deactivated team member', m.name);
    this.notify();
  }
}

export const fleetStore = new FleetStore();
