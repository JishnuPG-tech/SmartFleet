# TruckConnect – Full Project PRD

**Scope:** Android app (Admin + Driver), backend platform, in-app updates, infrastructure **Region:** Kerala, India **Version:** 2.1 (Free-tier build, engineering-ready draft) **Status:** For client and team review

---

## 1. Product Summary

TruckConnect replaces phone-call dispatch for a truck and lorry transport business. One Android app serves two roles after login:

- **Admin:** creates shipments, assigns drivers, tracks vehicles live, sees delivery status and reports.
- **Driver:** receives jobs instantly, accepts, starts the trip, updates milestones and completes delivery with proof.

A secure backend powers real-time assignment, tracking, notifications, storage and reporting. The app updates itself from inside, so nobody needs to be sent an APK again.

**Design constraint:** drivers are mostly elderly. The driver experience must be extremely simple, Malayalam-first and tolerant of poor networks.

### 1.1 Reference documents

- [UI blueprint: all Android screens](https://claude.ai/artifact/8rtSxxxku89XF3RVhhp67A) – splash, login, admin, driver and settings designs; the visual source of truth for the app.
- [Business PRD v1](https://claude.ai/artifact/MZKgb4JDFGWwmuhqKUJPbg) – the original business requirements, personas and modules; this document extends it with the technical stack.

Where they differ: the blueprint's driver OTP screen becomes a PIN screen in the free phase (section 24), and the first PRD's AWS, Google Maps and SMS choices are replaced by the free stack below.

### 1.2 Free-first build approach

The first build uses only free services so the client can approve the product before any spend. Every free choice has a named paid upgrade (section 26), and the code stays portable so nothing is rebuilt on upgrade.

- **Database and files:** Supabase free plan.
- **API hosting:** Hugging Face Space (Docker).
- **Maps:** OpenStreetMap data drawn with MapLibre, no Google Maps (section 25).
- **Push:** Firebase Cloud Messaging (free).
- **App updates:** direct in-app updater with APKs on free storage (section 10).
- **Not used yet:** SMS OTP, Redis, Google Play registration, paid monitoring.

## 2. Goals and Non-Goals

### Goals

- Assign jobs through the app with instant notification and a tracked accept/reject.
- Give the owner live visibility of every driver, vehicle and shipment.
- Keep a complete, evidence-backed record of every delivery.
- Deliver a premium, minimal, themeable UI with short texts and no long scrolling pages.
- Ship app updates in-app, with forced-update control for critical fixes.
- Be secure, auditable, and scalable beyond the first fleet.

### Non-Goals (v1)

- Customer-facing app or booking marketplace.
- Billing, GST invoicing, payments (roadmap).
- AI route optimisation and auto load matching.
- iOS app (architecture must not block it later).
- Dedicated GPS hardware integration.

## 3. Users and Roles

| Role | Description | Access |
| --- | --- | --- |
| Super Admin (Owner) | Business owner | Everything, including users, settings, reports, audit logs |
| Dispatcher | Staff who assign jobs | Shipments, drivers, vehicles, live map; no user/role management |
| Driver | Elderly, basic phone skills | Own jobs, own profile only |

## 4. Frontend Technology Decision

### 4.1 Decision

**Chosen: Kotlin + Jetpack Compose (Material 3 with a fully custom design system).**

### 4.2 Why not Flutter, and why Compose

Both can produce a beautiful UI. The decision is driven by what this app depends on most: reliable background work on cheap Android phones.

| Criteria | Jetpack Compose | Flutter | Winner for this project |
| --- | --- | --- | --- |
| Visual quality and custom theming | Excellent; Material 3 plus fully custom tokens | Excellent; pixel-identical rendering | Tie |
| Animation and polish | Strong (Compose animation, Lottie) | Very strong | Slight edge Flutter |
| Background GPS during trips | Native foreground service, direct access to Fused Location | Plugin-based; extra native code and OEM issues | **Compose** |
| Full-screen job alert over lock screen | Native full-screen intent, notification channels | Possible via plugins; more fragile | **Compose** |
| Low-end device performance (2 GB RAM) | Smaller, lighter, native | Larger APK and engine overhead | **Compose** |
| In-app APK install flow | Direct PackageInstaller and Play In-App Updates APIs | Needs platform channels | **Compose** |
| Battery and OEM kill-resistance (Xiaomi, Oppo, Vivo) | Best control | Same limits plus extra layer | **Compose** |
| Future iOS app | Needs a separate build (or Kotlin Multiplatform) | One codebase | Flutter |
| Hiring and long-term Android maintainability | Standard Android skill | Dart skill needed | Compose |

**Summary:** the app is Android-only for drivers, and its value depends on dependable tracking, alerts and updates. Compose gives the safest foundation and still delivers a premium look through a custom design system. If iOS becomes a real requirement later, Kotlin Multiplatform can share the business logic.

### 4.3 Android Stack

| Concern | Choice |
| --- | --- |
| Language | Kotlin |
| UI | Jetpack Compose, Material 3 as base, custom design tokens |
| Architecture | MVVM with unidirectional data flow, Clean Architecture layers (UI, domain, data) |
| Modules | :app, :core-design, :core-network, :core-data, :feature-auth, :feature-admin, :feature-driver, :feature-settings |
| Dependency injection | Hilt |
| Navigation | Navigation Compose with role-based graphs |
| Networking | Retrofit, OkHttp, Kotlin serialization, certificate pinning |
| Real-time | Supabase Realtime (WebSocket) for admin live data; FCM for driver alerts |
| Local storage | Room (offline queue and cache), DataStore (settings, theme, tokens via encrypted storage) |
| Background work | WorkManager (sync, uploads), foreground service (trip tracking) |
| Location | Fused Location Provider, geofencing API |
| Maps | MapLibre Native Android with OpenStreetMap data (no Google Maps) |
| Camera | CameraX for proof photos, with image compression |
| Images | Coil |
| Animation | Compose animation, Lottie for splash and empty states |
| Push | Firebase Cloud Messaging |
| Crash and performance | Firebase Crashlytics and Performance |
| Localisation | Android resources: Malayalam (default for driver) and English |
| Min SDK | Android 8.0 (API 26); target latest stable SDK |
| Build | Gradle with version catalogs, R8 shrinking, baseline profiles |
| Testing | JUnit, MockK, Turbine, Compose UI tests, Maestro for E2E |

## 5. Backend Technology Decision

The free build keeps all data and files in Supabase and runs the API as a Docker container, so it can move to any host later without code changes.

| Layer | Free-phase choice | Notes |
| --- | --- | --- |
| Language and framework | Node.js with NestJS (TypeScript) | Structured modules, large talent pool |
| API style | REST (OpenAPI documented) | Admin live screens use Supabase Realtime instead of a custom WebSocket server |
| Database | Supabase PostgreSQL (free plan) with the PostGIS extension | 500 MB limit; location history is pruned |
| Auth | Admin: email and password. Driver: phone number and admin-issued PIN. API issues short-lived JWTs with rotating refresh tokens | No SMS cost; SMS OTP added after upgrade |
| Realtime | Supabase Realtime (Postgres changes) | Free plan allows 200 concurrent connections |
| File storage | Supabase Storage | 1 GB; proof photos compressed to about 300 KB; APK releases for the updater |
| Cache and job queue | In-process cache and a Postgres-backed queue (pg-boss) | Replaces Redis in the free phase |
| Push | Firebase Cloud Messaging via the Firebase Admin SDK | Free; high-priority data messages |
| SMS | Not used in the free phase | Paid; planned in section 26 |
| Maps services | MapLibre tiles, OpenRouteService, Nominatim | See section 25 |
| API hosting | Hugging Face Space (Docker, CPU Basic) | Sleeps after 48 hours idle, disk not persistent, Docker Spaces may need a paid plan: see section 24 |
| Admin web console | Deferred | Admin uses the Android app in v1 |
| CI/CD | GitHub Actions | Build, test, sign APK, scheduled keep-alive pings |
| Monitoring | Firebase Crashlytics (app), Space and Supabase logs, a free uptime monitor | Paid monitoring later |

**Portability rule:** provider-specific code (storage, push, maps, SMS) lives in adapter modules, configuration comes from environment variables, and schema changes ship as versioned migrations.

## 6. System Architecture

**Clients:** Android app (Admin and Driver roles), optional admin web console.

**Edge:** Hugging Face Space public URL (TLS included); a free Cloudflare proxy is optional.

**Backend services (modular monolith first, split later if needed):**

- **Auth module:** OTP, password login, tokens, roles, device binding.
- **Directory module:** drivers, vehicles, documents.
- **Shipment module:** creation, assignment, state machine, events, proof of delivery.
- **Tracking module:** location ingestion, latest position cache, route history.
- **Notification module:** FCM, repeat-push retries, escalation timers (SMS fallback after upgrade).
- **Reporting module:** aggregates, exports.
- **Release module:** app version registry and update metadata.
- **Audit module:** immutable action log.

**Data stores:** Supabase PostgreSQL (system of record and realtime), in-process cache and Postgres job queue (live state), Supabase Storage (files).

**Key flows**

1. *Assignment:* Admin app calls API, shipment saved, notification job queued, FCM high-priority message sent, driver app shows full-screen alert, driver accepts, API updates state, Supabase Realtime pushes the change to admin screens.
2. *Tracking:* driver app batches location pings, API validates and stores, the latest position is cached and written to a latest-position table, Supabase Realtime streams updates to the admin live map.
3. *Offline:* driver actions are stored in Room with client request IDs and replayed when the network returns; the API is idempotent.

## 7. Functional Requirements

Priority: **P0** launch critical, **P1** important, **P2** later.

### 7.1 Authentication and Security Access

| ID | Requirement | P |
| --- | --- | --- |
| AUTH-1 | Driver login by mobile number and PIN (or SMS OTP after upgrade); long-lived session afterwards | P0 |
| AUTH-2 | Admin and dispatcher login by email and password; optional 2-step code | P0 |
| AUTH-3 | Role-based access control enforced on the server for every endpoint | P0 |
| AUTH-4 | Admin can create, deactivate and reset users; deactivation signs the user out immediately | P0 |
| AUTH-5 | One active device per driver; admin can revoke a device | P1 |
| AUTH-6 | Biometric or PIN app lock for admin | P1 |
| AUTH-7 | Audit log of admin actions | P1 |
| AUTH-8 | **Driver Self-Registration from Login:** Drivers can tap "Register as Driver" on the login screen and complete an onboarding flow with proper verification details (Full Name, Phone, 4-digit PIN, Emergency Contact, Driving Licence number/type/expiry, DL front/back photo, Vehicle association [Company fleet or Owner-operator], DPDP consent). Driver account is created with `PENDING_VERIFICATION` status until approved by Admin. | P0 |
| AUTH-9 | **Admin Driver Approval:** Admin/Dispatcher receives push & in-app alert for newly registered drivers; can inspect submitted details/documents, verify, and approve or reject with reason in one tap. | P0 |

### 7.2 Driver and Vehicle Management

| ID | Requirement | P |
| --- | --- | --- |
| DRV-1 | Add and edit driver: name, phone, photo, emergency contact | P0 |
| DRV-2 | Licence number, type and expiry; document uploads | P1 |
| DRV-3 | Driver list with filters: Available, On trip, Offline, Inactive | P0 |
| DRV-4 | Driver detail: trips, on-time rate, current vehicle | P1 |
| VEH-1 | Add and edit vehicle: registration, type, capacity | P0 |
| VEH-2 | Document expiry tracking (RC, insurance, fitness, permit, PUC) with reminders | P1 |
| VEH-3 | Vehicle status: Available, On trip, Maintenance | P0 |
| VEH-4 | Vehicle-to-driver default pairing with per-trip override | P0 |

### 7.3 Shipments

| ID | Requirement | P |
| --- | --- | --- |
| SHP-1 | Create shipment: pickup, drop, contacts, goods, weight, schedule | P0 |
| SHP-2 | Assign driver and vehicle; warn if busy or over capacity | P0 |
| SHP-3 | Reassign and cancel with reason; driver notified instantly | P0 |
| SHP-4 | Unique readable shipment code | P0 |
| SHP-5 | Saved addresses and recent places | P1 |
| SHP-6 | Duplicate a past shipment | P1 |
| SHP-7 | Attach documents (invoice, e-way bill, LR copy) | P1 |
| SHP-8 | Multi-stop shipments | P1 |
| SHP-9 | Auto-assignment suggestions | P2 |

**State machine:** Created, Assigned, Accepted or Rejected, Reached Pickup, Loaded, In Transit, Reached Drop, Delivered. Exits: Cancelled, Failed. Only valid transitions are allowed; each transition stores time, location and actor.

**Escalation:** if a driver does not respond within a configurable time (default 10 minutes), the admin is alerted, the push is repeated, and the shipment can be reassigned in one tap.

### 7.4 Driver App Experience

| ID | Requirement | P |
| --- | --- | --- |
| APP-1 | Loud full-screen alert for a new job, even on lock screen | P0 |
| APP-2 | Large Accept and Reject buttons; reject needs one tap reason | P0 |
| APP-3 | Start trip, one-tap milestone buttons | P0 |
| APP-4 | One-tap navigation for pickup and drop through the driver's installed maps app (Vela, OsmAnd or others) via a geo intent | P0 |
| APP-5 | Tap-to-call admin, consignor and consignee | P0 |
| APP-6 | Proof of delivery: photo, receiver name, optional signature or OTP | P0 |
| APP-7 | Malayalam-first text, optional voice read-out of job details | P0 |
| APP-8 | Full offline queue with automatic sync | P0 |
| APP-9 | SOS button sends alert and location to admin | P1 |
| APP-10 | Trip history | P1 |
| APP-11 | Expense capture (fuel, toll) | P2 |

### 7.5 Admin App Experience

| ID | Requirement | P |
| --- | --- | --- |
| ADM-1 | Dashboard: on trip, available, pending, delayed counts and live trips | P0 |
| ADM-2 | Live map with all active vehicles, status colours and vehicle sheet | P0 |
| ADM-3 | Shipment list with filters and search; shipment detail with timeline and proof | P0 |
| ADM-4 | Alerts centre: rejects, delays, SOS, expiring documents | P0 |
| ADM-5 | Reports: trips per driver and vehicle, on-time rate, cancelled vs completed | P1 |
| ADM-6 | Export to PDF and Excel | P1 |
| ADM-7 | Trip route playback | P1 |

### 7.6 Live Tracking

| ID | Requirement | P |
| --- | --- | --- |
| TRK-1 | Location reporting only during active trips, with clear driver consent | P0 |
| TRK-2 | Adaptive interval (15 to 60 seconds) based on speed and battery | P0 |
| TRK-3 | Batch uploads; store locally when offline | P0 |
| TRK-4 | Admin map updates within seconds via WebSocket | P0 |
| TRK-5 | Stale location flag when no ping for a set time | P1 |
| TRK-6 | Geofence-based auto arrival suggestion at pickup and drop | P2 |

### 7.7 Notifications

| ID | Requirement | P |
| --- | --- | --- |
| NOT-1 | FCM high-priority data messages for assignment, cancellation, reassignment | P0 |
| NOT-2 | Free phase: repeat the push, then alert the admin to call the driver if the alert is not opened in time (SMS fallback after upgrade) | P1 |
| NOT-3 | Admin notifications for accept, reject, start, delay, delivery, SOS | P0 |
| NOT-4 | In-app notification list | P1 |

## 8. Settings, Themes and Personalisation

- Settings screen: Profile, Theme, Language, Notifications, Security, Help, App version.
- **Themes (user selectable):** Indigo (default), Emerald, Graphite, Sunset; each with Light, Dark and Auto modes.
- Theme choice is stored on the device and synced to the user profile.
- Language: Malayalam and English, switchable at any time.
- Driver mode offers an Extra Large text option.
- All themes meet WCAG AA contrast (4.5:1 for text).

## 9. Design System Requirements

- **Design tokens:** colour, typography, spacing (4 dp grid), radius, elevation and motion, defined once in :core-design and consumed by every screen.
- **Typography:** Plus Jakarta Sans for Latin text; a Malayalam-capable companion font bundled for consistent rendering.
- **Components:** buttons (primary, secondary, danger), cards, status chips, inputs, bottom navigation, bottom sheets, empty states, skeleton loaders, snackbars.
- **Screen rules:** short labels, one primary action per screen, no long scrolling pages, content summarised into cards and bottom sheets.
- **Driver rules:** buttons at least 56 dp, body text at least 18 sp, colour plus icon plus text for status, no hidden gestures, no typing where a button or picker can be used.
- **Motion:** subtle, fast transitions; reduced-motion respected.
- **Screens in scope:** splash, role select, driver OTP/PIN login, driver registration (multi-step simple cards: mobile & PIN, identity & contact, DL details & document capture), registration pending/approval status, admin login, admin dashboard, live map, shipments list and detail, new shipment, drivers list and detail, pending driver approvals, vehicles, reports, alerts, driver home, job alert, active trip, proof of delivery, history, SOS, notifications, profile, settings, themes, language, help, update prompt.

## 10. In-App Update System

Goal: users receive updates inside the app and install with one tap; the team never sends an APK manually.

### 10.1 Distribution Strategy

| Option | How it updates | Pros | Cons | Recommendation |
| --- | --- | --- | --- | --- |
| **A. Google Play (closed or internal testing track, or private app via Managed Google Play)** | Play In-App Updates API shows a native update prompt inside the app; Play can also auto-update in the background | Most reliable, signed and scanned by Google, smooth for older users, no install-permission prompts | Needs a Play developer account and review time | **Production choice after approval (needs a one-time paid Play registration)** |
| **B. Self-hosted APK updater** | App checks the backend, downloads the signed APK and installs via PackageInstaller | Works without the Play Store; instant release | Requires the Install Unknown Apps permission, more security work, not allowed for apps published on Play | **Free-phase choice** |

Because Play policy forbids a Play-distributed app from updating itself outside Play, the project builds two flavors from one codebase: **play** (uses Play In-App Updates) and **direct** (uses the self-hosted updater). The team chooses one channel per deployment; mixing them on the same device is not allowed.

**Free-phase decision:** Play distribution needs a one-time paid registration, so the free build ships the **direct** flavor. APK files are stored in Supabase Storage (or GitHub Releases), listed in the release registry and downloaded by the in-app updater. The first APK is installed once from a link; every later version arrives inside the app. The team moves to the **play** flavor after approval.

### 10.2 Update Policy Requirements

| ID | Requirement | P |
| --- | --- | --- |
| UPD-1 | App checks for updates on launch and on a daily schedule | P0 |
| UPD-2 | **Flexible update:** download in the background, then show a short Restart to update prompt | P0 |
| UPD-3 | **Immediate (forced) update:** blocking full-screen when the installed version is below the minimum supported version | P0 |
| UPD-4 | Backend stores latest version, minimum version and short release notes per flavor | P0 |
| UPD-5 | Release notes shown in two to three short lines, Malayalam and English | P1 |
| UPD-6 | Staged rollout (for example 10%, 50%, 100%) and instant halt | P1 |
| UPD-7 | Never interrupt a trip in progress: non-critical updates wait until the trip ends | P0 |
| UPD-8 | Update status visible to admin (which drivers are on which version) | P1 |

### 10.3 Direct (Self-Hosted) Updater Flow

1. App calls GET /app/version with its flavor, version code and Android version.
2. Backend returns latest version, minimum version, APK URL (short-lived signed URL), SHA-256 checksum, file size and release notes.
3. App shows a short update card; the driver taps Update.
4. App downloads with resume support (Wi-Fi hint, mobile data allowed), with a progress bar.
5. App verifies the SHA-256 checksum, then confirms the APK signing certificate matches the installed app.
6. App installs through the PackageInstaller session API. The first time, the user grants Install unknown apps for TruckConnect; the app guides them with a one-screen explainer.
7. After install the app reports its new version to the backend.

**Constraints to design for**

- Updates must be signed with the same release key; the key is stored in a secure CI secret store and is backed up offline.
- Android may still ask the user to confirm the install; fully silent installs are not guaranteed on normal devices.
- Version codes must always increase; downgrades are blocked.
- A failed or corrupted download never replaces the working app.
- All update traffic uses HTTPS with certificate pinning.

### 10.4 Release Pipeline

- GitHub Actions builds, tests, signs (AAB for Play, APK for direct), and uploads artefacts.
- Release registry in the database records version, checksum, rollout percentage, minimum version and notes.
- Rollback: re-point latest version to the previous build, and raise minimum version only for safety fixes.

## 11. Data Model

- **users** (id, role, name, phone, email, password\_hash, pin\_hash, status ['pending_verification', 'active', 'inactive', 'suspended'], language, theme)
- **drivers** (user\_id, licence\_no, licence\_type, licence\_expiry, emergency\_contact_name, emergency\_contact_phone, emergency\_contact_relation, vehicle\_ownership ['company_fleet', 'owner_operator'], preferred_vehicle_id, approval_status ['pending', 'approved', 'rejected'], rejection_reason, approved_by, approved_at, availability)
- **vehicles** (id, reg\_no, type, capacity\_kg, status, default\_driver\_id)
- **documents** (id, owner\_type, owner\_id, kind ['driving_licence_front', 'driving_licence_back', 'aadhaar_card', 'rc_book', 'insurance_policy'], file\_url, expiry\_date, verified)
- **shipments** (id, code, status, pickup, drop, goods, weight, scheduled\_at, expected\_at, driver\_id, vehicle\_id, created\_by)
- **shipment\_stops** (shipment\_id, seq, type, address, geo\_point, contact\_name, contact\_phone)
- **shipment\_events** (id, shipment\_id, status, actor\_id, geo\_point, note, created\_at, client\_request\_id)
- **location\_pings** (driver\_id, shipment\_id, geo\_point, speed, accuracy, recorded\_at) partitioned by month
- **proofs\_of\_delivery** (shipment\_id, photo\_urls, receiver\_name, signature\_url, otp\_verified)
- **devices** (id, user\_id, fcm\_token, device\_model, app\_version, last\_seen, revoked)
- **notifications** (id, user\_id, type, payload, sent\_at, read\_at)
- **app\_releases** (id, flavor, version\_code, version\_name, min\_supported, checksum, file\_url, rollout\_percent, notes, created\_at)
- **audit\_logs** (id, actor\_id, action, entity, before, after, created\_at)

## 12. API Specification (Summary)

All endpoints are versioned under /api/v1, documented in OpenAPI, and idempotent for writes using a client request ID header.

| Area | Endpoints |
| --- | --- |
| Auth | POST /auth/otp/request, POST /auth/otp/verify, POST /auth/login, POST /auth/driver/register, POST /auth/driver/login-pin, POST /auth/refresh, POST /auth/logout |
| Driver Onboarding | POST /auth/driver/register (submits profile, pin, contact & DL metadata), POST /auth/driver/upload-document (DL front/back, ID), GET /auth/driver/registration-status |
| Admin Approvals | GET /admin/drivers/pending, POST /admin/drivers/{id}/approve, POST /admin/drivers/{id}/reject |
| Users | GET/POST /drivers, GET/PATCH /drivers/{id}, POST /drivers/{id}/deactivate |
| Vehicles | GET/POST /vehicles, GET/PATCH /vehicles/{id} |
| Shipments | GET/POST /shipments, GET /shipments/{id}, POST /shipments/{id}/assign, /reassign, /cancel |
| Driver actions | POST /shipments/{id}/accept, /reject, /start, /milestone, /complete |
| Tracking | POST /tracking/pings (batch), GET /tracking/live, GET /shipments/{id}/route |
| Files | POST /files/presign (upload URL), GET /files/{id} |
| Notifications | GET /notifications, POST /devices/register |
| Reports | GET /reports/summary, GET /reports/export |
| App updates | GET /app/version, POST /app/installed-version |
| Realtime | WebSocket /ws (admin live map, status events, and new driver registration alerts) |

## 13. Non-Functional Requirements

| Area | Target |
| --- | --- |
| Push latency | Under 5 seconds p95 from assignment to driver device |
| API latency | Under 500 ms p95 for standard calls |
| Availability | Best effort on free tiers (no SLA); 99.5% target after upgrade |
| Scale (v1 design) | Free phase: pilot-size fleet (about 10 to 30 drivers) within 500 MB of data. After upgrade: 500 drivers, 50,000 location pings per hour, 10,000 shipments per month, headroom 10x |
| App performance | Cold start under 2 seconds on 2 GB RAM devices; release APK under 25 MB |
| Battery | Under 5% per hour of active tracking |
| Connectivity | Works on 2G and 3G; offline queue and retry; no data loss |
| Security | TLS 1.2+, certificate pinning, encrypted sensitive data at rest, rate limiting, input validation, OWASP Mobile and API Top 10 coverage |
| Privacy | DPDP Act 2023: explicit location consent, purpose limitation, retention policy (for example location history 12 months), deletion on request |
| Backups | Free Supabase has no automatic backups: weekly pg\_dump to separate free storage and a restore test before the pilot; daily backups and point-in-time recovery after upgrade |
| Accessibility | Large text support, TalkBack labels, contrast AA |
| Compatibility | Android 8.0 and above, tested on low-end and mid-range devices from common OEMs |

## 14. Android Platform Requirements

- Foreground service of type location with a persistent notification during active trips.
- Permissions flow explained in short screens: notifications, location (precise, then background only if required), camera, install unknown apps (direct flavor only).
- Guidance screens for battery optimisation exemption on Xiaomi, Oppo, Vivo, Samsung and others.
- Notification channels: Job alerts (max importance, custom sound), Updates, General.
- Full-screen intent and use of the notification as fallback if the full-screen permission is denied.
- R8 obfuscation, Play Integrity check on login for the Play flavor, root and tamper detection warnings for admin.
- Secure storage: Android Keystore for tokens.

## 15. DevOps and Environments

| Environment | Purpose |
| --- | --- |
| Local | Docker Compose with local PostgreSQL and the Supabase CLI stack |
| Staging | Full replica with test FCM and SMS sandbox; used for UAT |
| Production | Hugging Face Space plus a Supabase free project; upgrade path in section 26 |

- Infrastructure kept as code: Dockerfile, Supabase migrations and GitHub Actions workflows.
- Pull request checks: lint, unit tests, API tests, Android build.
- Database migrations run automatically and are reversible.
- Secrets in Hugging Face Space secrets and GitHub Actions secrets; never in the repository.
- Monitoring: uptime checks, error rate, queue depth, push failure rate, location ingestion lag; alerts to the on-call channel.
- Crash-free session target tracked per release.

## 16. Testing Strategy

- **Unit tests:** domain logic, state machine, use cases (target 80% on core).
- **API tests:** every endpoint, role permissions, idempotency, invalid transitions.
- **Android UI tests:** Compose tests for each critical flow; Maestro end-to-end scripts for assignment to delivery.
- **Device lab:** test on low-end phones and OEM skins for notification delivery, background tracking and update flow.
- **Field tests:** real highway drives with weak signal, airplane mode toggles, app killed by the system.
- **Load tests:** location ingestion and push bursts.
- **Security:** dependency scanning, penetration test before rollout.
- **Usability:** at least five real drivers test the driver flow before launch; task success without help is the target.
- **UAT:** owner and dispatchers sign off on staging.

### 16.1 Agent-Assisted QA and UI Verification

The coding agent (Antigravity) tests its own builds on an emulator using [moreplay](https://github.com/JastinXyz/moreplay), an open-source MCP server that lets an agent read the Android screen as text, tap, type, scroll, take screenshots, install an APK and read logs and crashes. Flows the agent records can later be replayed without the AI, locally or in CI.

**Setup**

- Node.js 20 or newer, adb, and an Android emulator (one low-end profile on API 26 and one current) or a spare test phone; run `npx -y moreplay doctor` to verify.
- Add moreplay to Antigravity as an MCP server (command `npx`, args `-y moreplay`) and install it as a dev dependency (`npm i -D moreplay`) so the version is pinned.
- Only one automation tool controls a device at a time: do not run Maestro, Appium or another phone MCP server alongside it.
- Test devices hold no personal data.

**Agent loop for every UI task**

1. Build the debug APK and install it on the emulator.
2. Open each screen in the screen inventory (section 9) and read it with observe; compare labels, order and the primary action with the [UI blueprint](https://claude.ai/artifact/8rtSxxxku89XF3RVhhp67A).
3. Save one screenshot per screen to the evidence folder and review it against the blueprint.
4. Fix mismatches, crashes and layout problems in code, rebuild and repeat from step 2.
5. Replay the saved flows; the task is finished only when every flow passes.

**Flows to record and commit (e2e/mobile)**

| Flow | Covers | Tag |
| --- | --- | --- |
| register-driver | Driver self-registration, PIN setup, DL upload, approval gate | smoke |
| login-admin | Admin sign in, dashboard visible | smoke |
| login-driver | Phone and PIN login, empty driver home | smoke |
| assign-job | Create shipment, pick driver, assign | smoke |
| driver-accept | Job alert, accept, trip starts | smoke |
| proof-delivery | Photo, receiver name, complete | smoke |
| trip-milestones | Start trip through reached drop | regression |
| reject-reassign | Reject with reason, admin reassigns | regression |
| settings-theme | Each of the four themes, light and dark, language switch | regression |
| offline-sync | Network off during a trip, sync after reconnect | regression |
| app-update | Forced-update gate when below the minimum version | regression |

**UI checklist the agent verifies on every screen**

| Check | Standard |
| --- | --- |
| Matches the blueprint | Same labels, order and one primary action |
| Short text, no long scrolling pages | Content fits in cards and sheets |
| Touch targets | At least 56 dp for drivers, 48 dp for admin |
| Text size | At least 18 sp for drivers |
| Clipping | No clipped or overlapping text at default and largest font size |
| Themes | All four themes, light and dark, text contrast AA |
| Languages | Malayalam and English both render without clipping |
| Stability | No crash or error in the logs during any flow |

**Rules**

- After a failed step the agent reads the saved failed-step screenshot and screen text before changing code, makes one fix, then reruns the flow.
- PINs and passwords are flow variables, never written into committed files; the evidence folder is git-ignored; flows are committed.
- CI runs `npx moreplay run e2e/mobile --include-tags smoke --junit reports/mobile.xml` on every pull request once an emulator is available in CI.

**Stays manual:** real low-end phones for background GPS, lock-screen job alerts, battery and phone-brand behaviour; the highway field test; usability testing with real drivers.

## 17. Delivery Plan

| Phase | Scope | Duration (estimate) |
| --- | --- | --- |
| 0. Discovery | Workshops, driver interviews, final screens, data and process sign-off | 2 weeks |
| 1. Foundation | Repos, CI/CD, design system, auth, users, backend skeleton, release registry | 3 weeks |
| 2. Core dispatch | Drivers, vehicles, shipments, assignment, FCM alerts, accept/reject, state machine | 5 weeks |
| 3. Tracking and proof | Live tracking, admin map, milestones, POD, offline sync | 4 weeks |
| 4. Admin insights and settings | Dashboard, alerts, reports, themes, languages | 3 weeks |
| 5. In-app update and hardening | Update flows, security review, performance and battery tuning | 2 weeks |
| 6. Pilot | 5 to 10 drivers in the field, fixes | 3 weeks |
| 7. Rollout | Full onboarding, training, support | 2 to 3 weeks |

Estimates assume a small team: 1 Android engineer, 1 backend engineer, 1 UI/UX designer (part time), 1 QA (part time), and a project lead. Confirm after discovery.

## 18. Success Metrics

| Metric | Target after 90 days |
| --- | --- |
| Shipments assigned through the app | 90% or more |
| Median time from assignment to acceptance | Under 5 minutes |
| Weekly active drivers | 90% or more |
| Deliveries with proof captured | 95% or more |
| Trips with live tracking | 90% or more |
| Crash-free sessions | 99.5% or more |
| Devices on latest or minimum supported version within 7 days of release | 95% or more |

## 19. Risks and Mitigations

| Risk | Impact | Mitigation |
| --- | --- | --- |
| Drivers struggle with the app | Low adoption | Malayalam-first minimal UI, voice cues, training, pilot, admin can update status on behalf of a driver |
| System kills background tracking | Missing location | Foreground service, battery exemption guidance, stale-location flags |
| Missed push notifications | Missed jobs | High-priority FCM, repeat push, admin escalation (SMS fallback after upgrade) |
| Poor network | Delays | Offline queue, batched sync, idempotent API |
| Update install friction (direct flavor) | Drivers stay on old versions | Prefer Play flavor; clear one-screen guide; forced-update gate; admin version visibility |
| Signing key lost or leaked | Cannot ship updates | Offline encrypted backup, restricted CI access, Play App Signing for the Play flavor |
| Free map service limits or outage | Maps or routes stop loading | Caching, call limits, self-hosted tiles later, fallback to the external navigation app |
| Privacy objections | Resistance | Transparent consent, track only during trips, written policy |
| Scope creep | Delay | Phased scope, change-control process |

## 20. Assumptions and Dependencies

- Every driver has an Android smartphone (Android 8 or newer) with mobile data.
- The client owns the Firebase project; Supabase, Hugging Face and GitHub accounts are created under a client-owned email so they can be transferred; Play, domain and SMS accounts come after approval.
- When SMS is introduced, sender and templates are registered under India's DLT rules.
- Client supplies driver, vehicle and sample shipment data for testing.

## 21. Open Questions

1. Can the client use the Play Store (private or closed track) for distribution, or must APKs be installed directly?
2. Number of drivers, vehicles and dispatchers at launch and in two years?
3. Is a desk-based admin web console needed in phase 1, or is admin on the Android app enough?
4. Which proof of delivery is required: photo, signature, OTP, or all?
5. Are billing, trip allowances or expense tracking needed in the first release?
6. Should location be tracked only on trips, or whenever a driver is on duty?
7. Is integration with Tally or e-way bill systems needed?
8. Who provides first-line support and driver training?

## 22. Acceptance Criteria for Launch

- A dispatcher can create and assign a shipment in under one minute.
- A driver receives the alert within five seconds on a good network and sees it on a locked screen.
- A full trip (accept to delivery with photo proof) works with the network lost mid-trip, and syncs correctly afterwards.
- The admin sees live vehicle positions and the shipment timeline.
- A new app version reaches a test device through the in-app update, including a forced update, without manual APK sharing.
- Five pilot drivers complete the core flow without assistance.
- Security test passed with no open critical or high issues.

## 23. Roadmap After v1

- Customer tracking links and delivery notifications.
- Billing, GST invoicing, e-way bill integration, driver payments.
- Admin web console with advanced analytics.
- Route optimisation and auto-assignment.
- GPS and fuel sensor integration.
- iOS app using Kotlin Multiplatform shared logic.
- Multi-branch and multi-company support.

## 24. Free Build Constraints

The free-plan limits below shape the design. They come from the providers' own pages as read in November 2026; re-check them on setup day.

| Service | Free limit | Effect | Mitigation |
| --- | --- | --- | --- |
| Supabase | 500 MB database, 1 GB file storage, 5 GB egress a month, 50,000 monthly active users | Location history and proof photos can fill the plan | Ping every 30 to 60 s; keep 30 days of raw pings, then summarise or delete; compress photos to about 300 KB |
| Supabase | Project paused after 1 week of inactivity | A paused project means a dead app | Scheduled GitHub Actions ping every 3 days; daily use also counts |
| Supabase | 200 concurrent realtime connections; no automatic backups on the free plan | Fine for a small fleet; no safety net | Weekly pg\_dump to separate free storage; restore test before the pilot |
| Hugging Face Space | CPU Basic: 2 vCPU, 16 GB RAM, free; sleeps after 48 hours without visits; disk is not persistent | API may be asleep or restart and lose local files | All state in Supabase; scheduled ping and health endpoint; app retries from its offline queue |
| Hugging Face Space | Current HF documentation says Docker Spaces need a paid plan to create | The planned free API host may not be available on the account | Check on day one. Fallback: move API logic into Supabase Edge Functions (free quota) or another free container host; the NestJS code stays portable |
| Firebase Cloud Messaging | Free | None | None |
| SMS OTP | Paid per message | Driver OTP login is not free | Phone number plus admin-issued PIN in the free phase |
| Google Play | One-time registration fee | Play in-app update flow is not free | Direct updater now; Play flavor after approval |
| OpenStreetMap public services | Fair-use limits on tiles, search and routing | Heavy use can be blocked | See section 25 |

**Free-phase driver login and registration:** the driver can either log in with their registered mobile number and a 4 to 6 digit PIN (set by admin or chosen during self-registration), or tap "Register as Driver" to self-onboard. During registration, the driver supplies their name, mobile, sets a 4-digit PIN, enters emergency contact, driving licence details, and captures DL photos. The account enters `PENDING_VERIFICATION` status until approved by an Admin/Dispatcher. Once approved, the driver logs in with phone + PIN without SMS cost; sessions stay signed in, and admin can reset PIN or revoke access at any time.

## 25. Maps Plan: OpenStreetMap and Vela

**Decision:** draw OpenStreetMap data with MapLibre Native inside the app, and treat Vela as an optional navigation app and design reference, not as code to embed.

### 25.1 What Vela is

Vela is an open-source (GPL-3.0) Android maps and navigation app that describes itself as a Google Maps alternative. It draws open vector tiles built from OpenStreetMap and other open data, and routes through open OSRM or GraphHopper servers. It is a finished app, not a map library. Per its own README it also reads Google's public web endpoints for search, place details and live traffic.

### 25.2 Why we do not embed Vela

- **Licence:** GPL-3.0 requires an app that includes its code to be released under the same licence with source, which conflicts with a private client product.
- **Google dependence:** its traffic, search and place features rely on Google's public web endpoints, which can break without notice and may breach Google's terms. That is neither free of Google nor safe for a business-critical product.
- **Fit:** it is a general consumer navigation app; TruckConnect needs its own shipment pins, vehicle markers and route lines in its own design.

### 25.3 How maps work in TruckConnect

| Need | Free-phase solution | Notes |
| --- | --- | --- |
| In-app map (admin live map, trip route) | MapLibre Native Android (open source, BSD licence) with OpenStreetMap vector tiles | Show the required OpenStreetMap attribution on every map |
| Tile source | A free community vector-tile service, or a Kerala PMTiles extract on free storage; not the public OSM tile server | OSM's own tile server has a strict usage policy and no service guarantee. Week-1 spike: pick one, check file size and MapLibre support |
| Driver turn-by-turn navigation | Geo intent opens the installed navigation app: Vela, OsmAnd or Google Maps if present | No cost and no navigation engine to build; the driver chooses the app in settings |
| Route preview and distance (admin) | OpenRouteService free API key, with a straight-line fallback | Daily quota applies; cache the route per shipment |
| Address search | Nominatim (OpenStreetMap) with debounced input, caching and saved addresses | The public instance allows light use only; self-host later |
| Location | Android Fused Location Provider (free) | Points are stored offline and synced later |

**Rules:** cache aggressively, never call search or routing on every keystroke, and keep every map call behind one wrapper so a provider can be swapped without changing screens.

## 26. Upgrade Path After Business Approval

| Area | Free phase | After approval | Upgrade trigger |
| --- | --- | --- | --- |
| Database | Supabase free | Supabase Pro (listed at 25 USD a month) with backups and no pausing | Go-live, or 400 MB used |
| API hosting | Hugging Face Space or a free container host | Always-on host, for example AWS Mumbai or a paid container service | First real fleet rollout |
| Driver login | Phone number and PIN | SMS OTP through a DLT-registered provider | SMS budget approved |
| App updates | Direct in-app updater | Google Play with the in-app update API | One-time Play registration done |
| Maps | Community tiles, public search and routing | Hosted tile plan, self-hosted routing and geocoding for Kerala | Limits hit or uptime needed |
| Cache and queues | In-process cache, Postgres queue | Managed Redis | Real-time load grows |
| Monitoring | Crashlytics, platform logs, free uptime check | Sentry or Grafana stack with alerts | Pilot sign-off |
| Admin console | Android app only | React web console | Owner asks for desk reporting |

**Portability rules that prevent rework:** all configuration comes from environment variables; provider-specific code stays in adapter modules (storage, push, maps, SMS); database changes ship only as versioned migrations; the Docker image runs on any host.

## 27. Glossary

- **POD:** Proof of Delivery. **LR:** Lorry Receipt. **FCM:** Firebase Cloud Messaging. **AAB:** Android App Bundle. **DLT:** Distributed Ledger Technology registration required for commercial SMS in India. **DPDP:** Digital Personal Data Protection Act, 2023.
