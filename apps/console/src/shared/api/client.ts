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
  ShipmentStatus,
  DriverStatus,
  CITY_NAMES,
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
import {
  loginAdminApi,
  getAuthToken,
  fetchLiveShipments,
  createShipmentApi,
  assignShipmentApi,
  fetchLiveDrivers,
  fetchLiveVehicles,
  fetchPendingApprovalsApi,
  approveDriverApi,
  rejectDriverApi,
  publishApkRelease,
  fetchLiveReleases,
  setupSupabaseRealtime,
} from './supabase';

function toFrontendStatus(dbStatus: string): ShipmentStatus {
  const s = (dbStatus || '').toLowerCase();
  const map: Record<string, ShipmentStatus> = {
    created: 'Created',
    assigned: 'Assigned',
    accepted: 'Accepted',
    reached_pickup: 'Reached Pickup',
    loaded: 'Loaded',
    in_transit: 'In Transit',
    reached_drop: 'Reached Drop',
    delivered: 'Delivered',
    cancelled: 'Cancelled',
    rejected: 'Rejected',
    failed: 'Failed',
  };
  return map[s] || 'Created';
}

function toDriverStatus(dbAvailability: string): DriverStatus {
  const a = (dbAvailability || '').toLowerCase();
  const map: Record<string, DriverStatus> = {
    available: 'Available',
    on_trip: 'On Trip',
    on_leave: 'On Leave',
    offline: 'Offline',
  };
  return map[a] || 'Available';
}

function toVehicleType(dbType: string): 'Lorry' | 'Mini truck' | 'Container' | 'Pickup' {
  const t = (dbType || '').toLowerCase();
  if (t.includes('container')) return 'Container';
  if (t.includes('mini')) return 'Mini truck';
  if (t.includes('pickup')) return 'Pickup';
  return 'Lorry';
}

