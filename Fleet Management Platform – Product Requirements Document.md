# Fleet Management Platform – Product Requirements Document

**Product:** TruckConnect (working name) – Android driver app, admin console and backend **Version:** 1.0 (Draft for client review) **Region:** Kerala, India **Status:** Draft

---

## 1. Executive Summary

The client operates a goods transport business moving consignments by truck and lorry between locations in Kerala. Today, job assignment, driver coordination and delivery confirmation happen through phone calls and in-person conversations. This is slow, undocumented, and gives the owner no live visibility.

This product digitises the workflow: an **Admin** creates and assigns shipments; the assigned **Driver** is notified instantly in an Android app, accepts, starts the trip, and completes delivery; the Admin sees every driver, vehicle and shipment in real time.

The defining design constraint: **drivers are mostly elderly and have limited digital literacy.** The driver app must be extremely simple, large, mostly Malayalam-language and tolerant of poor connectivity.

## 2. Goals and Non-Goals

### 2.1 Goals

- Replace phone-call based dispatch with in-app assignment and instant notification.
- Give the owner a live view of drivers, vehicles and shipment status.
- Provide a reliable record of every shipment (who, what, where, when, proof of delivery).
- Deliver a driver experience usable by people with minimal smartphone experience.
- Build a secure, scalable foundation that can grow from one fleet to many.

### 2.2 Non-Goals (v1)

- Customer-facing tracking app or marketplace for third-party shippers.
- Online payments, invoicing or GST billing (planned for later phases).
- Automated route optimisation and AI load matching.
- iOS driver app.
- Hardware GPS/IoT device integration (phone GPS only in v1).

## 3. Users and Personas

| Persona | Description | Key needs |
| --- | --- | --- |
| **Owner / Super Admin** | Business owner; full control | Live visibility, reports, control of users and data |
| **Dispatcher / Admin** | Staff who create and assign jobs | Fast job creation, driver availability, status updates |
| **Driver** | Typically 45–65 years old, basic phone skills, Malayalam speaking | Clear job details, one-tap actions, navigation, minimal typing |
| **Customer (future)** | Consignor / consignee | Tracking link, delivery confirmation |

## 4. Problem Statement

- Assigning jobs requires calling each driver individually; there is no record.
- The owner cannot see where vehicles are or whether a delivery is completed without calling.
- Disputes about delivery, delays and load details cannot be resolved without evidence.
- Driver availability is unknown at the time of assignment.

## 5. Product Scope Overview

The platform has three components:

1. **Driver Android App (APK)** – receive, accept, execute and complete jobs.
2. **Admin Web Console** (primary) and optional Admin mobile view – manage drivers, vehicles, shipments, tracking and reports.
3. **Backend Services** – APIs, real-time messaging, notifications, tracking, storage, security.

> **Recommendation:** The owner will mostly work from a desk or phone. Build the admin as a responsive web console first. An admin Android section can be added within the same app by role if the owner insists on mobile only.

## 6. Core User Journey

1. Admin logs in and creates a shipment (pickup, drop, goods, weight, schedule).
2. Admin selects an available driver and vehicle and assigns the job.
3. Driver receives a push notification with sound and a full-screen job alert.
4. Driver taps **Accept** (or **Reject** with a reason).
5. Driver taps **Start Trip**; live location sharing begins.
6. Driver updates milestones: Reached Pickup, Loaded, In Transit, Reached Drop.
7. Driver completes delivery with proof (photo, receiver name, optional signature/OTP).
8. Admin sees the status change live and the shipment is archived with a full timeline.

## 7. Functional Requirements

Priority key: **P0** = launch critical, **P1** = important, **P2** = later.

### 7.1 Authentication and Access Control

