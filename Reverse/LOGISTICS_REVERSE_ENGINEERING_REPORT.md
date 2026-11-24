# Logistics Architecture & Reverse Engineering Analysis: Delhivery vs Shiprocket
**Target Applications**:
1. **Delhivery Consignee App** (`com.delhiveryConsigneeApp`, v3.5.3, build 132)
2. **Shiprocket App** (`com.shiprocket.shiprocket`, v9.14.3, build 1182)
**Investigated Under**: REA (Reverse-Engineer-Anything) Framework, JADX 1.5.6, APKTool 3.0.3, Hermes Bytecode v96 Extractor, and Flutter AOT Analyzer.

---

## Executive Summary

| Dimension | Delhivery (`com.delhiveryConsigneeApp`) | Shiprocket (`com.shiprocket.shiprocket`) | TruckConnect Application / Takeaway |
| :--- | :--- | :--- | :--- |
| **Framework & Engine** | **React Native** running on **Hermes Bytecode v96** (`index.android.bundle`, 16.7 MB). | **Flutter** compiled to **Dart AOT ARM64** (`libapp.so`, 19.5 MB + `libflutter.so`). | React + TypeScript web console & Kotlin/Compose mobile app. |
| **Logistics Model** | **3PL First-Party Carrier Network**: Own hubs, own line-haul trucks, own delivery riders. | **4PL Logistics Aggregator / Broker**: Multi-carrier recommendation and brokerage engine across 17+ couriers. | Enterprise fleet operator combining direct fleet tracking with consignee visibility. |
| **Tracking Identity** | Waybill Number (**AWB / WBN**), single carrier chain of custody. | Aggregated Master AWB + Carrier AWB mapping across external partners. | System Shipment UUID (`TC-xxxx`) mapped to Vehicle Reg & Driver ID. |
| **Real-time GPS / Map** | Custom vector/raster map tile server (`raster-v2-maps-pub-int.delhivery.com`) + Firebase Realtime DB. | Normalized tracking milestones; redirects or renders embedded WebView tracking link. | 1-Click Direct Google Maps Bridge (`openGoogleMapsTracking`) with 0 API-key costs. |
| **Delivery Verification** | Dual-channel **OTP** (`/api/v1/package/getOtp`) + Photo POD (`/api/v1/package/parcel-photo/upload`). | Courier-submitted POD + **NDR** (Non-Delivery Report) dispute resolution system. | Mandatory Driver OTP handshake + Signature/Photo POD stored in Supabase. |
| **From/To Routing** | Pincode-to-Pincode direct serviceability matrix (`/api/v1/serviceability/od`). | Dynamic Rate & Serviceability Matrix (`/app/courier/serviceability`) factoring delivery speed, cost, & NDR risk. | Kerala corridor geofenced route calculation with landmark coordinates. |

---

## 1. Delhivery Architecture Deep-Dive

### 1.1 Mobile Stack & Native Bridge
- **Runtime**: Hermes Bytecode Engine version 96.
- **Entrypoint**: `com.delhiveryConsigneeApp.MainActivity` loading `assets/index.android.bundle`.
- **Telemetry & Push**: MoEngage (`com.moengage.firebase.MoEFireBaseMessagingService`) + Firebase Cloud Messaging (`io.invertase.firebase.messaging.ReactNativeFirebaseMessagingService`).
- **Payment & FinTech**: Razorpay Checkout (`com.razorpay.CheckoutActivity`) and MagicX for in-app prepaid and COD payments.
- **Camera & Imaging**: AndroidX Camera2 (`androidx.camera.core.impl.MetadataHolderService`) and UCrop (`com.yalantis.ucrop.UCropActivity`) for package barcode scanning and photo proof capture.

### 1.2 Tracking & Live Telemetry Architecture
Delhivery structures tracking as an event-driven state stream:
1. **Waybill Addressing**: Every package is identified by a numeric Waybill (`wbn` / `awb`).
2. **Real-Time Consignee Sync**: Employs Firebase Realtime Database (`https://consignee-mobile-app-v2-prod-default-rtdb.asia-southeast1.firebasedatabase.app`) for push sync to consignees.
3. **Hyperlocal Driver Tracking**:
   - Detects proximity via events `hyperlocal_driver_arriving_soon` and `hyperlocal_order_delivered`.
   - Driver marker updates use smooth interpolation: `animateToCoordinates(lat, lng)` over polyline paths.
   - Endpoint `/api/v1/eta/route-polyline` returns compressed route geometry from hub to delivery location.
4. **Proprietary Tile Infrastructure**: Rather than paying per-call Google Maps API fees for background rendering, Delhivery hosts its own Indian boundary tile server at `https://raster-v2-maps-pub-int.delhivery.com/styles/india/`.

### 1.3 Delivery Lifecycle & Verification
- **Milestone Sequence**:
  $$\text{Manifested} \longrightarrow \text{In Transit} \longrightarrow \text{Pending Hub Arrival} \longrightarrow \text{Dispatched} \longrightarrow \text{Out For Delivery} \longrightarrow \text{Delivered}$$
- **Consignee Security**:
  - OTP verification endpoints: `/client-profile/otp/resend/+91...` and `/client-profile/otp/verify/+91...`.
  - Photo Proof of Delivery: Uploaded to `/api/v1/package/parcel-photo/upload` with flag `has_parcelPhoto`.
  - Delivery slot management: `/api/ptl/package/slot` allowing consignee to request specific time windows or address revisions (`/api/ptl/package/detailedAddress`).