function extractCity(address: string, fallback: string): string {
  if (!address) return fallback;
  for (const city of CITY_NAMES) {
    if (address.toLowerCase().includes(city.toLowerCase())) {
      return city;
    }
  }
  return fallback;
}

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
  private isSyncing = false;

  constructor() {
    this.initRealtimeAndBackend();
  }

  async initRealtimeAndBackend() {
    // 1. Ensure backend session
    try {
      const existingToken = getAuthToken();
      if (!existingToken) {
        await loginAdminApi('jishnupg2005@gmail.com', 'Jishnu2005');
      }
    } catch (e) {
      console.warn('Initial admin login check:', e);
    }

    // 2. Initial fetch of live database rows
    await this.syncAllFromBackendAndDatabase();

    // 3. Setup Supabase Realtime listener on tables
    try {
      setupSupabaseRealtime((table, payload) => {
        this.log(
          `Realtime ${payload.eventType || 'UPDATE'}`,
          table,
          `Record: ${payload.new?.id || payload.old?.id || 'live'}`
        );
        this.syncAllFromBackendAndDatabase();
      });
    } catch (err) {
      console.warn('Realtime subscription error:', err);
    }
  }

  // Sync all live rows directly from Supabase / Backend API
  async syncAllFromBackendAndDatabase() {
    if (this.isSyncing) return;
    this.isSyncing = true;
    try {
      const [rawShipments, rawDrivers, rawVehicles, rawApprovals, rawReleases] = await Promise.all([
        fetchLiveShipments().catch((e) => {
          console.warn('fetchLiveShipments failed:', e);
          return [];
        }),
        fetchLiveDrivers().catch((e) => {
          console.warn('fetchLiveDrivers failed:', e);
          return [];
        }),
        fetchLiveVehicles().catch((e) => {
          console.warn('fetchLiveVehicles failed:', e);
          return [];
        }),
        fetchPendingApprovalsApi().catch((e) => {
          console.warn('fetchPendingApprovalsApi failed:', e);
          return [];
        }),
        fetchLiveReleases().catch((e) => {
          console.warn('fetchLiveReleases failed:', e);
          return [];
        }),
      ]);

      if (Array.isArray(rawDrivers)) {
        this.drivers = rawDrivers.map((d: any) => {
          const status = toDriverStatus(d.availability);
          return {
            id: d.user_id,
            name: d.users?.name || 'Driver',
            phone: d.users?.phone || '+919847000000',
            status,
            lic: d.licence_no || 'KL07 20180012345',
            licExp: d.licence_expiry || '2028-12-31',
            onTime: 96,
            trips: status === 'On Trip' ? 14 : 9,
            vehicle: '',
            version: '2.1.0',
            emergencyContact: d.emergency_contact_phone || undefined,
          };
        });
      }

      if (Array.isArray(rawVehicles)) {
        this.vehicles = rawVehicles.map((v: any) => ({
          id: v.id,
          reg: v.reg_no,
          type: toVehicleType(v.type),
          cap: v.capacity_kg ? +(v.capacity_kg / 1000).toFixed(1) : 10,
          status: (v.status === 'on_trip' ? 'On Trip' : 'Available') as Vehicle['status'],
          driver: v.default_driver_id || '',
          docExp: '2027-12-31',
          km: 24500,
        }));
      }

      if (Array.isArray(rawShipments)) {
        this.shipments = rawShipments.map((s: any) => {
          const fromCity = extractCity(s.pickup_address, 'Kochi');
          const toCity = extractCity(s.drop_address, 'Kozhikode');
          const frontendStatus = toFrontendStatus(s.status);
          const prog =
            frontendStatus === 'In Transit'
              ? 0.45
              : frontendStatus === 'Delivered'
              ? 1
              : frontendStatus === 'Reached Drop'
              ? 0.95
              : 0.1;
          return {
            id: s.code || s.id,
            from: fromCity,
            to: toCity,
            pAddr: s.pickup_address,
            dAddr: s.drop_address,
            goods: s.goods_description || 'Freight Cargo',
            qty: 1,
            wt: s.weight_kg ? +(s.weight_kg / 1000).toFixed(1) : 5,
            status: frontendStatus,
            sched: s.scheduled_at || s.created_at || new Date().toISOString(),
            driver: s.driver_id || s.driver?.id || null,
            vehicle: s.vehicle_id || s.vehicle?.id || null,
            prog,
            delayed: false,
            consignee: s.drop_contact_name || 'Consignee',
            cphone: s.drop_contact_phone || '',
            version: s.version || 1,
          };
        });
      }

      if (Array.isArray(rawApprovals)) {
        this.approvals = rawApprovals.map((p: any) => ({
          id: p.user_id,
          name: p.users?.name || 'Driver Applicant',
          phone: p.users?.phone || '+919447000000',
          lic: p.licence_no || 'KL05 20190099887',
          type: p.licence_type || 'LMV-Transport',
          exp: p.licence_expiry || '2029-08-20',
          veh: p.vehicle_ownership || 'Company',
          emg: `${p.emergency_contact_name || ''} (${p.emergency_contact_phone || ''})`,
          at: p.created_at || new Date().toISOString(),
        }));
      }

      if (Array.isArray(rawReleases) && rawReleases.length > 0) {
        this.releases = rawReleases.map((r: any) => ({
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
      }

      // Compute dynamic real alerts
      const liveAlerts: FleetAlert[] = [];
      this.approvals.forEach((app) => {
        liveAlerts.push({
          id: 'alt-app-' + app.id,
          type: 'Pending',
          severity: 'warning',
          ic: 'doc',
          title: 'Driver KYC Verification Required',
          sub: `${app.name} (${app.lic}) submitted license for verification`,
          act: 'Verify Applicant',
        });
      });

      this.shipments
        .filter((s) => s.status === 'Created' && !s.driver)
        .forEach((s) => {
          liveAlerts.push({
            id: 'alt-unassigned-' + s.id,
            type: 'Critical',
            severity: 'serious',
            ic: 'warn',
            title: `Unassigned Shipment ${s.id}`,
            sub: `${s.from} → ${s.to} awaiting driver & vehicle assignment`,
            act: 'Assign Fleet',
            sid: s.id,
          });
        });

      const activeInTransit = this.shipments.filter((s) => s.status === 'In Transit');
      if (activeInTransit.length > 0) {
        const top = activeInTransit[0];
        const driverName = this.drivers.find((d) => d.id === top.driver)?.name || 'Driver';
        liveAlerts.push({
          id: 'alt-transit-' + top.id,
          type: 'Pending',
          severity: 'info',
          ic: 'clock',
          title: 'Active Shipment In Transit',
          sub: `${top.id} en route to ${top.to} (${driverName})`,
          act: 'Track Live',
          sid: top.id,
        });
      }

      this.alerts = liveAlerts;
      this.notify();
    } catch (err) {
      console.warn('Sync all error:', err);
    } finally {
      this.isSyncing = false;
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
    const tempCode = `TC-2026-${String(Math.floor(100000 + Math.random() * 900000))}`;
    const newShipment: Shipment = {
      ...data,
      id: tempCode,
      prog: 0,
      version: 1,
    };
    this.shipments.unshift(newShipment);
    this.log('Created shipment', tempCode, `${newShipment.from} → ${newShipment.to}, ${newShipment.wt} t`);
    this.notify();

    // Async push to backend & database
    createShipmentApi({
      from: newShipment.from,
      to: newShipment.to,
      pickupAddress: newShipment.pAddr || `${newShipment.from} Industrial Hub`,
      dropAddress: newShipment.dAddr || `${newShipment.to} Logistics Yard`,
      goods: newShipment.goods,
      wt: newShipment.wt,
      sched: newShipment.sched,
      consignee: newShipment.consignee,
      cphone: newShipment.cphone,
      driver: newShipment.driver,
      vehicle: newShipment.vehicle,
      status: newShipment.status,
    })
      .then((created) => {
        if (created?.code && created.code !== tempCode) {
          newShipment.id = created.code;
          this.notify();
        }
      })
      .catch((err) => {
        console.warn('Backend createShipment error:', err);
      });

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

    // Async push to backend & database
    assignShipmentApi(id, driverId, vehicleId).catch((err) => {
      console.warn('Backend assignShipment error:', err);
    });

    return s;
  }

  // Approvals
  approveDriver(applicantId: string, verificationNote: string): Driver {
    const pIdx = this.approvals.findIndex((p) => p.id === applicantId);
    const p = pIdx !== -1 ? this.approvals[pIdx] : null;

    const newDriver: Driver = {
      id: applicantId,
      name: p?.name || 'Approved Driver',
      phone: p?.phone || '',
      lic: p?.lic || '',
      licExp: p?.exp || new Date(Date.now() + 5 * 365 * 86400000).toISOString(),
      status: 'Available',
      onTime: 95,
      trips: 0,
      vehicle: '',
      version: '2.1.0',
      emergencyContact: p?.emg,
    };

    if (pIdx !== -1) {
      this.approvals.splice(pIdx, 1);
    }
    this.drivers.push(newDriver);
    this.log('Approved driver', newDriver.name, `Note: ${verificationNote || 'Licence verified'}`);
    this.notify();

    approveDriverApi(applicantId)
      .then(() => this.syncAllFromBackendAndDatabase())
      .catch((err) => console.warn('Backend approve driver error:', err));

    return newDriver;
  }

  rejectDriver(applicantId: string, reason: string) {
    const pIdx = this.approvals.findIndex((p) => p.id === applicantId);
    if (pIdx !== -1) {
      const p = this.approvals[pIdx];
      this.approvals.splice(pIdx, 1);
      this.log('Rejected driver', p.name, `Reason: ${reason}`);
      this.notify();
    }

    rejectDriverApi(applicantId, reason)
      .then(() => this.syncAllFromBackendAndDatabase())
      .catch((err) => console.warn('Backend reject driver error:', err));
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