| ID | Requirement | Priority |
| --- | --- | --- |
| AUTH-1 | Phone number + OTP/PIN login for drivers (no passwords to remember) | P0 |
| AUTH-2 | Admin login with email/phone + password and optional 2FA | P0 |
| AUTH-3 | Role-based access control: Super Admin, Dispatcher, Driver | P0 |
| AUTH-4 | Admin can create, deactivate and reset a driver account | P0 |
| AUTH-5 | Long-lived secure sessions on driver device (avoid repeated login) | P0 |
| AUTH-6 | Device binding: one active device per driver, admin can revoke | P1 |
| AUTH-7 | Audit log of admin actions | P1 |
| AUTH-8 | Driver Self-Registration: accessible directly from the Driver Login screen. Driver enters Name, Phone, sets 4-digit PIN, Emergency Contact, Driving Licence details (DL No, Type, Expiry), and uploads DL photos. Driver remains in pending verification status until approved by Admin. | P0 |
| AUTH-9 | Admin Approval for Self-Registered Drivers: Admin receives alerts for pending driver registrations, reviews submitted details and documents, and approves or rejects with reason. | P0 |

### 7.2 Driver Management (Admin)

| ID | Requirement | Priority |
| --- | --- | --- |
| DRV-1 | Add/edit driver: name, phone, photo, address, emergency contact | P0 |
| DRV-2 | Store licence number, licence type, expiry date, Aadhaar (masked) and documents | P1 |
| DRV-3 | Driver list with search and filters: Available, On Trip, Offline, Inactive | P0 |
| DRV-4 | Driver profile: assigned vehicle, trip history, ratings, on-time stats | P1 |
| DRV-5 | Alerts for licence and document expiry (30/15/7 days) | P1 |
| DRV-6 | Driver availability status (Available, On Leave, On Trip) | P0 |

### 7.3 Vehicle Management (Admin)

| ID | Requirement | Priority |
| --- | --- | --- |
| VEH-1 | Add/edit vehicle: registration number, type, capacity (tonnes), body type | P0 |
| VEH-2 | Track documents: RC, insurance, fitness, permit, PUC, with expiry reminders | P1 |
| VEH-3 | Link vehicle to default driver; allow per-trip override | P0 |
| VEH-4 | Vehicle status: Available, On Trip, Maintenance | P0 |
| VEH-5 | Maintenance and service log | P2 |

### 7.4 Shipment / Job Management (Admin)

| ID | Requirement | Priority |
| --- | --- | --- |
| SHP-1 | Create shipment: pickup and drop locations, contact persons and phones | P0 |
| SHP-2 | Goods details: description, quantity, weight, special instructions | P0 |
| SHP-3 | Scheduled pickup date/time and expected delivery time | P0 |
| SHP-4 | Assign driver and vehicle; system warns if driver busy or vehicle over capacity | P0 |
| SHP-5 | Reassign or cancel a shipment with reason; driver is notified | P0 |
| SHP-6 | Unique shipment ID (e.g. TC-2026-000123) | P0 |
| SHP-7 | Saved addresses and frequent customers for quick re-entry | P1 |
| SHP-8 | Multi-stop shipments (multiple pickups/drops) | P1 |
| SHP-9 | Duplicate a previous shipment | P1 |
| SHP-10 | Attach documents (e-way bill, invoice, LR copy) | P1 |
| SHP-11 | Auto-assignment suggestion based on proximity and availability | P2 |

### 7.5 Shipment Lifecycle and Status

**States:** Created → Assigned → Accepted / Rejected → Reached Pickup → Loaded → In Transit → Reached Drop → Delivered. Terminal alternates: Cancelled, Failed.

| ID | Requirement | Priority |
| --- | --- | --- |
| LIF-1 | Only valid state transitions allowed; each transition timestamped with location | P0 |
| LIF-2 | If a driver does not respond within a configurable time (e.g. 10 minutes), admin is alerted and may reassign | P0 |
| LIF-3 | Rejected jobs return to the admin queue with the reason | P0 |
| LIF-4 | Full shipment timeline visible to admin | P0 |
| LIF-5 | Delay detection against expected delivery time | P1 |

### 7.6 Driver Android App

