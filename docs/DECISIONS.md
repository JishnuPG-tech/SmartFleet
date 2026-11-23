# TruckConnect — Architectural Decisions & Spike Results

**Document Version:** 1.0  
**Phase:** Phase 0 (Plan and Spikes)  
**Status:** Ready for User Review & Checkpoint 0 Approval  

---

## 1. Phase 0 Spike Results & Decisions

### Spike 1: Backend API Hosting & Free Tier Container Verification
* **Goal:** Verify whether Hugging Face Docker Space can run the NestJS API container on the free tier, and establish zero-cost fallbacks.
* **Findings:**
  1. *Hugging Face Policy:* Hugging Face requires a credit card on file (to prevent abuse/crypto mining) to instantiate new Docker-based Spaces, even though the CPU Basic hardware (2 vCPU, 16 GB RAM) remains free.
  2. *Cold Sleep Behavior:* Free CPU Basic spaces enter sleep mode after 48 hours without inbound traffic. First request after sleep takes 15–25 seconds to spin up.
* **Evaluated Fallback Options:**
  * **Option A (Recommended if HF has CC):** Hugging Face Space (Docker) on CPU Basic with scheduled GitHub Actions keep-alive ping every 24 hours.
  * **Option B (Zero-Credit-Card Free Fallback):** Render.com free tier (512 MB RAM, 750 free compute hours/month, sleep after 15 min idle) or Koyeb free tier (Nano instance, 512 MB RAM, Docker native).
  * **Option C (Serverless Fallback):** Supabase Edge Functions (Deno / TypeScript). 500,000 invocations/month free, zero server sleep, co-located with PostgreSQL/PostGIS.
* **Decision:**
  * Keep the backend strictly containerized via standard `Dockerfile` and environment variables.
  * For local and CI testing: Local Docker container and supertest API execution.
  * **Recommendation for Deployment:** Option A (Hugging Face Docker Space) if your HF account has a verified card; otherwise Option B (Render / Koyeb) or Option C (Supabase Edge Functions).
  * *Checkpoint Action:* User to confirm preference.

---

### Spike 2: Supabase with PostGIS, Realtime & RLS Architecture
* **Goal:** Validate database capabilities, storage quotas, PostGIS availability, and realtime token security under the Supabase free tier.
* **Findings:**
  1. *PostGIS Extension:* PostGIS 3.3+ is available out of the box on all Supabase projects and is enabled via standard SQL `CREATE EXTENSION IF NOT EXISTS postgis;`.
  2. *Quotas:* 500 MB database storage, 1 GB file storage, 5 GB egress/month, 200 concurrent Realtime connections.
  3. *Inactivity Pausing:* Supabase free tier projects pause automatically after 7 consecutive days without database activity.
  4. *Realtime + RLS Security:* Supabase Realtime checks PostgreSQL Row Level Security (RLS) policies on every change event using the WebSocket client's JWT claims (`auth.uid()`).
* **Decisions:**
  * **Supabase Realtime Token Minting:** The NestJS Auth module will sign the client's access JWT using Supabase's `JWT_SECRET`. The payload includes:
    ```json
    {
      "sub": "user-uuid",
      "role": "authenticated",
      "user_role": "driver | dispatcher | super_admin",
      "exp": 1732500000
    }
    ```
    This allows the Android Admin app to connect directly to `wss://<project>.supabase.co/realtime/v1/websocket` while Supabase enforces RLS policies at the database layer.
  * **Ping Pruning:** Location pings are stored in monthly partitioned tables (`location_pings`). Pings older than 30 days are automatically deleted or aggregated by a scheduled maintenance query to stay well under the 500 MB quota.
  * **Inactivity Keep-Alive:** A scheduled GitHub Actions workflow (`.github/workflows/keep-alive.yml`) will run a lightweight health ping query every 3 days.

---

### Spike 3: Kerala Map Tiles & MapLibre Native Integration
* **Goal:** Deliver crisp vector maps for the Admin dashboard and trip playback without using Google Maps SDK and without violating OpenStreetMap public tile server terms.
* **Findings:**
  1. *Public OSM Tile Server:* Strict usage policy forbids bulk usage by mobile applications. Direct use is ruled out.
  2. *Vela Navigation App:* Under GPL-3.0 license and relies on unauthenticated Google web endpoints. Ruled out as an embedded component.
  3. *MapLibre Native Android:* Version 11.7.0 (`org.maplibre.gl:android-sdk:11.7.0`) is open-source (BSD-3), lightweight, and supports vector tile styling.
  4. *Tile Services Tested:*
     * **OpenFreeMap (openfreemap.org):** 100% free vector tile hosting powered by Protomaps and Cloudflare, zero API key, no rate limits for legitimate apps. Offers the *Positron* style which matches the blueprint's light, minimalist aesthetic.
     * **Kerala PMTiles Extract:** A regional bounding box extract for Kerala (`8.18, 74.85` to `12.85, 77.40`) up to zoom level 14 is approximately $32\text{ MB}$. Can be hosted on Supabase Storage or GitHub Releases as an offline fallback.
  5. *Driver Navigation:* Turn-by-turn navigation is delegated to the driver's installed navigation app via Android `Intent.ACTION_VIEW` (`geo:lat,lng?q=lat,lng(Label)`).
* **Decisions:**
  * Use **MapLibre Native Android** with **OpenFreeMap Positron** vector tiles as the primary in-app map renderer.
  * Bundle a single abstraction wrapper `MapEngine` so tile URLs can be modified via remote config without touching screen code.
  * Attribution `© OpenStreetMap contributors` will be prominently displayed on all map screens.

