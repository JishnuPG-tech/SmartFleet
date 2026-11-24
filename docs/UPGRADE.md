# TruckConnect / SmartFleet: Comprehensive System & UI Upgrade Specification

> **Architectural Reference**: Synthesized from the deep reverse engineering of industry leaders **Delhivery** (`com.delhiveryConsigneeApp`, React Native/Hermes) and **Shiprocket** (`com.shiprocket.shiprocket`, Flutter/Dart AOT), adapted for Kerala & Pan-India enterprise fleet logistics.

---

## Part 1: Core Logistics System Upgrades (Features & Implementations)

### 1.1 Milestone State Machine & Auditable Handover
* **Irreversible Status Gates**:
  $$\text{Created} \longrightarrow \text{Assigned} \longrightarrow \text{Accepted} \longrightarrow \text{In Transit} \longrightarrow \text{Out for Delivery} \longrightarrow \text{Delivered}$$
  *Exceptions*: $\longrightarrow \text{Delayed} \mid \text{SOS Panic} \mid \text{NDR (Failed Attempt)} \mid \text{Cancelled}$.
* **Driver Acceptance Countdown Timer (10 Minutes)**:
  - When dispatch assigns a consignment, driver's phone triggers an audible high-priority notification with a 10-minute countdown circular progress ring.
  - If unaccepted at $t=10\text{m}$, the backend automatically revokes assignment, moves the order to `Pending Driver Response` in the Operations Console, and notifies backup drivers.

### 1.2 Zero-API-Cost Google Maps Telemetry Bridge
* **Mobile Driver App**: Runs a lightweight Android background service streaming GPS telemetry `(lat, lng, speed, bearing)` to Supabase `truck_live_telemetry` every 10–15 seconds.
* **Driver 1-Tap Navigation Intent**: Bypasses heavy embedded map canvases in the mobile app by launching native Google Maps:
  ```kotlin
  val navIntent = Intent(Intent.ACTION_VIEW, Uri.parse("google.navigation:q=${destLat},${destLng}&mode=d"))
  navIntent.setPackage("com.google.android.apps.maps")
  context.startActivity(navIntent)
  ```
* **Operations Console 1-Click Live Bridge**: Clicking **"Track in Google Maps"** opens a pre-composed Google Maps navigation link displaying the trip origin, destination, and the truck's real-time coordinates.

### 1.3 Delivery Handover Security: OTP & Photo POD
* **Consignee OTP Verification**:
  - The recipient receives a 4-digit system OTP upon consignment dispatch.
  - Driver cannot mark the order `Delivered` without inputting this OTP in the app, eliminating fraudulent or false drops.
* **Camera Photo POD & Digital Signature**:
  - Uses the device camera with an on-screen document/cargo alignment viewfinder to snap unloaded goods at the delivery dock.
  - Recipient signs on a finger-drawn digital canvas. Both are uploaded to Supabase Storage and attached to the audit record.

### 1.4 NDR (Non-Delivery Report) Exception Management (Shiprocket Pattern)
* If delivery is obstructed (*Road Blocked*, *Warehouse Closed*, *Consignee Unavailable*, *Vehicle Breakdown*):
  - Driver selects a structured exception code + optional photo proof.
  - Automatically raises an active ticket in the Operations Console **Alerts Center** (`/alerts`) with 1-click dispatcher actions: *Call Driver*, *Call Consignee*, *Re-route*, or *Reschedule Delivery*.

### 1.5 Batch Pickup Manifest & Thermal QR Label Printing
* **Batch Trip Manifest**: Dispatchers can select 1–25 shipments assigned to a specific truck and click **"Generate Manifest"** to create a printable PDF handover sheet containing vehicle number, driver name, shipment breakdown, total tonnage, and physical signature boxes.
* **4x6" Thermal Shipping Labels**: 1-click printable SVG/PDF label containing high-contrast barcodes and QR codes for rapid dock scanning.

### 1.6 Silent OTA Driver App Updates
* On app launch, the driver app polls `/api/releases/latest`. If a newer version code exists, it displays an in-app banner enabling instant APK download and installation without needing the Google Play Store.

---

## Part 2: Frontend UI & UX Upgrades

### 2.1 Android Driver Mobile App UI Upgrades (Inspired by Delhivery)

1. **Active Job HUD & Bottom Sheet Card**:
   - **Collapsed State**: Sleek bottom bar showing: Destination City, Cargo Weight, Consignee Name, and a pulsing green **"Navigate via Maps"** button.
   - **Expanded State (Swipe Up)**:
     - Direct phone call button with masked customer contact.
     - Cargo manifest breakdown (item count, handling instructions: Fragile / Temperature-sensitive).
     - Full address with highlighted landmark badges (*e.g., "Near IOCL Petrol Pump", "Opposite Milma Booth"*).