| ID | Requirement | Priority |
| --- | --- | --- |
| APP-1 | Instant push notification with loud alert on new assignment | P0 |
| APP-2 | Job card with pickup, drop, goods, contact numbers, and map shortcut | P0 |
| APP-3 | Large **Accept** and **Reject** buttons | P0 |
| APP-4 | **Start Trip**, then one-tap milestone updates with large buttons | P0 |
| APP-5 | Open Google Maps navigation to pickup/drop with one tap | P0 |
| APP-6 | Tap-to-call admin and consignee/consignor contacts | P0 |
| APP-7 | Complete delivery: photo of goods/POD, receiver name, optional signature or OTP | P0 |
| APP-8 | Malayalam as default language, English optional; voice prompts for new jobs | P0 |
| APP-9 | Offline support: actions are queued and synced when network returns | P0 |
| APP-10 | Job history list (past trips) | P1 |
| APP-11 | SOS / help button that alerts admin with location | P1 |
| APP-12 | Expense logging (fuel, toll) with photo | P2 |

### 7.7 Live Tracking

| ID | Requirement | Priority |
| --- | --- | --- |
| TRK-1 | Background GPS reporting during active trips (adaptive interval, 15–60 s) | P0 |
| TRK-2 | Admin live map showing all active vehicles with status colour | P0 |
| TRK-3 | Click a vehicle to see driver, shipment, speed, last update, ETA | P0 |
| TRK-4 | Trip route history playback | P1 |
| TRK-5 | Offline gap handling; flag stale locations | P1 |
| TRK-6 | Geofence auto-arrival at pickup/drop | P2 |
| TRK-7 | Battery-aware tracking and clear driver consent | P0 |

### 7.8 Notifications and Communication

| ID | Requirement | Priority |
| --- | --- | --- |
| NOT-1 | FCM push notifications for assignment, cancellation, reassignment | P0 |
| NOT-2 | Fallback SMS or WhatsApp message if the driver has not opened the push within X minutes | P1 |
| NOT-3 | Admin notifications: accepted, rejected, started, delayed, delivered, SOS | P0 |
| NOT-4 | Notification centre in app and web | P1 |

### 7.9 Admin Dashboard and Reports

| ID | Requirement | Priority |
| --- | --- | --- |
| DSH-1 | Dashboard: drivers available/on trip, active shipments, pending assignments, delays | P0 |
| DSH-2 | Shipment list with filters (status, date, driver, vehicle, route) and search | P0 |
| DSH-3 | Reports: trips per driver/vehicle, on-time %, completed vs cancelled | P1 |
| DSH-4 | Export to Excel/CSV and PDF | P1 |
| DSH-5 | Proof of delivery viewer and download | P0 |
| DSH-6 | Driver and vehicle utilisation analytics | P2 |

## 8. UX Requirements for Elderly Drivers

This is the make-or-break area of the product.

- **Minimal screens:** Home shows only the current job, or "No job right now". Maximum 3 taps for any action.
- **Large touch targets:** minimum 56 dp buttons, large fonts (minimum 18 sp), high contrast, optional dark mode off by default.
- **Colour plus icon plus text:** green Accept, red Reject; never colour alone.
- **Malayalam-first:** all labels and messages localised; avoid English jargon.
- **Voice and sound:** audible alert and optional voice read-out of the job in Malayalam.
- **No typing wherever possible:** use buttons, pickers, camera. Phone number OTP auto-read.
- **Forgiving design:** confirmation on destructive actions, easy undo, no hidden gestures.
- **Onboarding:** admin assists first login; short in-app video tutorial; optional WhatsApp support link.
- **Low-end device support:** runs smoothly on Android 8+ devices with 2 GB RAM.
- **Usability testing** with at least 5 actual drivers before launch.

## 9. Non-Functional Requirements

| Area | Requirement |
| --- | --- |
| Performance | Push delivered to driver in under 5 s (p95); API responses under 500 ms (p95) |
| Availability | 99.5% backend uptime in v1, target 99.9% later |
| Scalability | Support 500 drivers and 50,000 location updates per hour in v1 architecture; horizontally scalable |
| Connectivity | Works on 2G/3G and patchy highway coverage; offline queue and retry with idempotent APIs |
| Security | TLS everywhere, encrypted sensitive data at rest, hashed credentials, JWT with refresh tokens, rate limiting, input validation |
| Privacy | Compliance with India's DPDP Act 2023: consent for location tracking, purpose limitation, retention policy, data deletion on request |
| Reliability | Automated daily backups, point-in-time recovery, disaster recovery plan |
| Compatibility | Android 8.0 (API 26) and above |
| Auditability | Immutable status history and admin audit trail |
| Battery | Tracking should consume under 5% battery per hour of active trip |
| Localisation | Malayalam and English; date/time in IST |
| Observability | Centralised logging, metrics, crash reporting, alerting |

