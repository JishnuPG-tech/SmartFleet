# TruckConnect Operations Console — Architectural & Execution Blueprint

**Document Version:** 1.0.0  
**Target Package:** `apps/console` (Monorepo)  
**Author:** Operations Console Lead Architect  
**Status:** Approved for Implementation  
**Companion Documents:** `docs/OPERATIONS_CONSOLE_MASTER_PROMPT.md`, `mockup/TruckConnect_Operations_Console_v2.html`, `docs/full-project-prd.md`, `docs/DECISIONS.md`

---

## 1. System Architecture & Foundation

### 1.1 Monorepo & Packaging Model
The Operations Console is implemented as an independent web application located at `apps/console` within the TruckConnect repository.

```
apps/console/
├── index.html                           # Root HTML with inline theme script (prevents FOUC)
├── vite.config.ts                       # Vite bundler configuration (code splitting, SVG loader, proxy)
├── tsconfig.json                        # Strict TypeScript compiler options
├── package.json                         # Dependencies and build scripts
├── public/
│   ├── fonts/                           # Self-hosted Plus Jakarta Sans (woff2 format)
│   ├── favicon.svg
│   └── locales/                         # en-IN, ml localization catalogues
├── src/
│   ├── app/
│   │   ├── App.tsx                      # Root application component with Providers
│   │   ├── Router.tsx                   # React Router 6.x route tree with auth/role guards
│   │   └── Shell.tsx                    # Persistent Layout Shell (Sidebar, Header, Alerts, Command Palette)
│   ├── features/                        # Domain-driven feature vertical slices
│   │   ├── auth/                        # Login, 2FA, lockout timer, session watchdog
│   │   ├── dashboard/                   # KPI metrics, 14-day trends, status mix, live trips overview
│   │   ├── map/                         # MapLibre GL engine, vehicle clustering, live telemetry
│   │   ├── shipments/                   # Table, 6-tab full page, detail drawer, optimistic editor
│   │   ├── fleet/                       # Drivers directory, vehicles registry, approval queue
│   │   ├── alerts/                      # Dedicated alerts center, popover, resolution workflows
│   │   ├── reports/                     # Charts engine, 90-day targets, CSV/XLSX/PDF streaming
│   │   ├── releases/                    # Version management, staged rollout, emergency halts
│   │   ├── team/                        # User administration, role management, session revocation
│   │   ├── audit/                       # Immutable log viewer, JSON field-level diff engine
│   │   ├── playback/                    # Historical GPS route playback with telemetry scrubber
│   │   └── settings/                    # Profile, appearance, security 2FA, notifications
│   └── shared/
│       ├── api/                         # OpenAPI client generated from NestJS/Supabase schema
│       ├── realtime/                    # Supabase WebSocket client with auto-reconnect & JWT
│       ├── theme/                       # Token definitions, switcher hooks, CSS variable injection
│       ├── ui/                          # Design system primitives matching v2 mockup
│       ├── hooks/                       # Reusable hooks (useDebounce, useIdleTimer, useKeyCombo)
│       └── utils/                       # Date formatters (en-IN), tabular number utils, validation
```

### 1.2 Technology Stack & Selection Rationale
| Layer / Tool | Chosen Technology | Architectural Rationale |
| :--- | :--- | :--- |
| **Framework & Runtime** | React 18 + Vite | Concurrent rendering for high-frequency telemetry, ultra-fast HMR, sub-250 KB gzip bundle target. |
| **Language & Typings** | TypeScript (Strict) | Enforces `noImplicitAny`, `strictNullChecks`, `exactOptionalPropertyTypes`. Zero `any` policy. |
| **Server State & Caching** | TanStack Query v5 | Fine-grained cache keys, automated background polling fallbacks, optimistic updates, query deduplication. |
| **Forms & Validation** | React Hook Form + Zod | Isomorphic client/server schema validation; zero re-renders on keystrokes; RFC 7807 problem field mapping. |
| **Geospatial Map Engine** | MapLibre GL JS (v4.x) | Free vector tile rendering via OpenFreeMap Positron; hardware-accelerated WebGL; zero Google Maps billing risk. |
| **Visual Analytics** | Chart.js 4.x via `react-chartjs-2` | Exact match to `v2.html` visuals; direct dynamic CSS token extraction via `getComputedStyle`; canvas 60 FPS performance. |
| **Icons & Styling** | Inline SVG primitives + CSS Modules | Exact parity with `TruckConnect_Operations_Console_v2.html`; no heavyweight external UI kits that distort design tokens. |
| **Typography** | Self-hosted Plus Jakarta Sans | Zero external Google Fonts CDN calls (satisfies strict Content Security Policy and offline staging). |

---

### 1.3 Design System & CSS Token Architecture

The design system strictly adheres to the CSS variables declared in `:root` of `TruckConnect_Operations_Console_v2.html`. No third-party UI library (Tailwind/MUI/Chakra) is allowed to override or deviate from these variables.

#### Token Matrix: 4 Accents × 2 Modes (Light / Dark)
The application dynamically toggles `data-theme` (`indigo`, `emerald`, `graphite`, `sunset`) and `data-mode` (`light`, `dark`).

