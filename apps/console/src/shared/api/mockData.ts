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

const TODAY = new Date('2026-11-22T00:00:00');
const addDays = (d: Date, n: number) => {
  const x = new Date(d);
  x.setDate(x.getDate() + n);
  return x;
};

// Initial Drivers
const DRIVER_NAMES = [
  'Suresh Kumar',
  'Rajan Nair',
  'Babu Thomas',
  'Mohanan P',
  'Joseph Mathew',
  'Sasidharan K',
  'Abdul Rahman',
  'Venugopal R',
  'Gopalakrishnan',
  'Shaji Varghese',
  'Haridas M',
  'Unnikrishnan',
];

const DRIVER_STATUSES: Driver['status'][] = [
  'On Trip',
  'On Trip',
  'On Trip',
  'On Trip',
  'On Trip',
  'On Trip',
  'On Trip',
  'Available',
  'Available',
  'On Leave',
  'Offline',
  'Inactive',
];

export const INITIAL_DRIVERS: Driver[] = DRIVER_NAMES.map((name, i) => ({
  id: 'D' + (i + 1),
  name,
  phone: `+91 9${(447000000 + i * 7312351).toString().slice(0, 9)}`,
  status: DRIVER_STATUSES[i],
  lic: `KL-${1000 + i * 37}/20${12 + (i % 6)}`,
  licExp: addDays(
    TODAY,
    [210, 420, 18, 640, 95, 5, 310, 780, -12, 260, 500, 140][i]
  ).toISOString(),
  onTime: Math.round(82 + (i * 3.7) % 17),
  trips: Math.round(18 + (i * 7.3) % 34),
  vehicle: 'V' + (i + 1),
  version: [
    '2.1.0',
    '2.1.0',
    '2.1.0',
    '2.0.3',
    '2.1.0',
    '2.1.0',
    '2.0.3',
    '2.1.0',
    '2.0.1',
    '2.1.0',
    '2.1.0',
    '2.0.3',
  ][i],
  emergencyContact: 'Family Member (+91 98470 12345)',
}));

// Initial Vehicles
const VEHICLE_CONFIGS: [Vehicle['type'], number][] = [
  ['Lorry', 10],
  ['Mini truck', 3],
  ['Container', 20],
  ['Pickup', 1.5],
  ['Lorry', 10],
  ['Mini truck', 3],
  ['Container', 20],
  ['Lorry', 10],
  ['Pickup', 1.5],
  ['Lorry', 16],
  ['Mini truck', 3],
  ['Lorry', 10],
];

export const INITIAL_VEHICLES: Vehicle[] = VEHICLE_CONFIGS.map(([type, cap], i) => ({
  id: 'V' + (i + 1),
  reg: `KL-${[7, 8, 11, 1, 14, 9, 7, 2, 13, 4, 5, 10][i]
    .toString()
    .padStart(2, '0')}-${'ABCDEFGHJKLM'[i]}${'ZXWVUTSRQPNM'[i]}-${1200 + i * 371}`,
  type,
  cap,
  status: [
    'On Trip',
    'On Trip',
    'On Trip',
    'On Trip',
    'On Trip',
    'On Trip',
    'On Trip',
    'Available',
    'Maintenance',
    'Available',
    'Available',
    'Available',
  ][i] as Vehicle['status'],
  driver: 'D' + (i + 1),
  docExp: addDays(
    TODAY,
    [300, 45, 200, 12, 150, 400, 90, 25, 600, 330, 210, 500][i]
  ).toISOString(),
  km: Math.round(52000 + (i * 9200) % 90000),
}));

// Initial Shipments
const GOODS_LIST = [
  'Rice bags',
  'Cement',
  'Steel rods',
  'Furniture',
  'Tiles',
  'Vegetables',
  'Plywood',
  'Cashew sacks',
  'Spices',
  'Tyres',
  'Paper reams',
  'FMCG cartons',
];

const CONSIGNEES = [
  'Mathew Traders',
  'Kerala Agro Mart',
  'Nair & Sons',
  'City Hardware',
  'Fresh Basket',
  'Malabar Stores',
];

