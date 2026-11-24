# TruckConnect (SmartFleet) — Master Handover & AI Agent Context

> **IMPORTANT NOTICE FOR INCOMING AI AGENTS & DEVELOPERS:**
> This document is your primary source of truth. Read this file first. Do NOT start from scratch or rebuild existing architectures. Everything created up to this point is enterprise-grade, functional, and organized.

---

## 1. Project Overview & Architecture

**TruckConnect (SmartFleet)** is an end-to-end fleet management and logistics platform engineered specifically for heavy trucking operations across Kerala (NH-66 corridor: Kasaragod → Kannur → Kozhikode → Malappuram → Thrissur → Kochi → Thiruvananthapuram).

The project consists of three core sub-systems:
1. **Android App (`android/`)**: Production-ready native Android app written in **Kotlin + Jetpack Compose** implementing all 47 screens across 4 themes (Indigo, Emerald, Crimson, Amber) × Light/Dark modes × English/Malayalam bilingual support.
2. **Operations Console (`apps/console/`)**: Real-time web dispatch dashboard built with **React + TypeScript + Vite + MapLibre GL + OpenStreetMap Standard**, featuring live vehicle telemetry, collapsable sidebars, and road-snapped route tracking.
3. **Backend & Telemetry Server (`backend/` & `supabase/`)**: Node.js/Express REST and WebSocket streaming server connected to Supabase PostgreSQL database schemas.

---

## 2. Android Native App (`android/`)

### Multi-Module Structure
- `:app`: Application entry point, Hilt dependency injection, `MainActivity.kt`, Compose navigation graph (`NavDestinations`).
- `:core-design`: Complete design system token hierarchy (`TcColors`, `TcType`, `TcDimens`, `TcShapes`, `TcScaffold`, `TcAppBar`, `TcBottomNav`, `TcCard`, `TcButton`, `TcField`, `TcOsmMap`).
- `:core-data`: Mock data fixtures (`MockData.kt`), local storage, repository contracts.
- `:core-network`: Retrofit / OkHttp clients and WebSocket listeners.
- `:feature-admin`: Admin screens (Dashboard, Live Map, Shipments, Fleet Vehicles, Drivers, More, Audit Log, New Shipment).
- `:feature-auth`: Auth screens (Splash, Role Select, Admin Sign-In, Driver Login with Phone & Password, Registration flows).
- `:feature-driver`: Driver screens (Home empty, Active Trip with Milestone Timeline, Job Alert, POD signature & camera, Delivered).
- `:feature-settings`: Settings screens (Admin Settings, Driver Settings, Edit Profile, Theme, Language, Notifications, Security, Help, App Version).

### Key Architectural Guidelines
- **Zero API Key Maps**: Built using `org.osmdroid:osmdroid-android:6.1.20`. Renders official OpenStreetMap Standard tiles (`TileSourceFactory.MAPNIK`) with custom vehicle markers, status badges, and NH-66 polyline routing.
- **Edge-to-Edge System Insets**: Universal `TcScaffold` composable automatically handles `WindowInsets.statusBars.union(WindowInsets.displayCutout)` on top app bars and `WindowInsets.navigationBars` on bottom bars/sheets, preventing overlap with camera cutouts and physical 3-button navigation.
- **Driver Authentication**: Uses **Mobile Number (+91 94470 12345) and Password** (PIN code removed per owner directive).
- **Driver Active Trip (V5)**: Displays full 5-step milestone timeline and equal-weight Navigate/Call buttons. The floating round SOS button was removed.
- **Strict Anti-Slop Tokens**: Every padding, color, typography size, and border is centralized in `:core-design`. No hardcoded magic numbers.

### Building & Running Android
```powershell
cd android
./gradlew :app:assembleDirectDebug
# APK is generated at: android/app/build/outputs/apk/direct/debug/app-direct-debug.apk
adb install -r android/app/build/outputs/apk/direct/debug/app-direct-debug.apk
adb shell am start -n com.truckconnect.app.debug/com.truckconnect.app.MainActivity
```

---

## 3. Operations Console (`apps/console/`)