```css
/* Base Light Tokens */
:root {
  --bg: #f5f6f8;
  --surface: #ffffff;
  --surface-2: #f7f8fa;
  --border: #e8eaee;
  --border-2: #d9dce3;
  --text: #111318;
  --text-2: #4b5160;
  --muted: #8a90a0;
  --grid: #eef0f3;
  
  /* Status Colors */
  --ok: #0b7d45;
  --warn: #a96b00;
  --serious: #c2540a;
  --crit: #c9342f;
  --info: #2563eb;
  --neutral: #6b7280;

  /* Chart Palette (Light) */
  --c1: #2a78d6; --c2: #eb6834; --c3: #1baf7a; --c4: #eda100;
  --c5: #e87ba4; --c6: #008300; --c7: #6250d6; --c8: #e34948;

  /* Map Canvas Tokens */
  --map-land: #eef1f6;
  --map-sea: #f8fafd;
  --map-line: #cfd5e1;

  --shadow: 0 1px 2px rgba(16, 24, 40, 0.04);
  --shadow-lg: 0 12px 40px rgba(16, 24, 40, 0.14);
  color-scheme: light;
}

/* Accent Variations (Light) */
html[data-theme="indigo"]   { --accent: #4f46e5; --accent-soft: #eef0ff; --accent-ink: #ffffff; }
html[data-theme="emerald"]  { --accent: #047857; --accent-soft: #e6f6ef; --accent-ink: #ffffff; }
html[data-theme="graphite"] { --accent: #27272a; --accent-soft: #f0f0f1; --accent-ink: #ffffff; }
html[data-theme="sunset"]   { --accent: #d9480f; --accent-soft: #fff0e8; --accent-ink: #ffffff; }

/* Base Dark Tokens */
html[data-mode="dark"] {
  --bg: #0d0f13;
  --surface: #15181e;
  --surface-2: #1b1f26;
  --border: #262b34;
  --border-2: #343a46;
  --text: #eceef2;
  --text-2: #b6bcc9;
  --muted: #7f8696;
  --grid: #222731;

  --ok: #3cc47a;
  --warn: #e0a63a;
  --serious: #ee8a4a;
  --crit: #f0716c;
  --info: #6aa1ff;
  --neutral: #9aa1b0;

  /* Chart Palette (Dark) */
  --c1: #3987e5; --c2: #d95926; --c3: #199e70; --c4: #c98500;
  --c5: #d55181; --c6: #0ca30c; --c7: #9085e9; --c8: #e66767;

  /* Map Canvas Tokens (Dark) */
  --map-land: #1c212b;
  --map-sea: #12151b;
  --map-line: #323948;

  --shadow: 0 1px 2px rgba(0, 0, 0, 0.4);
  --shadow-lg: 0 12px 40px rgba(0, 0, 0, 0.5);
  color-scheme: dark;
}

/* Accent Variations (Dark) */
html[data-mode="dark"][data-theme="indigo"]   { --accent: #8b83ff; --accent-soft: #232245; --accent-ink: #0d0f13; }
html[data-mode="dark"][data-theme="emerald"]  { --accent: #34d399; --accent-soft: #0f2a22; --accent-ink: #0d0f13; }
html[data-mode="dark"][data-theme="graphite"] { --accent: #d4d4d8; --accent-soft: #25262b; --accent-ink: #0d0f13; }
html[data-mode="dark"][data-theme="sunset"]   { --accent: #fb923c; --accent-soft: #33200f; --accent-ink: #0d0f13; }
```

#### FOUC Prevention Protocol
To avoid Flash of Unstyled Content (or flashing light mode before user preference loads):
```html
<!-- In index.html <head> before stylesheets -->
<script>
  (function() {
    try {
      const savedTheme = localStorage.getItem('tc_theme') || 'indigo';
      const savedMode = localStorage.getItem('tc_mode') || 'auto';
      document.documentElement.dataset.theme = savedTheme;
      if (savedMode === 'auto') {
        const isDark = window.matchMedia('(prefers-color-scheme: dark)').matches;
        document.documentElement.dataset.mode = isDark ? 'dark' : 'light';
      } else {
        document.documentElement.dataset.mode = savedMode;
      }
    } catch (e) {
      document.documentElement.dataset.theme = 'indigo';
      document.documentElement.dataset.mode = 'light';
    }
  })();
</script>
```

#### Profile Theme Sync
Whenever the user modifies their appearance via the UI:
1. `document.documentElement` attributes update immediately (synchronous repaint).
2. LocalStorage (`tc_theme`, `tc_mode`) is updated.
3. If authenticated, an asynchronous background request `PATCH /api/v1/me` is fired with `{ theme: selectedTheme, mode: selectedMode }` so the user's mobile app and web console remain synchronized.

---

## 2. Backend & Database Integration Architecture

### 2.1 Database Schema Extensions (Supabase PostgreSQL + PostGIS)
To support the enterprise capabilities defined in the Master Prompt without altering working tables, the following schema migrations will be applied to Supabase:

```sql
-- Migration: 20261123000001_operations_console_extensions.sql

-- 1. Optimistic Concurrency & ETag support on Shipments
ALTER TABLE shipments ADD COLUMN IF NOT EXISTS version INT NOT NULL DEFAULT 1;

-- 2. Alerts & Exceptions Table
DO $$ BEGIN
    CREATE TYPE alert_type AS ENUM ('sos', 'rejected', 'timeout', 'delayed', 'expiring_document', 'stale_location');
EXCEPTION
    WHEN duplicate_object THEN null;
END $$;

DO $$ BEGIN
    CREATE TYPE alert_severity AS ENUM ('critical', 'serious', 'warning', 'info');
EXCEPTION
    WHEN duplicate_object THEN null;
END $$;

CREATE TABLE IF NOT EXISTS alerts (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    type alert_type NOT NULL,
    severity alert_severity NOT NULL,
    title VARCHAR(255) NOT NULL,
    subtitle TEXT NOT NULL,
    shipment_id UUID REFERENCES shipments(id) ON DELETE SET NULL,
    driver_id UUID REFERENCES users(id) ON DELETE SET NULL,
    vehicle_id UUID REFERENCES vehicles(id) ON DELETE SET NULL,
    resolved BOOLEAN NOT NULL DEFAULT FALSE,
    resolved_at TIMESTAMPTZ,
    resolved_by UUID REFERENCES users(id),
    resolution_note TEXT,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

-- 3. Saved Places / Address Book (Nominatim caching & business locations)
CREATE TABLE IF NOT EXISTS saved_places (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    name VARCHAR(255) NOT NULL,
    address TEXT NOT NULL,
    city VARCHAR(100) NOT NULL,
    geo_point GEOMETRY(Point, 4326) NOT NULL,
    contact_name VARCHAR(255),
    contact_phone VARCHAR(20),
    is_frequent BOOLEAN DEFAULT FALSE,
    created_by UUID REFERENCES users(id),
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

-- 4. User 2FA Secrets & Active Admin Sessions
CREATE TABLE IF NOT EXISTS user_two_factor (
    user_id UUID PRIMARY KEY REFERENCES users(id) ON DELETE CASCADE,
    totp_secret TEXT NOT NULL,
    backup_codes TEXT[] NOT NULL,
    enabled BOOLEAN NOT NULL DEFAULT FALSE,
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE TABLE IF NOT EXISTS user_sessions (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    user_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    refresh_token_hash VARCHAR(255) NOT NULL,
    ip_address VARCHAR(45) NOT NULL,
    user_agent TEXT NOT NULL,
    last_active TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    expires_at TIMESTAMPTZ NOT NULL,
    is_revoked BOOLEAN NOT NULL DEFAULT FALSE,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

-- 5. Indexes
CREATE INDEX IF NOT EXISTS idx_alerts_resolved_created ON alerts(resolved, created_at DESC);
CREATE INDEX IF NOT EXISTS idx_alerts_shipment ON alerts(shipment_id);
CREATE INDEX IF NOT EXISTS idx_saved_places_geo ON saved_places USING GIST(geo_point);
CREATE INDEX IF NOT EXISTS idx_user_sessions_user_active ON user_sessions(user_id, is_revoked);
```