### 1.4 Direct Shipping & Pricing Engine
- Pincode Serviceability: `/api/dlvlocal/v1/serviceability/city` and `/api/v1/serviceability/od`.
- Weight & Freight: Calculated via `/api/v1/price/charged_weight` and `/api/dlvlocal/v1/estimate`.
- Consignee Labeling: Directly instructs users: *"We recommend you to print & paste shipping label on the package or else write the AWB number with a marker."*

---

## 2. Shiprocket Architecture Deep-Dive

### 2.1 Mobile Stack & Flutter Engine
- **Runtime**: Flutter Dart AOT compiled engine (`libflutter.so` and `libapp.so`).
- **Backend Host**: `https://apiv2.shiprocket.co/v1`.
- **Native Android Shell**: `com.shiprocket.shiprocket.MainActivity` supporting barcode scanning, thermal printing integrations, and SMS/contact syncing.

### 2.2 The 4PL Aggregator Engine (Multi-Courier Orchestration)
Shiprocket's core engineering advantage is its multi-courier brokerage and normalization engine:
1. **Dynamic Courier Recommendation (CORE)**:
   - When a shipment is created, Shiprocket queries `/app/courier/serviceability` and `/courier/ratingserviceability`.
   - Evaluates:
     - Cost (freight charges per 500g slab).
     - Delivery speed (Air vs Surface).
     - Courier historical performance rating (Delhivery, BlueDart, Shadowfax, Xpressbees, DTDC, Ecom Express).
     - Pickup & Delivery Pincode operational status.
2. **Batch Operations & Manifesting**:
   - Order States: `Courier Assigned` $\rightarrow$ `Pickup Scheduled` $\rightarrow$ `Manifested` $\rightarrow$ `In Transit`.
   - Manifest generation: `/app/manifests/manifestforpickup` produces legal handover sheets that delivery drivers physically sign when collecting packages from merchant warehouses.
   - Thermal Label Generation: `/courier/generate/label` outputs 4x6" PDF shipping labels with AWB barcode, routing hub codes, and COD tags.

### 2.3 Non-Delivery Report (NDR) & RTO Defense System
NDR is Shiprocket's most sophisticated logistics subsystem, designed to mitigate eCommerce return losses:
1. **NDR Trigger**: When a courier reports an unsuccessful delivery attempt (e.g. *Customer Unavailable*, *Wrong Address*, *Customer Rejected*), the system triggers:
   - `/shipments/ndr/list`
   - `/seller/ndr/ndr-action-required`
2. **Automated Buyer Outreach**:
   - Initiates automated IVR call or WhatsApp message asking the buyer to confirm intent (`/ndr/update/buyerIntent`).
   - Allows buyer to provide a landmark or updated delivery time (`/ndr/update/deliveryAddress`).
3. **Courier Accountability**:
   - Requires the courier to submit proof of attempt: `/shipments/ndr/upload-proof` (geo-tagged image of door or audio call log).
4. **RTO Risk Score Engine**:
   - Endpoints `/rto-score` and `/rto-score-redirect` run predictive risk modeling on COD buyers based on phone number and pincode cancellation history.

### 2.4 Volumetric Weight Dispute Engine
- **Formula**:
  $$\text{Volumetric Weight (kg)} = \frac{\text{Length (cm)} \times \text{Width (cm)} \times \text{Height (cm)}}{5000}$$
- **Dispute Workflow**:
  - Couriers laser-scan packages at sorting hubs. When courier weight exceeds merchant declared weight, excess freight is charged.
  - Endpoints `/v1/app/weightdispute/check`, `/v1/app/weightdispute/accept`, and `/pid_reject_discrepancy` allow automated resolution with photo evidence.

---

## 3. Blueprint & Recommendations for TruckConnect / SmartFleet

| Feature Category | Industry Standard (Delhivery & Shiprocket) | TruckConnect Implementation Strategy |
| :--- | :--- | :--- |
| **Tracking Architecture** | Unified AWB tracking with event-driven milestones and normalized status. | Store shipment milestones (`Created` $\rightarrow$ `Assigned` $\rightarrow$ `Accepted` $\rightarrow$ `In Transit` $\rightarrow$ `Out for Delivery` $\rightarrow$ `Delivered` $\rightarrow$ `Delayed` / `SOS`) in Supabase with timestamped audit records. |
| **Live Map & Telemetry** | Delhivery maintains costly custom tile servers; Shiprocket redirects to web tracking. | **1-Click Direct Google Maps Bridge**: Zero-API-key redirect (`openGoogleMapsTracking`) leveraging native Android & browser Google Maps apps with direct vehicle coordinates. |
| **Proof of Delivery (POD)** | Dual-factor OTP + camera capture of parcel and recipient signature. | Driver app records recipient signature + mobile camera POD photo, requiring numeric OTP before marking shipment `Delivered`. |
| **Driver Handover & Manifest** | Batch manifest document generated for pickup handover. | Dispatcher generates printable dispatch manifest sheet grouping assigned consignments for a single truck route. |
| **Exception Handling (NDR)** | Non-Delivery Reports with interactive buyer re-scheduling. | SmartFleet Alerts Center (`/alerts`) with instant triage for SOS signals, delays, and unacknowledged dispatches. |

---

*Report generated and archived at `Reverse/LOGISTICS_REVERSE_ENGINEERING_REPORT.md`.*
