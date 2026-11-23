# TruckConnect — Upgrade Path & Enterprise Roadmap

**Document Version:** 1.0  
**Status:** Architecture Blueprint for Post-Approval Scaling  

This document details every commercial/paid service upgrade planned for TruckConnect once client business approval is achieved and pilot testing is completed. **None of these paid services are used in the free phase build.** The codebase is structured with adapter interfaces to ensure that upgrading to any of these services requires zero rewrite of domain logic or user interfaces.

---

## 1. Free Phase vs. Enterprise Upgrade Matrix

| Architecture Layer | Free Phase (Zero Spend) | Enterprise Upgrade | Upgrade Trigger | Estimated Cost |
| :--- | :--- | :--- | :--- | :--- |
| **Database & Realtime** | Supabase Free Plan (500 MB DB, 200 realtime connections) | **Supabase Pro Plan** (8 GB DB included, unpaused, daily automated backups, point-in-time recovery, dedicated support) | Fleet exceeds 30 vehicles or database reaches 400 MB | \$25 / month |
| **Backend API Hosting** | Hugging Face Space (Docker) or Free Container Host | **Always-On Cloud Host** (e.g., AWS ECS / App Runner in `ap-south-1` Mumbai region or Render Team) | Commercial production launch with 99.9% SLA requirement | \$15 – \$40 / month |
| **Driver Login & Verification** | Phone Number + Self-Chosen 4-Digit PIN with Admin Approval | **SMS OTP via DLT-Registered Gateway** (Twilio, MSG91, or Fast2SMS with DLT entity registration in India) | Formal client budget sign-off for telecom traffic | $\approx$ ₹0.15 to ₹0.25 per SMS |
| **App Distribution & Updates** | Direct In-App Updater via `PackageInstaller` session API and Supabase Storage | **Google Play Console** (Internal / Closed Testing Track or Managed Google Play for Enterprise) | Pilot sign-off and Play Developer account registration | \$25 one-time registration fee |
| **Map Tiles & Geocoding** | OpenFreeMap vector tiles (Positron) + Nominatim search | **Hosted Vector Tiles & Commercial Geocoding** (e.g., Stadia Maps / MapTiler or Self-Hosted Tile Server on AWS) | Traffic exceeds community fair-use limits | \$20 – \$50 / month |
| **Cache & Real-time Queue** | In-process cache + PostgreSQL-backed queue (`pg-boss`) | **Managed Redis / Upstash** | Location ping ingestion exceeds 50,000 pings/hour | \$10 – \$30 / month |
| **Monitoring & Telemetry** | Firebase Crashlytics + Platform logs + Free Uptime Robot | **Sentry or Datadog / Grafana Cloud** | SLA enforcement and enterprise fleet monitoring | \$25 / month |
| **Admin Portal** | Android App Admin role | **Dedicated React / Next.js Web Dashboard** | Owner or desk dispatchers request multi-monitor web console | Engineering effort only |

---

## 2. Architectural Portability Guarantees

To ensure seamless upgrades without technical debt or rework:
1. **Adapter Pattern for External Services:**
   * Storage: `IFileStorageService` with `SupabaseStorageAdapter` (swappable to `S3StorageAdapter`).
   * Push Notifications: `IPushNotificationService` with `FirebaseCloudMessagingAdapter`.
   * SMS / OTP: `ISmsService` with `NoOpPinAuthAdapter` (swappable to `Msg91SmsAdapter`).
   * Maps: `IMapProvider` with `MapLibreTileAdapter`.
2. **Environment-Driven Configuration:**
   * All API keys, base URLs, credentials, and feature flags are injected strictly through environment variables.
3. **Versioned Database Migrations:**
   * All schema changes ship exclusively as reversible, sequential SQL migration files (`V1__initial_schema.sql`, etc.).
4. **Android Build Flavors:**
   * `direct`: Employs internal `PackageInstaller` updater engine (active now).
   * `play`: Configured for the Google Play Core In-App Updates SDK (switched upon Play registration).