### 2.2 Row Level Security (RLS) & Supabase Realtime Authorization

#### Realtime Publication Configuration
```sql
-- Add relevant operational tables to supabase_realtime publication
ALTER PUBLICATION supabase_realtime ADD TABLE shipments;
ALTER PUBLICATION supabase_realtime ADD TABLE alerts;
ALTER PUBLICATION supabase_realtime ADD TABLE drivers;
ALTER PUBLICATION supabase_realtime ADD TABLE vehicles;
```

#### RLS Policies for Operations Console
The backend issues JWTs signed with `SUPABASE_JWT_SECRET` containing:
```json
{
  "sub": "user-uuid",
  "role": "authenticated",
  "user_role": "super_admin", -- or "dispatcher"
  "email": "anil@truckconnect.in",
  "exp": 1795747200
}
```

Database policies restrict full fleet inspection solely to operational roles:
```sql
-- Alerts Policy: Only admins and dispatchers can read and update alerts
ALTER TABLE alerts ENABLE ROW LEVEL SECURITY;

CREATE POLICY "Admins and dispatchers full access to alerts"
ON alerts FOR ALL TO authenticated
USING (
  ((auth.jwt() ->> 'user_role') IN ('super_admin', 'dispatcher'))
)
WITH CHECK (
  ((auth.jwt() ->> 'user_role') IN ('super_admin', 'dispatcher'))
);

-- Drivers table RLS update: Dispatchers and Super Admins can read all drivers; Drivers only read self
CREATE POLICY "Admins read all drivers"
ON drivers FOR SELECT TO authenticated
USING (
  ((auth.jwt() ->> 'user_role') IN ('super_admin', 'dispatcher')) OR
  (auth.uid() = user_id)
);
```

> [!IMPORTANT]
> **RLS Leak Prevention:** Driver tokens (`user_role: 'driver'`) subscribing to `supabase_realtime` on `alerts` or `shipments` receive 0 events due to PostgreSQL row-level filters matching `auth.uid() = driver_id`.

---

### 2.3 API Contracts & OpenAPI Client Generation
The client maintains type safety by generating interfaces from the backend OpenAPI 3.1 schema using `openapi-typescript`.

#### Standard RFC 7807 Error Response
Every non-2xx response from NestJS or Supabase Edge Functions adheres strictly to `application/problem+json`:
```json
{
  "type": "https://api.truckconnect.in/errors/validation-failed",
  "title": "Validation Failed",
  "status": 422,
  "detail": "Weight exceeds vehicle maximum payload limit.",
  "instance": "/api/v1/shipments/TC-2026-000135",
  "code": "OVER_CAPACITY",
  "errors": [
    {
      "field": "weight_kg",
      "code": "EXCEEDS_VEHICLE_CAPACITY",
      "message": "Assigned vehicle KL-07-AZ-1200 maximum capacity is 10000 kg."
    }
  ]
}
```
The React Hook Form integration parses `errors[]` and calls `setError(err.field, { message: err.message })` automatically.

---

### 2.4 Server-Side State Machine & Concurrency Control

#### Shipment State Machine Matrix
All transitions must be validated server-side. Dispatchers and Admins operate under strict validation:

```
[Created] ────(Assign)───► [Assigned] ───(Accept)───► [Accepted]
    │                          │                          │
 (Cancel)                   (Cancel)                   (Cancel)
    ▼                          ▼                          ▼
[Cancelled]                [Cancelled]                [Cancelled]

[Accepted] ───(Arrive)───► [Reached Pickup] ───(Load)───► [Loaded]
                                                             │
                                                          (Depart)
                                                             ▼
[Delivered] ◄──(Proof)─── [Reached Drop] ◄──(Arrive)─── [In Transit]
```

#### Role Permission Boundary
| Operation | Super Admin (Owner) | Dispatcher | Validation / Audit Rules |
| :--- | :---: | :---: | :--- |
| **Create Shipment** | Allowed | Allowed | Validates capacity, schedules, address existence. |
| **Assign / Reassign (Pre-Trip)** | Allowed | Allowed | Triggers FCM alert to driver. Driver status must not be `inactive`. |
| **Modify In-Progress Shipment** | Allowed | Allowed (Reason Req.) | Requires non-empty reason string (`change_reason`). Dispatches push alert to driver. Writes before/after to `audit_logs`. |
| **Cancel Shipment** | Allowed | Allowed (Reason Req.) | Mandatory reason selection (`Customer request`, `No vehicle`, etc.). Reverts driver to `available`. |
| **Status Override** | Allowed | **Forbidden (403)** | Emergency administrative override (`POST /shipments/:id/override-status`). Explicit audit entry. |
| **Reopen Cancelled / Failed** | Allowed | **Forbidden (403)** | Moves status back to `created`. Clears previous failure logs. |
| **Correct Delivered Details** | Allowed | **Forbidden (403)** | Updates notes or attaches replacement documents only. Immutable POD photo preserved. |

#### Optimistic Concurrency Protocol (409 Conflict Handling)
1. Every `GET /api/v1/shipments/:id` returns an HTTP header `ETag: "W/<version>"` and body property `{ version: 5 }`.
2. Every `PATCH /api/v1/shipments/:id` sends `If-Match: "W/<version>"` or payload `{ expected_version: 5 }`.
3. If database `version != expected_version`:
   - Backend rolls back transaction and issues HTTP 409 Conflict.
   - Body contains `{ current_version: 6, last_modified_by: "Divya S", updated_at: "..." }`.
   - The Console UI detects 409 and opens the **Conflict Resolution Modal**:
     - Highlights side-by-side differences between the local unsaved form values and the fresh server values.
     - Offers two actions: `Discard My Changes & Reload Server Version` or `Re-apply My Edits on Top of Version 6`.

