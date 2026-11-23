# SmartFleet / TruckConnect

An enterprise-grade, full-stack fleet management and logistics platform engineered for heavy transport logistics across the Kerala NH-66 corridor.

> **CRITICAL DEVELOPER / AI AGENT NOTICE**:
> For the comprehensive handoff and context briefing, please read:
> 👉 **[docs/HANDOVER_AGENT_CONTEXT.md](file:///docs/HANDOVER_AGENT_CONTEXT.md)**

---

## Repository Structure

```
├── android/                 # Native Android App (Kotlin + Jetpack Compose)
│   ├── app/                 # Main app module, Hilt DI, navigation graph
│   ├── core-design/         # Design tokens (TcColors, TcType, TcShapes) & OsmMap
│   ├── core-data/           # Domain models, MockData fixtures & repos
│   ├── core-network/        # API clients & WebSocket handlers
│   ├── feature-admin/       # Admin screens (Dashboard, Live Map, Shipments, Fleet)
│   ├── feature-auth/        # Auth screens (Driver login with Phone+Password, Admin)
│   ├── feature-driver/      # Driver screens (Active Trip, Job Alerts, POD)
│   └── feature-settings/    # Settings screens (Profile, Theme, Security, Help)
├── apps/
│   └── console/             # Operations Console (React 19 + TypeScript + MapLibre + OpenStreetMap)
├── backend/                 # Node.js + Express + WebSocket Telemetry API
├── supabase/                # Supabase PostgreSQL schema migrations
├── mockup/                  # Interactive HTML prototype (index.html)
├── reference/               # Design specs & rendered PNG screenshots
├── evidence/                # Test evidence and bug reports
└── docs/
    ├── HANDOVER_AGENT_CONTEXT.md   # Complete AI Agent / Developer Handover Guide
    ├── handover/                   # Historical transcripts, user uploads & artifacts
    └── ...                         # PRDs, decision records (ADRs), runbooks
```

## Quick Start

### Android App
```powershell
cd android
./gradlew :app:assembleDirectDebug
# APK is generated at: android/app/build/outputs/apk/direct/debug/app-direct-debug.apk
```

### Operations Console (Web)
```powershell
cd apps/console
npm install
npm run dev
# Open http://localhost:5173
```

### Backend API
```powershell
cd backend
npm install
npm run dev
# Open http://localhost:3000
```