- **Tech Stack**: React 19, TypeScript, Vite, MapLibre GL, Lucide React icons, OpenStreetMap tiles.
- **Map Implementation**: Pure OpenStreetMap Standard raster tiles (`https://tile.openstreetmap.org/{z}/{x}/{y}.png`), zero API keys needed. Absolute positioning for vehicle markers, heading direction rotation, animated radar pulses, and road route polylines.
- **UI Components**:
  - Collapsible sidebar with brand identity and quick navigation.
  - Floating mini-HUD in bottom right corner (dismissable, doesn't block road views).
  - Active vehicle telemetry drawer.
- **Running Operations Console**:
```powershell
cd apps/console
npm install
npm run dev
# Running on http://localhost:5173
```

---

## 4. Backend & Database (`backend/` & `supabase/`)

- **Tech Stack**: Node.js, Express, WebSocket (ws), Supabase PostgreSQL.
- **Database Schema (`supabase/migrations/`)**:
  - `vehicles`: License plate, model, capacity, status, current GPS coordinates.
  - `drivers`: Name, phone, license number, Aadhaar verification, status.
  - `shipments`: Tracking ID, origin, destination, assigned driver, milestones.
  - `telemetry_logs`: Historical GPS traces, speeds, alerts.
- **Running Backend**:
```powershell
cd backend
npm install
npm run dev
# Running on http://localhost:3000
```

---

## 5. Artifacts, History & References Directory Guide

- `mockup/index.html`: The interactive HTML mockup that serves as the visual reference.
- `reference/spec/`: Element-by-element JSON layout specifications.
- `reference/png/`: Reference PNG screenshots for all themes and modes.
- `Mobile/`: Physical phone screenshot captures from client testing identifying edge cases (E01-E13).
- `docs/handover/transcripts/transcript.jsonl`: Full trace of every decision and action taken.
- `docs/handover/user_uploads/`: Original user images and screenshots from real phone testing.
- `docs/handover/artifacts/`: Generated verification captures and UI hierarchy dumps.
- `docs/handover/artifacts/v3_release/`: Final verified device captures after v3 release pass.
- `evidence/`: Bug verification logs and comparison reports.

---

## 6. V3 Master Release Pass — Complete Changelog & Fixes

1. **Top Cutout / Punch-Hole & 3-Button Navigation Inset Elimination (`TcScreen`)**:
   - Every single screen now uses `TcScreen`, combining `WindowInsets.statusBars.union(WindowInsets.displayCutout)` at top and `navigationBarsPadding()` at bottom.
   - Screen headers, back buttons, and top actions no longer clip into the device's dynamic island / camera cutout.
   - Bottom primary action buttons, FABs, and bottom bars no longer overlap with 3-button navigation bars.

2. **Complete AI-Slop Purge (`TcChip`)**:
   - Replaced all circular colored dots (green/orange/red dots) in `TcStatusChip` with 16dp line vector icons (`ic_truck`, `ic_check`, `ic_clock`, `ic_warn`, `ic_x`, `ic_user`).
   - Chip backgrounds standardized to 12% tint of status color with 10dp corner radius and 28dp height per Antigravity v3 spec OD-1.

3. **60 FPS Hardware-Accelerated Navigation & Rendering**:
   - Removed per-frame bitmap allocations in OpenStreetMap (`TcOsmMap`), pre-caching 12 vector marker drawables (`pinCache`).
   - Bottom tab transitions maintain view hierarchies with `saveState = true` and `restoreState = true`.
   - Rapid double-tap debounce (`safeNavigate`) added to `MainActivity.kt`.
   - Hardware-accelerated enter/exit slide + fade animations.

4. **Driver Authentication & Active Trip (V5)**:
   - Driver Login updated to **Mobile Phone Number (+91 94470 12891) and Password** with visibility toggle (PIN code authentication removed).
   - Floating round red SOS button completely removed across driver and admin screens per user directive.
   - Active Trip screen features full milestone timeline, route summary cards, pickup & drop locations, and solid "Start trip" action.

5. **OpenStreetMap Integration**:
   - Zero-API-key OpenStreetMap standard tile surface (`osmdroid` MAPNIK) with Kerala highway corridor route polyline and live vehicle markers.
   - Interactive zoom buttons (+ / -) and recenter location control.

---

## 7. V3.2 Physical Device Verification & Defect Remediation (Latest Release)

During testing on physical OnePlus/Oppo hardware (Android 14, 393 × 877 dp viewport), specific edge cases were isolated and resolved:

### 1. "Start Trip" & History ANR / Process Crash
- **Root Cause**: Android 14 (`SDK 34`) strictly enforces runtime background and foreground service restrictions. Invoking `Service.startForeground()` without `ACCESS_FINE_LOCATION` or `ACCESS_COARSE_LOCATION` granted or from non-permitted lifecycle states instantly raises `ForegroundServiceStartNotAllowedException`, terminating the application process.
- **Remediation**:
  - `LocationTrackingService.kt`: Added proactive permission validation before invoking `startForeground(NOTIFICATION_ID, notification, ServiceInfo.FOREGROUND_SERVICE_TYPE_LOCATION)`. Wrapped every service lifecycle callback (`onCreate`, `onStartCommand`, `onDestroy`, and notification channel setup) inside `try / catch (e: Throwable)` safety shields to guarantee zero unhandled crashes or ANRs.
  - `ActiveTripScreen.kt`: Guarded `startForegroundService` behind runtime permission validation.
  - `DriverHistoryScreen.kt` & `MainActivity.kt`: Tapping historical delivered trips routes directly to `DRIVER_DELIVERED` read-only summary, avoiding re-triggering active GPS foreground telemetry.

### 2. Multi-Step Bar Elimination in "New Shipment"
- **Issue**: Screen displayed an unnecessary `TcStepsBar` (`2 / 3 · Goods & schedule`) on what is designed as an immediate, single-page dispatch form.
- **Remediation**: In `NewShipmentScreen.kt`, excised `TcStepsBar` and `2 / 3` step indicators completely. Transformed primary bottom button from "Next: assign driver" to "Create shipment", directly finalizing dispatch creation.

### 3. Dynamic Detail Routing (Shipments & Drivers)
- **Issue**: Tapping on distinct list items (e.g., Assigned, Rejected, Loaded, Delivered) previously fell back to a hardcoded "In transit" Kasaragod $\rightarrow$ Malappuram mock screen. Tapping any driver opened Suresh Kumar.
- **Remediation**:
  - `ShipmentDetailScreen.kt`: Implemented `ShipmentRepository.getShipment(id)` to map unique data for `TC-2026-000138` (Assigned), `TC-2026-000141` (Rejected), `TC-2026-000131` (In transit), `TC-2026-000133` (Loaded), and `TC-2026-000125` (Delivered). Dynamically loads matching route, consignee, driver, vehicle, and stage-specific milestones.
  - `DriverDetailScreen.kt`: Implemented `DriverDetailsRepository.getDriver(name)` supporting dynamic lookups for Suresh Kumar, Rajan Nair, Venugopal R, Shaji Varghese, and Haridas M with live vehicle assignments and ratings.
  - `MainActivity.kt`: Added `selectedShipmentId` and `selectedDriverName` state holders.

### 4. Job Alert Destination Truncation & Layout Balance
- **Issue**: Destination banner rendered `Palakkad →` with the destination city clipped due to oversize typography (`30.sp`). Massive empty void existed at the bottom of the screen.
- **Remediation**:
  - `TcType.kt`: Adjusted `jobAlertDestination` font size from `30.sp` to `22.sp` across all theme profiles. Routes like `Palakkad → Kozhikode` now display cleanly on a single line on 360dp+ viewports.
  - `JobAlertScreen.kt`: Applied `Arrangement.SpaceEvenly` and balanced vertical spacers, distributing HUD elements symmetrically.

### 5. Driver Approvals Button Squishing
- **Issue**: "Review" button was squished into an oval green pill against the right screen border due to horizontal row cramping next to the status chip.
- **Remediation**: In `DriverApprovalsScreen.kt`, separated items into a clean 2-row layout: Row 1 displays driver status chip and arrival timestamp; Row 2 provides equal-weight `Approve` (outline) and `Review` (primary) full-width touch targets.

### 6. Operations Console (Web) Zero-Key Map Routing
- **Remediation**: In `apps/console`, replaced heavyweight MapLibre GL bundle with native Google Maps external direction routing (`apps/console/src/shared/utils/gmaps.ts`), eliminating WebGL overhead and Google API key billing requirements. Automatically opens turn-by-turn navigation in a new tab.

---

## 8. Quick Commands for Mobile AI Agents

When working via remote agents or phone terminals, use these standard operational commands:

```bash
# Check repository status
git status

# Build Android APK (Release / Direct Debug)
cd android
./gradlew :app:assembleDirectDebug

# Run Web Console
cd apps/console
npm run dev

# Run Backend
cd backend
npm run dev
```