const ST_PLAN: Shipment['status'][] = [
  'In Transit',
  'In Transit',
  'Loaded',
  'Reached Pickup',
  'Accepted',
  'In Transit',
  'Reached Drop',
  'Assigned',
  'Created',
  'Created',
  'Rejected',
  'Assigned',
  'Delivered',
  'Delivered',
  'Delivered',
  'Delivered',
  'Delivered',
  'Delivered',
  'Delivered',
  'Delivered',
  'Cancelled',
  'Delivered',
  'Failed',
  'Delivered',
];

export const INITIAL_SHIPMENTS: Shipment[] = ST_PLAN.map((st, i) => {
  const from = CITY_NAMES[(i * 3) % CITY_NAMES.length];
  let to = CITY_NAMES[(i * 5 + 4) % CITY_NAMES.length];
  if (to === from) to = CITY_NAMES[(i + 7) % CITY_NAMES.length];

  const dIdx = st === 'Created' ? null : i % 12;
  const isTerminal = st === 'Delivered' || st === 'Cancelled' || st === 'Failed';
  const sch = addDays(TODAY, isTerminal ? -1 - (i % 9) : i < 12 ? 0 : 1);
  sch.setHours(6 + ((i * 2) % 12), (i * 15) % 60);

  return {
    id: `TC-2026-${String(131 + i).padStart(6, '0')}`,
    from,
    to,
    pAddr: `${from} Industrial Estate, Gate 2`,
    dAddr: `${to} Central Market Road, Yard B`,
    goods: GOODS_LIST[i % GOODS_LIST.length],
    qty: Math.round(8 + (i * 7) % 90),
    wt: +((1 + (i * 0.9) % 8)).toFixed(1),
    status: st,
    sched: sch.toISOString(),
    driver: dIdx === null || st === 'Rejected' ? null : INITIAL_DRIVERS[dIdx].id,
    vehicle: dIdx === null || st === 'Rejected' ? null : INITIAL_VEHICLES[dIdx].id,
    prog:
      st === 'In Transit'
        ? 0.25 + ((i * 0.17) % 0.55)
        : st === 'Loaded'
        ? 0.05
        : st === 'Reached Drop'
        ? 0.97
        : 0,
    delayed: i === 1 || i === 5,
    note: st === 'Rejected' ? 'Vehicle issue' : '',
    consignee: CONSIGNEES[i % CONSIGNEES.length],
    cphone: `+91 9847${(100000 + i * 11111).toString().slice(0, 6)}`,
    version: 1,
  };
});

// Initial Alerts
export const INITIAL_ALERTS: FleetAlert[] = [
  {
    id: 'a1',
    type: 'Critical',
    severity: 'critical',
    ic: 'sos',
    title: 'SOS from Rajan Nair',
    sub: 'TC-2026-000132 · near Thrissur · 4 min ago',
    act: 'Call driver',
    sid: 'TC-2026-000132',
    createdAt: new Date(Date.now() - 4 * 60000).toISOString(),
  },
  {
    id: 'a2',
    type: 'Rejected',
    severity: 'critical',
    ic: 'x',
    title: 'Job rejected by Mohanan P',
    sub: 'TC-2026-000141 · “Vehicle issue”',
    act: 'Reassign',
    sid: 'TC-2026-000141',
    createdAt: new Date(Date.now() - 15 * 60000).toISOString(),
  },
  {
    id: 'a3',
    type: 'Delayed',
    severity: 'serious',
    ic: 'clock',
    title: 'TC-2026-000136 running 42 min late',
    sub: 'Sasidharan K · expected delivery passed',
    act: 'View',
    sid: 'TC-2026-000136',
    createdAt: new Date(Date.now() - 42 * 60000).toISOString(),
  },
  {
    id: 'a4',
    type: 'Pending',
    severity: 'warning',
    ic: 'warn',
    title: 'No response for 12 min',
    sub: 'TC-2026-000138 assigned to Venugopal R',
    act: 'Reassign',
    sid: 'TC-2026-000138',
    createdAt: new Date(Date.now() - 12 * 60000).toISOString(),
  },
  {
    id: 'a5',
    type: 'Expiring',
    severity: 'warning',
    ic: 'doc',
    title: 'Licence expires in 5 days',
    sub: 'Sasidharan K · KL-1185/2017',
    act: 'Open',
    createdAt: new Date(Date.now() - 120 * 60000).toISOString(),
  },
];