## 10. Technical Architecture (Recommended)

### 10.1 Suggested Stack

| Layer | Recommendation | Notes |
| --- | --- | --- |
| Android app | Kotlin with Jetpack Compose, or Flutter | Flutter gives a single codebase for a future iOS or admin app; native Kotlin gives best background-location reliability |
| Admin console | React / Next.js | Responsive web |
| Backend | Node.js (NestJS) or Python (FastAPI/Django) | REST plus WebSocket |
| Database | PostgreSQL with PostGIS | Relational data and geo queries |
| Cache and queue | Redis | Live location cache, job queues |
| Real-time | WebSocket / Socket.IO or managed pub-sub | Live map updates |
| Push | Firebase Cloud Messaging | High-priority data messages |
| Maps | Google Maps SDK / Directions API | Consider cost; OpenStreetMap alternative |
| File storage | S3-compatible object storage | POD photos, documents |
| Hosting | AWS Mumbai (ap-south-1) or equivalent Indian region | Data residency and low latency |
| CI/CD | GitHub Actions | Automated build, test, release |

### 10.2 Key Components

- **API Gateway / Backend:** auth, shipments, drivers, vehicles, reporting.
- **Tracking Service:** ingests location pings, stores latest position in Redis and history in PostgreSQL (partitioned or time-series).
- **Notification Service:** FCM, SMS/WhatsApp fallback, retry logic.
- **Admin Web App:** dashboards, live map, management screens.

### 10.3 Android Technical Considerations

- Use a foreground service with a persistent notification for location during trips.
- Guide drivers through battery-optimisation exemptions (OEM-specific on Xiaomi, Oppo, Vivo, Samsung).
- Use FCM high-priority messages and a full-screen intent for new assignments.
- Local database (Room/SQLite) for offline queue and sync.
- Distribute via Play Store (private or closed track) plus direct APK with in-app update prompts.

## 11. Data Model (High Level)

- **User** (id, role, name, phone, email, status)
- **Driver** (user\_id, licence details, documents, availability, device token)
- **Vehicle** (id, reg\_no, type, capacity, documents, status)
- **Shipment** (id, code, pickup, drop, goods, schedule, status, assigned\_driver, assigned\_vehicle, created\_by)
- **ShipmentStop** (shipment\_id, sequence, type, address, geo, contact)
- **ShipmentEvent** (shipment\_id, status, timestamp, geo, actor, notes)
- **LocationPing** (driver\_id, shipment\_id, lat, lng, speed, accuracy, timestamp)
- **ProofOfDelivery** (shipment\_id, photo\_urls, receiver\_name, signature, otp\_verified)
- **Document** (owner\_type, owner\_id, type, file\_url, expiry)
- **Notification** (user\_id, type, payload, sent\_at, read\_at)
- **AuditLog** (actor, action, entity, before, after, timestamp)

## 12. API Overview

| Area | Sample endpoints |
| --- | --- |
| Auth | POST /auth/otp/request, POST /auth/otp/verify, POST /auth/refresh |
| Drivers | GET/POST /drivers, GET/PATCH /drivers/{id} |
| Vehicles | GET/POST /vehicles, GET/PATCH /vehicles/{id} |
| Shipments | GET/POST /shipments, PATCH /shipments/{id}/assign, POST /shipments/{id}/cancel |
| Driver actions | POST /shipments/{id}/accept, /reject, /start, /milestone, /complete |
| Tracking | POST /tracking/pings (batch), GET /tracking/live, GET /shipments/{id}/route |
| Reports | GET /reports/summary, GET /reports/export |

All write APIs must be **idempotent** (client-generated request IDs) to survive retries on bad networks.

## 13. Success Metrics (KPIs)

| Metric | Target (90 days post launch) |
| --- | --- |
| Shipments assigned via app instead of calls | ≥ 90% |
| Average time from assignment to driver acceptance | < 5 minutes |
| Driver app weekly active usage | ≥ 90% of drivers |
| Deliveries with proof of delivery captured | ≥ 95% |
| Shipments with live tracking active | ≥ 90% |
| Driver app crash-free sessions | ≥ 99.5% |
| Admin time spent on dispatch per shipment | Reduced by ≥ 50% |