2. **Prominent Status Transition Stepper**:
   - Visual vertical milestone stepper with emerald completed checkpoints, active pulsing current status, and gray upcoming points.
3. **Delivery Completion Modal**:
   - Prominent 4-box numeric pin pad for entering the Consignee OTP.
   - Viewfinder overlay for snapping the cargo photo with auto-cropping and flash toggle.
   - Smooth signature canvas with "Clear" and "Confirm Delivery" buttons.
4. **Emergency SOS Floating Action Button (FAB)**:
   - High-contrast red SOS button in the top corner. Long-pressing (3 seconds) triggers immediate dispatcher panic alert with instant GPS coordinates and emergency contacts dialog.

---

### 2.2 Android Admin / Supervisor Mobile App UI Upgrades

1. **Fleet Pulse Dashboard**:
   - Top horizontal summary chips: `Vehicles on Road`, `Delayed (>30m)`, `Unassigned Orders`, `Active SOS`.
2. **Fast 1-Tap Driver Allocation**:
   - Bottom sheet presenting closest available drivers sorted by distance from pickup point, vehicle capacity match, and working hour limits.
3. **Driver KYC & Document Reviewer**:
   - Zoomable document viewer for reviewing driver commercial license and vehicle insurance uploads, with 1-tap **"Approve"** and **"Request Resubmission"** buttons.

---

### 2.3 Operations Console Web App UI Upgrades (Inspired by Shiprocket & Delhivery)

1. **Shipment Lifecycle Tab Bar with Live Counts (Shiprocket Pattern)**:
   - Segmented filter bar on `/shipments`:
     ```
     [ All (24) ] [ Unassigned (3) ] [ Accepted (2) ] [ In Transit (8) ] [ Out for Delivery (4) ] [ Delivered (7) ] [ Exceptions/NDR (1) ]
     ```
   - Each tab features dynamic pill badges with color accents (Amber for Unassigned, Emerald for Active, Crimson for Exceptions).
2. **Live Trip Stepper in Shipment Detail Drawer**:
   - Clicking any shipment row slides out a 440px detail drawer displaying:
     - Visual vertical progress stepper with live timestamps for each milestone.
     - Live Driver Telemetry Card: Driver photo, phone number, vehicle registration (`KL-07-CD-4821`), current speed, and a direct green button: **"Open in Google Maps"** (with external link icon).
3. **Interactive Volumetric vs. Dead Weight Calculator**:
   - In the **New Shipment** modal:
     - Dispatcher inputs cargo dimensions: $L \times W \times H$ (cm) and Dead Weight (kg).
     - Dynamically computes and displays:
       $$\text{Volumetric Weight} = \frac{L \times W \times H}{5000} \text{ kg}$$
     - Automatically highlights whichever is greater as the **Billable Weight** to prevent freight undercharging.
4. **Printable Document Center Modal**:
   - 1-click modal allowing operators to toggle between:
     - **Thermal Shipping Label (4x6")**: High-contrast SVG barcode, origin/destination hubs, consignee details.
     - **Trip Handover Manifest (A4 Sheet)**: Formal multi-shipment chain-of-custody document ready for instant printing (`window.print()`).
5. **Alerts Triage Queue with Quick Resolution Actions**:
   - In `/alerts`:
     - Every alert features actionable inline buttons:
       - SOS: Red flashing banner + **"Emergency Dispatch / Call Driver"**.
       - Delayed Milestone: **"Realise ETA / Re-route"**.
       - Unassigned > 10m: **"Auto-Assign Next Available Driver"**.
       - KYC Expiring: **"Notify Driver via SMS"**.

---

## Part 3: Implementation Roadmap & Execution Order

| Phase | Milestone | Primary Deliverable |
| :---: | :--- | :--- |
| **Phase 1** | **Delivery Security & State Machine** | 4-Digit OTP Handshake + Photo POD Capture + Complete Status Pipeline. |
| **Phase 2** | **Zero-Cost Navigation & Google Maps Bridge** | Mobile Google Maps Intent + Console 1-Click Live Google Maps Telemetry. |
| **Phase 3** | **Exception Handling & NDR Subsystem** | Mobile "Report Issue" Flow + Console Alerts Center Actionable Triage. |
| **Phase 4** | **Printable Manifests & Shipping Labels** | Batch Handover Manifest PDF export + 4x6" Thermal QR Label generator. |
| **Phase 5** | **Realtime Bi-directional Push & OTA Updates** | Supabase Postgres CDC subscriptions + In-App APK update checker. |

---

*Document created and maintained at `docs/UPGRADE.md`.*