---

## 3. Comprehensive View Catalog

The Operations Console encompasses **15 distinct production views**: 6 core views originating from the mockup, plus 9 critical enterprise views required for complete operational readiness.

### 3.1 The 6 Core Views (Derived from Mockup)

#### View 1: Sign In & Authentication (`/login`)
- **Visual Spec:** Minimalist centered card, TruckConnect logo mark, email & password inputs, "Keep me signed in" checkbox, inline alert box for server errors.
- **Security & Lockout Engine:**
  - Server-side failed attempt tracker (5 consecutive failures → 15-minute lockout).
  - Countdown timer displayed inline when locked (`Too many attempts. Try again in 14m 32s`).
  - Zero hardcoded demo credentials in production bundles.
- **Header Controls:** Dedicated theme palette swatches (4 colors) and Light/Dark toggle embedded on the login page.
- **Session Architecture:** In-memory short-lived access JWT (15-min lifespan); httpOnly, Secure, SameSite=Strict cookie for refresh token.

#### View 2: Executive Operations Dashboard (`/dashboard`)
- **4 Key Operational Metrics (KPIs):**
  1. *On Trip:* Active drivers currently moving loads with comparative delta against total fleet.
  2. *Available:* Idle drivers ready for dispatch.
  3. *Pending Assignment:* Shipments awaiting driver allocation, flagging overdue items.
  4. *Delayed:* Shipments operating past scheduled milestone or ETA windows.
- **Shipments Per Day Trend:** Dual-bar Chart.js visual (Created vs. Delivered) across the trailing 14 calendar days.
- **Status Mix Donut:** Proportion of Delivered, In Progress, Awaiting Driver, and Cancelled loads with central count badge.
- **Live Trips Stream:** Real-time milestone stepper (`Accepted` → `Pickup` → `Loaded` → `Transit` → `Drop`) updating via WebSocket.
- **Alerts Mini-Center:** Immediate high-priority alerts with one-click resolution actions.

#### View 3: Live Geospatial Tracking Map (`/map`)
- **MapLibre GL Canvas:** Kerala bounding box (`8.18, 74.85` to `12.85, 77.40`), styled with custom OpenFreeMap Positron tokens matching `--map-land`, `--map-sea`, and `--map-line`.
- **Dynamic Vehicle Markers:**
  - Status color-coded rings: Active/Moving (`--accent`), Delayed (`--serious`), Stale Telemetry (`--warn`).
  - Pulsing animated radar ring on selected vehicles.
  - Directional headings derived from bearing telemetry.
- **Split-View Vehicle Master-Detail Rail:**
  - Left/Right collapsible list displaying registration, driver avatar, route (`From → To`), and telemetry freshness (`12s ago`).
  - Vehicle inspection sheet displaying live speed (km/h), calculated ETA, quick link to Milestone Timeline, and "Call Driver" CTA.
  - Active polyline route overlay with dashed line for remaining segment and solid line for completed trail.

#### View 4: Shipments Dispatch Hub (`/shipments`)
- **Segmented Filter Bar:** `All`, `Active`, `Pending`, `Delivered`, `Cancelled`.
- **High-Density Data Table:** Columns for Shipment Code, Route, Goods & Weight, Driver avatar, Vehicle reg, Scheduled pickup, Status chip.
- **Instant Search:** Global search debounced against shipment ID, addresses, driver name, vehicle registration.
- **Action Drawer Integration:** Tapping any table row smoothly slides out the comprehensive Shipment Detail Drawer without leaving page context.
- **Dispatch Quick Actions:** "New Shipment" modal launch, bulk export triggers, one-click duplication.

#### View 5: Fleet & Asset Directory (`/fleet`)
- **Sub-Tab Navigation:**
  - **Tab 1 (Drivers):** Name, Phone, Assigned Vehicle, Licence Expiry countdown chip, On-time performance bar (%), 30-day trip count, Installed app version code, Status (`Available`, `On Trip`, `On Leave`, `Offline`, `Inactive`).
  - **Tab 2 (Vehicles):** Registration number, Type (`Lorry`, `Mini truck`, `Container`, `Pickup`), Payload capacity (tonnes), Default driver, Statutory document health (`All valid` or `Renewal in X days`), Odometer (km), Operational status.
  - **Tab 3 (Approvals Queue):** Self-registered driver submissions awaiting verification; count badge in tab title.
- **Fleet Asset Modals:** Add/Edit forms for drivers and vehicles with inline capacity vs payload cross-checking.
- **Credential Governance:** Admin-initiated "Reset Driver Password" (one-time secure display dialog) and "Revoke Device" session kill.

#### View 6: Executive Analytics & Reports (`/reports`)
- **Time Window Segmentation:** 8 Weeks vs 6 Months aggregate toggles.
- **On-Time Delivery Performance:** Line chart with continuous smoothing and benchmark target threshold line (92%).
- **90-Day Enterprise Targets:** Progress bars tracking SLA metrics:
  - Shipments assigned via app (Goal: 90% | Actual: 94%).
  - Proof of delivery captured (Goal: 95% | Actual: 96%).
  - Trips with live tracking (Goal: 90% | Actual: 91%).
  - Weekly active drivers (Goal: 90% | Actual: 88%).
  - Crash-free app sessions (Goal: 99.5% | Actual: 99.7%).
- **Comparative Metrics:** Completed vs. Cancelled monthly stacked bars; Top 8 Drivers by trip count horizontal bar chart; Median Assignment-to-Acceptance duration curve (target < 5 min).
- **Client App Adoption:** Version distribution donut tracking driver migration to the latest APK release.
- **Report Streaming:** Direct asynchronous downloads for CSV, XLSX, and formatted PDF summaries via backend worker endpoints.

---

### 3.2 The 9 Critical Enterprise Views (Required for Parity & Production)