// Initial Pending Driver Registrations
export const INITIAL_APPROVALS: PendingApproval[] = [
  {
    id: 'P1',
    name: 'Rajan Pillai',
    phone: '+91 98470 55501',
    lic: 'KL-0720110012345',
    type: 'HGMV',
    exp: '2029-03-14',
    veh: 'Owner-operator',
    emg: 'Sreeja Nair (Spouse)',
    at: '22 Nov, 9:12 pm',
  },
  {
    id: 'P2',
    name: 'Anoop Das',
    phone: '+91 99955 20318',
    lic: 'KL-1420150044120',
    type: 'LMV',
    exp: '2028-07-02',
    veh: 'Company fleet',
    emg: 'Deepa Das (Spouse)',
    at: '22 Nov, 7:40 pm',
  },
];

// Initial Audit Logs
export const INITIAL_AUDIT: AuditLogItem[] = [
  {
    id: 'aud-1',
    t: '2026-11-22T08:10:00.000Z',
    who: 'Anil Kumar',
    a: 'Approved driver',
    e: 'Haridas M',
    d: 'Licence verified against Parivahan portal',
  },
  {
    id: 'aud-2',
    t: '2026-11-22T09:30:00.000Z',
    who: 'Divya S',
    a: 'Created shipment',
    e: 'TC-2026-000131',
    d: 'Kochi → Kozhikode, 5.0 t Rice bags',
  },
];

// Initial Releases
export const INITIAL_RELEASES: AppRelease[] = [
  {
    id: 'rel-1',
    flavor: 'direct',
    versionCode: 21,
    versionName: '2.1.0',
    minSupportedVersionCode: 19,
    fileSizeMb: 14.2,
    sha256: '9f83ab4e680a6b47c0f1604a39f60bfb72a0f8ab8c73801f70d18f4a7c2c9d81',
    releaseNotesEn: 'High-frequency telemetry compression, offline POD capture sync, background battery optimization.',
    releaseNotesMl: 'ഓഫ്‌ലൈൻ പി.ഒ.ഡി സിങ്ക്, ബാറ്ററി ഒപ്റ്റിമൈസേഷൻ, ടെലിമെട്രി അപ്‌ഡേറ്റ്.',
    rolloutPercentage: 75,
    isHalted: false,
    createdAt: '2026-11-18T10:00:00Z',
  },
  {
    id: 'rel-2',
    flavor: 'direct',
    versionCode: 20,
    versionName: '2.0.3',
    minSupportedVersionCode: 18,
    fileSizeMb: 13.9,
    sha256: 'e3b0c44298fc1c149afbf4c8996fb92427ae41e4649b934ca495991b7852b855',
    releaseNotesEn: 'Geofence notification triggers and Malayalam localization fixes.',
    releaseNotesMl: 'ജിയോഫെൻസ് നോട്ടിഫിക്കേഷൻ, മലയാളം ഭാഷാ തിരുത്തലുകൾ.',
    rolloutPercentage: 100,
    isHalted: false,
    createdAt: '2026-11-05T14:30:00Z',
  },
];

// Initial Team Members
export const INITIAL_TEAM: TeamMember[] = [
  {
    id: 'user-1',
    name: 'Anil Kumar',
    email: 'anil@truckconnect.in',
    phone: '+91 98470 11000',
    role: 'super_admin',
    isActive: true,
    lastLogin: '2026-11-23T08:45:00Z',
    createdAt: '2026-01-10T00:00:00Z',
  },
  {
    id: 'user-2',
    name: 'Divya S',
    email: 'dispatch@truckconnect.in',
    phone: '+91 94471 22334',
    role: 'dispatcher',
    isActive: true,
    lastLogin: '2026-11-23T08:15:00Z',
    createdAt: '2026-03-15T00:00:00Z',
  },
];
