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
- `docs/handover/transcripts/transcript.jsonl`: Full trace of every decision and action taken.
- `docs/handover/user_uploads/`: Original user images and screenshots from real phone testing.
- `docs/handover/artifacts/`: Generated verification captures and UI hierarchy dumps.
- `evidence/`: Bug verification logs and comparison reports.