#### View 7: Dedicated Alerts & Exceptions Center (`/alerts`)
- **Scope & Purpose:** Centralized triage board for real-time fleet anomalies.
- **Alert Taxonomy:**
  - `Critical (SOS)`: Emergency panic button triggered by driver; displays GPS coordinates, battery %, and emergency contact.
  - `Rejected`: Immediate notification when a driver declines an assigned trip; includes mandatory driver reason (`Vehicle issue`, `Personal emergency`).
  - `Assignment Timeout`: Unresponsive driver (> 10 min without accepting); triggers immediate reassignment workflow.
  - `Trip Delayed`: Geofence or speed calculation indicates vehicle will miss expected delivery window by > 30 min.
  - `Expiring Documents`: Driver licence, vehicle RC, Insurance, Fitness, Permit, or PUC expiring within 30 days.
  - `Stale Location`: GPS signal missing for > 15 minutes during an active load.
- **Action Workflows:** Inline resolution modal requiring operator note (`"Called driver, tire puncture resolved, replacement dispatched"`), automated audit logging, one-click phone dialer, and quick-reassign sheet.

#### View 8: App Releases & In-App Rollout Engine (`/releases`)
- **Scope & Purpose:** Super Admin management of the self-hosted APK updater pipeline.
- **Version Registry Table:** Flavor (`direct` vs `play`), Version Code, Version Name, Min Supported Version Code, File Size (MB), SHA-256 Checksum, English & Malayalam release notes, Upload date.
- **Staged Rollout Controller:** Interactive slider (10% → 25% → 50% → 100%) targeting randomized driver cohorts.
- **Emergency Circuit Breaker:** Single-click "Halt Rollout" button immediately preventing further updates if crash rate spikes.
- **Forced Update Enforcement:** Ability to raise `min_supported_version_code` to block outdated or vulnerable APKs with a non-dismissible update screen on the driver's phone.
- **Adoption Matrix:** Per-driver breakdown showing exact version installed, update download status, and last handshake timestamp.

#### View 9: Team & Operator Administration (`/team`)
- **Scope & Purpose:** User governance for Super Admins and Dispatchers.
- **Operator Table:** Full name, Email, Phone, Role badge, Created at, Last login, Active status.
- **User Invitation & Provisioning:** Modal to create new dispatchers, generating a cryptographically secure temporary password and invitation link.
- **Granular Role Toggle:** Upgrade or downgrade privileges between `Super Admin` and `Dispatcher`.
- **Immediate Session Revocation:** Super Admin capability to sign out any operator across all active devices in one click (invalidates refresh tokens and broadcasts WebSocket termination).

#### View 10: Account Settings & Security (`/settings`)
- **Profile Configuration:** Update name, contact phone, avatar, and preferred language (Malayalam / English).
- **Password Governance:** Old password verification, new password complexity enforcement (Zod schema).
- **Two-Factor Authentication (2FA / TOTP):**
  - QR Code generator for Google Authenticator / Authy.
  - Verification code challenge to activate.
  - Generation and secure print-ready display of 8 single-use recovery codes.
- **Notification Preferences:** Toggles for desktop push and sound alerts for SOS events, delay warnings, and driver rejections.
- **Active Sessions Watchdog:** List of all browser sessions showing IP address, browser/OS fingerprint, login timestamp, and "Sign Out Everywhere" button.

#### View 11: Driver 360 Detail Dossier (`/fleet/drivers/:id`)
- **Header Profile Card:** High-resolution driver photo, full name, phone number with one-click call, emergency contact details, current status badge.
- **Licence & Compliance Section:**
  - Driving licence number, class (`LMV`, `HGMV`, `HMV`), expiry date with status indicator.
  - Secure signed image viewer for Driving Licence Front and Back copies.
- **Performance Telemetry:** Overall on-time delivery rate (%), lifetime trips completed, average customer rating, safety incident log.
- **Trip History Ledger:** Grouped list of all historical shipments, milestone durations, and links to proof records.
- **Hardware & App Telemetry:** Registered device model, Android OS version, FCM push token health, battery status at last ping, installed app version.
- **Administrative Actions:** Reset Driver Password, Revoke Device Token, Deactivate Account (Owner only).

#### View 12: Vehicle 360 Detail Dossier (`/fleet/vehicles/:id`)
- **Specification Overview:** Registration plate, vehicle type icon, gross vehicle weight, payload capacity (kg/tonnes), cumulative odometer.
- **Compliance & Statutory Document Registry:**
  - Tabs for Registration Certificate (RC), Comprehensive Insurance, Fitness Certificate, State/National Goods Permit, Pollution Under Control (PUC).
  - Expiry date monitoring chips (`Valid`, `Expiring in 12 days`, `Expired`).
  - Signed document attachment downloads.
- **Assignment & History:** Current default driver link, lifetime maintenance log, historical trip timeline, utilization rate (hours moving vs idle).

#### View 13: Shipment Enterprise Full Page (`/shipments/:id`)
Provides an exhaustive 6-tab deep dive for complex shipment management:
- **Tab 1: Overview & Consignment Details:**
  - Origin & Destination addresses with contact persons and verified phone numbers.
  - Cargo manifests: description, quantity, gross weight, handling instructions.
  - Scheduled pickup vs actual delivery timestamps.
  - Assigned driver and vehicle cards with quick-switch buttons.
- **Tab 2: Milestone Timeline:**
  - Chronological trace: `Created` → `Assigned` → `Accepted` → `Reached Pickup` → `Loaded` → `In Transit` → `Reached Drop` → `Delivered`.
  - Exact UTC timestamp and relative delta for each milestone.
  - Actor attribution (Admin vs Driver name) and GPS location checkmark (`Coordinates captured within 25m of geofence`).
- **Tab 3: Route & Geofence Map:**
  - Full-screen MapLibre canvas showing planned road corridor vs actual recorded GPS breadcrumbs.
  - Geofence circle visualization around pickup and drop locations.
  - Flagged stop points and excess idle durations.
- **Tab 4: Commercial Documents:**
  - Presigned upload and preview gallery for Tax Invoices, E-Way Bills, and Lorry Receipts (LR).
- **Tab 5: Proof of Delivery (POD):**
  - High-resolution delivery photo viewer with zoom and pan.
  - Digital signature canvas replay.
  - Receiver name, designation, and OTP verification confirmation badge.
