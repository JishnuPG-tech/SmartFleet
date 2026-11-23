export type UserRole = 'super_admin' | 'dispatcher';

export interface UserSession {
  email: string;
  name: string;
  roleTitle: string;
  role: UserRole;
  isOwner: boolean;
}

export type ShipmentStatus =
  | 'Created'
  | 'Assigned'
  | 'Accepted'
  | 'Reached Pickup'
  | 'Loaded'
  | 'In Transit'
  | 'Reached Drop'
  | 'Delivered'
  | 'Cancelled'
  | 'Rejected'
  | 'Failed';

export type DriverStatus =
  | 'Available'
  | 'On Trip'
  | 'On Leave'
  | 'Offline'
  | 'Inactive';

export type VehicleStatus =
  | 'Available'
  | 'On Trip'
  | 'Maintenance';

export interface Driver {
  id: string;
  name: string;
  phone: string;
  status: DriverStatus;
  lic: string;
  licExp: string; // ISO date
  onTime: number; // percentage
  trips: number;
  vehicle: string; // vehicle id
  version: string;
  emergencyContact?: string;
  avatarUrl?: string;
}

export interface Vehicle {
  id: string;
  reg: string;
  type: 'Lorry' | 'Mini truck' | 'Container' | 'Pickup';
  cap: number; // tonnes
  status: VehicleStatus;
  driver: string; // driver id
  docExp: string; // ISO date
  km: number;
}

export interface Shipment {
  id: string;
  from: string;
  to: string;
  pAddr?: string;
  dAddr?: string;
  goods: string;
  qty: number;
  wt: number; // tonnes
  status: ShipmentStatus;
  sched: string; // ISO datetime
  driver: string | null;
  vehicle: string | null;
  prog: number; // 0 to 1
  delayed: boolean;
  note?: string;
  consignee: string;
  cphone?: string;
  version: number; // ETag concurrency
  podPhotoUrl?: string;
  receivedBy?: string;
}

export type AlertSeverity = 'critical' | 'serious' | 'warning' | 'info';

export interface FleetAlert {
  id: string;
  type: 'Critical' | 'Rejected' | 'Delayed' | 'Pending' | 'Expiring' | 'Stale';
  severity: AlertSeverity;
  ic: 'sos' | 'x' | 'clock' | 'warn' | 'doc';
  title: string;
  sub: string;
  act: string;
  sid?: string;
  done?: boolean;
  createdAt?: string;
}

export interface AuditLogItem {
  id: string;
  t: string; // ISO timestamp
  who: string;
  a: string; // action
  e: string; // entity
  d: string; // details
}

export interface PendingApproval {
  id: string;
  name: string;
  phone: string;
  lic: string;
  type: string;
  exp: string;
  veh: string;
  emg: string;
  at: string;
}

export interface AppRelease {
  id: string;
  flavor: 'direct' | 'play';
  versionCode: number;
  versionName: string;
  minSupportedVersionCode: number;
  fileSizeMb: number;
  sha256: string;
  releaseNotesEn: string;
  releaseNotesMl: string;
  rolloutPercentage: number;
  isHalted: boolean;
  createdAt: string;
}

export interface TeamMember {
  id: string;
  name: string;
  email: string;
  phone: string;
  role: UserRole;
  isActive: boolean;
  lastLogin: string;
  createdAt: string;
}

export const KERALA_CITIES: Record<string, [number, number]> = {
  Kasaragod: [74.99, 12.5],
  Kannur: [75.37, 11.87],
  Wayanad: [76.08, 11.61],
  Kozhikode: [75.78, 11.26],
  Malappuram: [76.07, 11.07],
  Palakkad: [76.65, 10.78],
  Thrissur: [76.21, 10.53],
  Kochi: [76.27, 9.93],
  Idukki: [77.0, 9.95],
  Kottayam: [76.52, 9.59],
  Alappuzha: [76.33, 9.49],
  Kollam: [76.6, 8.89],
  Thiruvananthapuram: [76.94, 8.52],
};

export const CITY_NAMES = Object.keys(KERALA_CITIES);

export const SHIPMENT_FLOW: ShipmentStatus[] = [
  'Created',
  'Assigned',
  'Accepted',
  'Reached Pickup',
  'Loaded',
  'In Transit',
  'Reached Drop',
  'Delivered',
];

export const ACTIVE_STATUSES: ShipmentStatus[] = [
  'Accepted',
  'Reached Pickup',
  'Loaded',
  'In Transit',
  'Reached Drop',
];
