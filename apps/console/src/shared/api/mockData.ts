import {
  Driver,
  Vehicle,
  Shipment,
  FleetAlert,
  AuditLogItem,
  PendingApproval,
  AppRelease,
  TeamMember,
  CITY_NAMES,
} from './types';

// Clean production zero-state defaults (All live data is loaded directly from database)
export const INITIAL_DRIVERS: Driver[] = [];
export const INITIAL_VEHICLES: Vehicle[] = [];
export const INITIAL_SHIPMENTS: Shipment[] = [];
export const INITIAL_ALERTS: FleetAlert[] = [];
export const INITIAL_APPROVALS: PendingApproval[] = [];
export const INITIAL_AUDIT: AuditLogItem[] = [];
export const INITIAL_RELEASES: AppRelease[] = [];
export const INITIAL_TEAM: TeamMember[] = [];

export { CITY_NAMES };