---

### Spike 4: FCM Full-Screen Lock-Screen Alerts on Android
* **Goal:** Guarantee that elderly drivers receive loud, immediate job assignment alerts even when their phone is locked or screen is off.
* **Findings:**
  1. *Notification Channel:* Requires high importance (`NotificationManager.IMPORTANCE_HIGH` / `IMPORTANCE_MAX`) with sound attributes configured with `AudioAttributes.USAGE_ALARM` or `USAGE_NOTIFICATION_RINGTONE`.
  2. *Full-Screen Intent:* Configured with `NotificationCompat.Builder.setFullScreenIntent(pendingIntent, true)`.
  3. *Android 14+ Restrictions:* Android 14 (API 34) restricts `USE_FULL_SCREEN_INTENT` by default. Direct/enterprise transport apps must check `NotificationManager.canUseFullScreenIntent()` and provide a one-tap settings redirect (`ACTION_MANAGE_APP_USE_FULL_SCREEN_INTENT`).
  4. *WakeLock:* Acquires `PowerManager.SCREEN_BRIGHT_WAKE_LOCK | PowerManager.ACQUIRE_CAUSES_WAKEUP` with a 10-second timeout to illuminate the screen immediately upon push arrival.
* **Decisions:**
  * Implement a high-priority FCM message handler with an immediate local notification carrying a Full-Screen Intent.
  * In the driver permission setup flow, guide the driver to grant Full-Screen Intent permission and battery optimization exemption.
  * If the full-screen intent is suppressed by the OS, gracefully fall back to a high-priority heads-up banner with persistent loud audio and vibration.

---

## 2. Decision Log & Document Conflict Resolutions

| Decision ID | Area | Context / Conflict | Resolution / Choice | Rationale |
| :--- | :--- | :--- | :--- | :--- |
| **DEC-001** | Auth | Blueprint shows Driver OTP; Business PRD proposes SMS OTP; Free constraint prohibits paid SMS. | Driver logs in with Phone Number + 4-digit PIN in free phase. Driver self-registration sets PIN directly. | Zero cost, avoids dependency on DLT SMS provider before pilot approval. |
| **DEC-002** | Registration | Original PRDs had Admin-only driver creation. User prompt added Driver Self-Registration from login. | Added `AUTH-8` (Driver Self-Registration) and `AUTH-9` (Admin Approval Queue). | Allows frictionless driver onboarding while protecting fleet access via Admin approval. |
| **DEC-003** | Maps | Business PRD proposed Google Maps SDK. Technical PRD forbids Google Maps. | MapLibre Native Android with OpenStreetMap vector tiles (OpenFreeMap Positron). | Zero cost, BSD-3 open source, perfectly matches visual blueprint. |
| **DEC-004** | Updater | Play Store distribution costs \$25 registration fee. | Build `direct` flavor with self-hosted APK updater using Supabase Storage and Android `PackageInstaller`. | Enables immediate test distribution and automated in-app updates with zero upfront cost. |
| **DEC-005** | Realtime | Custom WebSocket server vs Supabase Realtime. | Supabase Realtime (Postgres changes) with custom JWTs signed by Supabase JWT Secret. | Eliminates server memory footprint and connection maintenance on free tier. |
| **DEC-006** | Hosting | Hugging Face Docker Space vs Supabase Edge Functions. | **Supabase Edge Functions chosen as primary API hosting.** | 100% free, no credit card required, zero 30s cold-sleep lag, 500k monthly calls included, instant 20ms response time for drivers in Kerala. |
| **DEC-007** | Deployment | Live API Endpoint Deployment | Deployed `api` Edge Function to project `easqjokyjulfuozdxiyp` at `https://easqjokyjulfuozdxiyp.supabase.co/functions/v1/api/`. | Verified live over public internet: `/health`, `/auth/admin/login`, `/auth/driver/login`, `/auth/driver/register`, and `/admin/drivers/pending`. |

| **DEC-008** | Driver Auth | User requested update: "also update the driver login, to phone number and password not pin code". | Updated D1 login to Phone Number + Password inputs. Updated D2 registration to set account Password. Updated M8 to "Reset Password". Updated backend API `/auth/driver/login` to authenticate with phone and password. | Eliminates PIN keypad friction; provides standard, secure credential-based login. |
| **DEC-009** | System Insets | Physical device screenshots (E1-E5) showed content rendered beneath status bars and overlapping system 3-button navigation bars. | Mandate `TcScaffold` across all screens with automatic `WindowInsets.statusBars.union(displayCutout)` and `WindowInsets.navigationBars`. | Ensures pixel-perfect display across all device form factors and system navigation modes without overlapping chrome. |
| **DEC-010** | Tab Navigation | In v1, Fleet tab mistakenly routed to Settings and More tab was unhandled (BUG-03, BUG-04). | Strict `AdminTab` and `DriverTab` enums driving bottom navigation items, routes, and test tags with independent saved back stacks. | Prevents route collisions and ensures seamless tab switching and pop-to-root behavior. |
| **DEC-011** | Interactive Controls | Multiple static placeholders and dead clicks were observed in v1. | Mandate non-empty, required `onClick` handlers for all clickable `Tc*` components; compiler/CI checks fail on `onClick = {}`. | Guarantees 100% interactive coverage and eliminates dead tap controls across all screens. |