## 14. Release Plan and Milestones

| Phase | Scope | Indicative duration |
| --- | --- | --- |
| 0. Discovery | Requirement workshops, driver interviews, wireframes, final scope sign-off | 2 weeks |
| 1. MVP | Auth, drivers, vehicles, shipment create/assign, push, accept/start/complete, basic admin dashboard | 8–10 weeks |
| 2. Tracking and POD | Live tracking, map, proof of delivery, offline sync, reports | 4–6 weeks |
| 3. Pilot | 5–10 drivers in the field, feedback, fixes | 3–4 weeks |
| 4. Rollout | Full fleet onboarding, training, support | 2–4 weeks |
| 5. Enhancements | Billing, multi-stop, expenses, customer tracking links | Ongoing |

Durations are estimates for a small team and should be confirmed after discovery.

## 15. Testing and Quality Strategy

- Unit and API tests for all backend modules; target 80% coverage on core logic.
- End-to-end tests for the shipment lifecycle.
- Device matrix testing on popular low-end Android devices and OEM skins.
- Field tests for background tracking on real highways with weak signal.
- Load testing for location ingestion and push bursts.
- Security testing: OWASP checks, penetration test before rollout.
- User acceptance testing with the owner, dispatcher and real drivers.

## 16. Risks and Mitigations

| Risk | Impact | Mitigation |
| --- | --- | --- |
| Drivers struggle with the app | Adoption failure | Malayalam-first minimal UI, training, voice cues, pilot testing, admin can mark status on behalf of driver as a fallback |
| Android kills background tracking | Missing locations | Foreground service, battery whitelist guidance, stale-location flags |
| Poor network on highways | Delayed updates | Offline queue, batched sync, SMS fallback |
| Notifications delayed or missed | Missed assignments | High-priority FCM, SMS/WhatsApp fallback, escalation alert to admin |
| Driver privacy concerns | Resistance | Transparent consent, track only during active trips, clear policy |
| Google Maps cost overrun | Budget | Cache, limit API calls, evaluate OSM-based alternatives |
| Scope creep | Delay | Strict phased scope and change-control process |
| Shared or lost phones | Security | Device binding, admin revoke, OTP re-login |

## 17. Assumptions and Dependencies

- Every driver has, or will be given, an Android smartphone with mobile data.
- The client provides the driver list, vehicle details and sample shipments for testing.
- Third-party services used: Firebase, Google Maps, an SMS gateway (e.g. MSG91 or Twilio).
- Google Play developer account and domain/hosting accounts are owned by the client.

## 18. Open Questions for the Client

1. How many drivers, vehicles and dispatchers at launch and in two years?
2. Is the owner comfortable with a web admin console, or must admin also be an Android app?
3. Do shipments have fixed customers, or can any customer book? Does the customer need tracking?
4. Are multi-stop and return-load trips common?
5. Is proof of delivery needed as photo, signature, OTP or all of these?
6. Are billing, driver payments (batta) or expense tracking required in phase 1?
7. Do drivers use their own phones, and what is the typical phone quality?
8. Is tracking needed only during trips, or whenever the driver is on duty?
9. Is integration needed with existing accounting software (Tally) or e-way bill systems?
10. Who handles driver training and first-line support?

## 19. Future Roadmap

- Customer tracking links and notifications.
- Billing, invoicing, GST and e-way bill integration.
- Driver payments, trip allowances and expense settlement.
- Fuel and maintenance analytics; GPS/IoT hardware integration.
- Route optimisation, load matching and auto-assignment.
- Multi-branch and multi-company (SaaS) support.
- iOS app and WhatsApp-based job assignment.

## 20. Glossary

- **POD:** Proof of Delivery.
- **LR:** Lorry Receipt, the transport document for a consignment.
- **FCM:** Firebase Cloud Messaging.
- **RBAC:** Role-Based Access Control.
- **DPDP:** Digital Personal Data Protection Act, 2023 (India).