- **Tab 6: Activity & Audit Diff:**
  - Comprehensive field-level change history showing who changed what (e.g. Weight modified from 5.0t to 6.2t by Anil Kumar).

#### View 14: Route Playback & Telemetry Scrubber (`/shipments/:id/playback`)
- **Scope & Purpose:** Post-trip investigative telemetry replay tool.
- **Map Visualizer:** Animated vehicle marker following the sequential PostGIS `location_pings` path.
- **Interactive Time Scrubber:** Scrub through trip duration with second-level granularity.
- **Playback Controls:** Play, Pause, Speed Multipliers (`1x`, `2x`, `5x`, `10x`).
- **Telemetry HUD:** Synchronized readout of speed (km/h), heading, battery %, GPS accuracy (meters), and current road segment name.
- **Exception Markers:** Route map annotates hard braking, unexpected route deviations (> 5 km from planned path), and prolonged stops (> 20 min).

#### View 15: Global Command Palette (`⌘K` / `Ctrl+K`)
- **Global Keyboard Shortcut:** Accessible from any view via `⌘K` (Mac) or `Ctrl+K` (Windows/Linux).
- **Fuzzy Search Indexing:**
  - Live query against Shipments (`TC-2026-000135`), Consignees, Drivers (`Suresh Kumar`), and Vehicles (`KL-07-AZ-1200`).
- **Quick Action Commands:**
  - `> New Shipment` (opens creation wizard).
  - `> Add Driver` / `> Add Vehicle`.
  - `> Switch Theme: Emerald` / `> Toggle Dark Mode`.
  - `> View Open Alerts` (jumps to Alerts Center).
  - `> Go to Live Map`.
- **Keyboard Navigation:** Full arrow key navigation (`↑`, `↓`), `Enter` to execute, `Esc` to close.

---

## 4. Phased Execution & Quality Gates (Phases C0 through C7)

Each phase is defined by strict boundaries, deliverables, concrete acceptance criteria, and explicit Playwright verification gates.

```
┌──────┐    ┌──────┐    ┌──────┐    ┌──────┐    ┌──────┐    ┌──────┐    ┌──────┐    ┌──────┐
│  C0  │───►│  C1  │───►│  C2  │───►│  C3  │───►│  C4  │───►│  C5  │───►│  C6  │───►│  C7  │
└──────┘    └──────┘    └──────┘    └──────┘    └──────┘    └──────┘    └──────┘    └──────┘
 Scaffolding   Auth &     Shipments   Fleet &     Map &       Reports &   A11y, Perf  Cross-App
 & Tokens      Security   Engine      Approvals   Realtime    Releases    & Reflow    Release
```

---

### Phase C0: Monorepo Scaffolding, Token Engine, Mockup Alignment & CI Baseline

#### Objectives
1. Initialize `apps/console` using Vite, React 18, strict TypeScript.
2. Establish CSS token architecture implementing all 8 combinations of 4 accents × Light/Dark/Auto modes.
3. Configure persistent application shell (Sidebar, Top Navigation Bar, View Switcher).
4. Integrate OpenAPI schema parser and auto-generate typed API client.
5. Setup Playwright visual regression harness capturing baseline screenshots of `TruckConnect_Operations_Console_v2.html`.

#### Deliverables & File Changes
- `apps/console/package.json`, `apps/console/vite.config.ts`, `apps/console/tsconfig.json`
- `apps/console/src/shared/theme/tokens.css` (Base design tokens)
- `apps/console/src/shared/theme/useTheme.ts` (Theme state hook and switcher)
- `apps/console/src/app/Shell.tsx` (Sidebar and top chrome layout)
- `apps/console/src/shared/api/generated/client.ts` (OpenAPI client)
- `tests/visual/baseline.spec.ts` (Playwright automated screenshot suite)

#### Concrete Acceptance Criteria
- Token computation test passes across all 8 theme/mode variations, matching values in `v2.html` within 0.01% color variance.
- Playwright pixel diff between rendered Vite shell and `v2.html` is $\le 1.5\%$ at $1440 \times 900$ and $390 \times 844$.
- TypeScript compilation (`tsc --noEmit`) passes with zero errors under strict mode.

---

### Phase C1: Authentication, Session Management, 2FA & RBAC Security

#### Objectives
1. Implement Login view (`V1`) with server-side validation and lockout messaging.
2. Build in-memory access token storage and secure httpOnly cookie refresh loop.
3. Implement 30-minute idle session timeout modal with warning countdown.
4. Enforce role-based route guards for `Super Admin` vs `Dispatcher`.
5. Integrate TOTP Two-Factor Authentication challenge flow.

#### Deliverables & File Changes
- `apps/console/src/features/auth/LoginView.tsx`
- `apps/console/src/features/auth/TwoFactorModal.tsx`
- `apps/console/src/features/auth/useAuth.ts`
- `apps/console/src/features/auth/sessionWatchdog.ts`
- `apps/console/src/app/GuardedRoute.tsx`
- `apps/console/src/shared/ui/Forbidden403.tsx`

#### Concrete Acceptance Criteria & Playwright Plan
- **Test C1.1:** Attempt 5 invalid logins; verify server returns 429/423 and UI displays countdown lock banner.
- **Test C1.2:** Login as Dispatcher; verify access to `/dashboard`, `/shipments`, `/map`, `/fleet`; verify navigation to `/releases`, `/team`, and `/audit` displays styled 403 page.
- **Test C1.3:** Simulate 30-minute idle period; verify "Stay signed in?" dialog displays with 60-second logout countdown.

---

### Phase C2: Shipments Engine, State Machine, Drawer & Optimistic Concurrency

#### Objectives
1. Implement Shipments Hub (`V4`), full 6-tab Shipment Detail Page (`V13`), and quick-edit Drawer.
2. Build New Shipment & Edit Shipment forms with Zod schema validation.
3. Integrate Optimistic Concurrency using `ETag` / `If-Match` with 409 conflict resolution dialog.
4. Implement Assign, Reassign, and Cancel workflows with mandatory reason capture.
5. Implement document attachment upload via presigned URLs.

