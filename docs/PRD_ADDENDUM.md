# TruckConnect — Product Requirements Document Addendum (v2.0)

This document formalizes additions, modifications, and clarifications to `docs/full-project-prd.md` (v2.1) resulting from the Antigravity Master Prompt v2 and direct owner directives.

---

## 1. Authentication Updates (Direct Owner Directive)
* **Driver Authentication Method:**
  * **Previous Specification (v1):** 4–6 digit numeric PIN with custom on-screen keypad (`D1_driver_login`).
  * **Updated Specification (v2):** Phone Number + Password authentication.
  * **Impact on Screens:**
    * **`D1` (Driver Login):** Updated from numeric keypad to two input fields: **Mobile Number** (`+91` prefix, 10 digits) and **Password** (masked with visibility toggle), plus "Login", "Language Switcher", and "Register as new driver" CTA.
    * **`D2` (Driver Registration Step 1):** Sets mobile number and creates account password (minimum 8 characters, alphanumeric/special).
    * **`M8` (Driver Detail - Admin):** "Reset PIN" action updated to "Reset Password", generating and issuing a secure temporary password.
    * **Backend Auth Endpoints:** `POST /auth/driver/login` accepts `{ "phone": string, "password": string }`.

---

## 2. Universal Insets & System Bars (`TcScaffold`)
* **Window Insets Contract:**
  * All screens must be hosted in `TcScaffold` from `:core-design`.
  * Top bar automatically consumes `WindowInsets.statusBars.union(WindowInsets.displayCutout)`.
  * Bottom navigation automatically consumes `WindowInsets.navigationBars`.
  * Content areas apply safe padding to prevent system 3-button navigation (≡ □ ◁) or gesture bar overlap.
  * Floating action buttons (`FAB`, `SOS`) must be anchored inside the scaffold content bounds, positioned at `bottomNavHeight + navInset + 16.dp`.

---

## 3. Realtime & Live Tracking Architecture
* **Supabase Realtime Token Minting:**
  * The backend signs a Supabase-compatible JWT for admin realtime sessions (`sub`, `role: "authenticated"`, `app_role: "admin"`).
  * Direct client access is restricted via Row Level Security (RLS) on `vehicle_positions`, `shipments`, and `alerts`.
  * Drivers do not subscribe to global vehicle positions.
* **Map Engine:**
  * MapLibre Native SDK replaces static polygon canvas.
  * Vector tiles via OpenStreetMap / PMTiles provider with token-driven light/dark styles matching `TcColors`.

---

## 4. X-Screens Catalog
The following auxiliary screens and bottom sheets are officially added to the application scope:
1. **`X1` Search:** Full search screen with query debounce, recent searches, and filtered results.
2. **`X2` Filter Sheet:** Modal bottom sheet for status, date range, vehicle type, and driver filters.
3. **`X3` Add/Edit Driver:** Full form for driver profile, emergency contacts, licence uploads, and initial password generation.
4. **`X4` / `X4b` Vehicle Management:** Add/edit vehicle and detailed vehicle overview with document expiry chips (RC, Insurance, Fitness, Permit, PUC).
5. **`X5` Cancel Shipment Sheet:** Reason picker chips and cancellation notes.
6. **`X6` New Shipment Wizard:** 3-step creation flow with draft persistence.
7. **`X7` Address Picker:** Nominatim-backed search with map pin placement.
8. **`X9` Audit Log:** Chronological administrative audit trail.
9. **`X10` Proof Viewer & Trip Detail:** High-resolution proof photo viewer and comprehensive milestone timeline.
10. **`X11` Route Playback:** Historical GPS path playback with scrubber and speed multipliers.
11. **`X13` Edit Profile:** Admin profile editor with name, phone, email, and photo.
12. **`X14` Change Password:** Secure password update modal.
13. **`X16` Confirmation Dialogs:** Standardized `TcDialog` for destructive actions.
14. **`X22` Help Articles:** Offline bilingual support documentation.
15. **`X23` Call Chooser Sheet:** Multi-contact selection (Office, Consignor, Consignee).