#### Deliverables & File Changes
- `apps/console/src/features/shipments/ShipmentsView.tsx`
- `apps/console/src/features/shipments/ShipmentDetailPage.tsx`
- `apps/console/src/features/shipments/ShipmentDrawer.tsx`
- `apps/console/src/features/shipments/ShipmentFormModal.tsx`
- `apps/console/src/features/shipments/ConflictModal.tsx`
- `apps/console/src/features/shipments/schemas.ts`

#### Concrete Acceptance Criteria & Playwright Plan
- **Test C2.1:** Create a shipment; verify instant appearance in table and correct database insertion.
- **Test C2.2 (Concurrency):** Open shipment in two distinct browser sessions. Session A edits weight to 8t and saves. Session B attempts to edit delivery address to 'Kollam' and saves. Verify Session B receives 409 Conflict dialog showing Session A's changes.
- **Test C2.3:** Attempt to assign a driver whose status is `On Trip`; verify non-blocking warning banner appears.

---

### Phase C3: Fleet & Approvals Engine, Document Expiry & Driver Credentials

#### Objectives
1. Implement Fleet Management Hub (`V5`) with Drivers, Vehicles, and Approvals tabs.
2. Build Driver 360 (`V11`) and Vehicle 360 (`V12`) detail dossier views.
3. Implement Driver Self-Registration approval queue with document zoom and mandatory verification notes.
4. Implement secure Driver PIN/Password reset and device token revocation.
5. Implement statutory document expiry chips (RC, Insurance, Fitness, Permit, PUC).

#### Deliverables & File Changes
- `apps/console/src/features/fleet/FleetView.tsx`
- `apps/console/src/features/fleet/DriverDetailView.tsx`
- `apps/console/src/features/fleet/VehicleDetailView.tsx`
- `apps/console/src/features/fleet/ApprovalsQueue.tsx`
- `apps/console/src/features/fleet/ApprovalReviewModal.tsx`
- `apps/console/src/features/fleet/ResetCredentialModal.tsx`

#### Concrete Acceptance Criteria & Playwright Plan
- **Test C3.1:** Review pending driver registration; zoom in on licence photo; approve with note; verify driver is immediately added to active fleet.
- **Test C3.2:** Trigger "Reset Password" for driver; verify temporary password displayed once with copy-to-clipboard CTA and audit log recorded.
- **Test C3.3:** Filter vehicles by document expiry $\le 30$ days; verify accurate chip coloring (`--warn` / `--crit`).

---

### Phase C4: Live Map, PostGIS Tracking, Supabase Realtime & Full Alerts Engine

#### Objectives
1. Integrate MapLibre GL JS engine rendering OpenFreeMap Positron vector tiles.
2. Connect to Supabase Realtime WebSocket using minted admin JWT.
3. Render live vehicle markers with status coloring and dynamic popups.
4. Implement Dedicated Alerts & Exceptions Center (`V7`) and header alert popover.
5. Implement Route Playback tool (`V14`) with interactive time scrubber and speed multiplier.

#### Deliverables & File Changes
- `apps/console/src/features/map/LiveMapView.tsx`
- `apps/console/src/features/map/MapLibreWrapper.tsx`
- `apps/console/src/features/map/VehicleMarker.tsx`
- `apps/console/src/features/alerts/AlertsCenterPage.tsx`
- `apps/console/src/features/alerts/AlertPopover.tsx`
- `apps/console/src/features/playback/RoutePlaybackView.tsx`
- `apps/console/src/shared/realtime/useRealtimeFleet.ts`

#### Concrete Acceptance Criteria & Playwright Plan
- **Test C4.1 (Realtime Latency):** Ingest synthetic GPS ping via API; assert vehicle marker position updates on the MapLibre canvas within $\le 500\text{ ms}$ via WebSocket.
- **Test C4.2 (Alert SLA):** Inject synthetic SOS alert; verify immediate audible/visual chime, pip badge appearance, and alerts table update.
- **Test C4.3 (Playback Scrubber):** Load completed trip route; scrub to 50% milestone; verify HUD displays correct historical speed and coordinate.

---

### Phase C5: Reports & Streaming Exports, Releases, Team, Audit & Command Palette

#### Objectives
1. Implement Executive Analytics & Reports (`V6`) with Chart.js reactive token redraws.
2. Build server-streamed CSV, Excel (XLSX), and branded PDF export generators.
3. Implement App Releases & Rollout Engine (`V8`) with staged rollout slider and halt button.
4. Implement Team Management (`V9`), Audit Log viewer (`X9`), and Command Palette (`V15`).

#### Deliverables & File Changes
- `apps/console/src/features/reports/ReportsView.tsx`
- `apps/console/src/features/reports/exportClient.ts`
- `apps/console/src/features/releases/ReleasesView.tsx`
- `apps/console/src/features/team/TeamView.tsx`
- `apps/console/src/features/audit/AuditLogView.tsx`
- `apps/console/src/features/command/CommandPalette.tsx`

#### Concrete Acceptance Criteria & Playwright Plan
- **Test C5.1:** Trigger PDF report export; verify received binary contains correct TruckConnect branding and tabular data.
- **Test C5.2:** Change APK staged rollout percentage from 25% to 50%; verify API update and driver app check reflects new percentage.
- **Test C5.3:** Press `⌘K`; type `TC-2026-000132`; press `Enter`; verify route immediately navigates to shipment detail.

---

### Phase C6: Responsive Reflow, WCAG 2.2 AA Accessibility & Web Vitals Hardening

#### Objectives
1. Implement responsive layout reflow:
   - Full sidebar $\ge 1100\text{ px}$.
   - Collapsible icon rail $760\text{ px} \le w < 1100\text{ px}$.
   - Horizontal top scroller & stacked cards $< 760\text{ px}$.
2. Conduct automated axe-core audit ensuring WCAG 2.2 AA accessibility compliance across all 15 views.
3. Optimize production bundle: route-based code splitting, asset compression, LCP $\le 2.5\text{ s}$.

#### Deliverables & File Changes
- `apps/console/src/shared/ui/ResponsiveContainer.tsx`
- `apps/console/src/shared/ui/A11yLiveAnnouncer.tsx`
- `tests/a11y/axe.spec.ts`
- `tests/perf/lighthouse.spec.ts`

#### Concrete Acceptance Criteria & Playwright Plan
- **Test C6.1 (A11y):** Run `@axe-core/playwright` across all 15 views; 0 critical or serious violations detected; all text/background pairs pass 4.5:1 contrast.
- **Test C6.2 (Responsive):** Test viewport resize to $375 \times 667$ (mobile); verify tables reflow into stacked cards without horizontal viewport blowout.
- **Test C6.3 (Performance):** Gzip bundle analysis confirms entry chunk $\le 250\text{ KB}$; Lighthouse performance score $\ge 90$ on desktop.

---

### Phase C7: Cross-Product Android Emulator Integration, Security Audit & Staging Deployment

#### Objectives
1. Execute end-to-end cross-product verification between Console and Android app (running in emulator).
2. Perform OWASP Web Security Audit (CSP without `unsafe-inline`, strict CORS, secure cookie flags).
3. Draft production runbook and deployment automation scripts for static hosting.

#### Deliverables & File Changes
- `tests/e2e/cross_product_assignment.spec.ts`
- `docs/console_release_readiness.md`
- `docs/CONSOLE_RUNBOOK.md`
- `.github/workflows/deploy_console.yml`

#### Concrete Acceptance Criteria & Playwright Plan
- **Test C7.1 (Cross-Product E2E):**
  1. Operator creates and assigns shipment in Operations Console.
  2. Android Driver emulator receives high-priority FCM notification and displays full-screen alert.
  3. Driver taps "Accept" and "Start Trip" on Android emulator.
  4. Operations Console map marker transitions from idle to active, and milestone timeline updates to `In Transit` within 3 seconds.
- **Test C7.2 (Security):** Security scan confirms zero secrets in client bundle, strict CSP headers present, and access tokens not accessible in `localStorage`.

---

## 5. Action Map & Click-Through Verification Strategy

To guarantee that no control is left dead or non-interactive, every UI element in `apps/console` carries a standardized `data-testid` attribute following the convention `<view>:<element_id>`.

A dedicated test suite in Playwright reads `docs/console_action_map.csv` and systematically tests every interactive element:

```csv
testid,view,type,expected_event,target_endpoint_or_route
login:input_email,login,input,type_text,none
login:btn_submit,login,button,api_call,/api/v1/auth/login
nav:btn_dashboard,shell,button,route_change,/dashboard
nav:btn_map,shell,button,route_change,/map
nav:btn_shipments,shell,button,route_change,/shipments
nav:btn_fleet,shell,button,route_change,/fleet
nav:btn_reports,shell,button,route_change,/reports
top:btn_new_shipment,shell,button,open_modal,modal:new_shipment
top:btn_theme_toggle,shell,button,open_popover,popover:theme
theme:swatch_emerald,shell,button,theme_change,data-theme=emerald
shipments:table_row_000131,shipments,tr,open_drawer,/shipments/TC-2026-000131
drawer:btn_reassign,drawer,button,open_modal,modal:reassign
modal:btn_submit_reassign,reassign_modal,button,api_call,/api/v1/shipments/:id/reassign
fleet:tab_approvals,fleet,button,filter_change,tab=approvals
approvals:btn_review_p1,fleet,button,open_modal,modal:review_driver
review:btn_approve,review_modal,button,api_call,/api/v1/admin/drivers/:id/approve
reports:btn_export_csv,reports,button,file_download,/api/v1/reports/export?format=csv
cmd:palette_input,command_palette,input,filter_items,none
```

Any interactive element present in the DOM without a corresponding entry in the Action Map will cause CI gate failure.

---

## 6. Open Owner Questions & Architectural Resolutions

| # | Question / Issue | Default Implementation (Applied) | Owner Decision Required |
| :--- | :--- | :--- | :--- |
| **1** | **Dispatcher Permission Bounds** | Dispatchers can view dashboards, manage shipments, assign/reassign, cancel with reason. Dispatchers **cannot** deactivate drivers, approve registrations, access app releases, or modify team accounts. | Confirm whether Dispatchers should be granted permission to approve self-registered drivers. |
| **2** | **In-Progress Shipment Edits** | Allowed for both Owners and Dispatchers, but **requires a mandatory reason** string, issues a high-priority push to the driver, and writes an immutable audit record. | Confirm whether route destination modification during transit requires Owner approval. |
| **3** | **Driver Approval Call Requirement** | Verification phone call note is mandatory in the UI form before the "Approve" button unlocks. | Confirm whether automated phone OTP should replace the manual phone verification call note. |
| **4** | **Static Hosting Selection** | Direct deployment to Cloudflare Pages or Vercel static tier with custom domain `console.truckconnect.in`. | Confirm preferred custom domain and DNS provider access. |
| **5** | **Document & Proof Retention** | Location pings pruned after 30 days; POD photographs and commercial invoices retained indefinitely in Supabase Storage. | Confirm enterprise data retention period (e.g. 7 years for GST compliance). |

---

## 7. Execution Timeline & Milestones Summary

| Phase | Duration | Primary Milestone | Release Gate Artifact |
| :--- | :---: | :--- | :--- |
| **C0** | Day 1–2 | Scaffolding, Strict CSS Tokens & Playwright Baselines | `tests/visual/baseline_report.html` |
| **C1** | Day 3–4 | Auth, Cookie Refresh, Idle Watchdog & RBAC Guards | `tests/e2e/auth_rbac_matrix.log` |
| **C2** | Day 5–7 | Shipments Dispatch, 6-Tab View & 409 Concurrency | `tests/e2e/shipments_audit.log` |
| **C3** | Day 8–9 | Fleet Directory, Document Expiry & Approvals Queue | `tests/e2e/fleet_approvals.log` |
| **C4** | Day 10–12 | MapLibre Live Map, Realtime WebSocket & Alerts Engine | `tests/perf/realtime_latency_report.json` |
| **C5** | Day 13–14 | Analytics, Streaming Exports, Releases & Command ⌘K | `tests/e2e/exports_releases.log` |
| **C6** | Day 15 | Responsive Shell, WCAG 2.2 AA Audit & Web Vitals | `tests/a11y/axe_audit_report.json` |
| **C7** | Day 16 | Cross-Product Android Emulator E2E & Production Deploy | `docs/console_release_readiness.md` |

This plan forms the authoritative implementation roadmap for the TruckConnect Operations Console, ensuring exact fidelity to the approved mockup, robust enterprise integration, and zero regressions across the platform.
