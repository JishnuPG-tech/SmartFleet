# TruckConnect Operations Console — Build and Backend Integration (Antigravity Master Prompt)

Build the **web Operations Console** for TruckConnect as a production-grade React app, wired to the real backend. The approved design and behaviour are in `TruckConnect_Operations_Console_v2.html` (a single-file clickable mockup with fake in-memory data). The backend, API, data model, auth, realtime and map decisions come from `TruckConnect_Full_Project_PRD.md` (v2.1) and from sections 9.1–9.9 of `ANTIGRAVITY_FIX_PROMPT_v2.md` (hosting spike, state machine, RLS, Realtime, retention, security). Read all three first. Start this work only after the Android app and backend pass their own release gates; reuse the same API and fix the API where this console needs more.

Rules that apply throughout: do not rewrite what works; evidence over summaries (paste tool output, say "NOT RUN" when not run); never loosen a threshold or edit a reference to make a check pass; every decision goes in `docs/DECISIONS.md`, every owner question in `docs/OWNER_QUESTIONS.md`.

---

## 1. What the mockup is, and what it is not

The mockup (`v2.html`) shows the look and the flows. Treat these parts as **demo-only and replace them**: seeded random data, the `setInterval` movement simulation, client-side login with listed demo passwords, client-side lockout, client-side PIN generation, the in-browser audit array, "Copy CSV" via clipboard, the schematic Kerala SVG map, `V8` fallback vehicle for approved drivers, and anything stored in JS variables. Everything must come from the API; PINs, lockouts, audit entries and permissions are decided **server-side**.

Views in the mockup: Login · Dashboard · Live map · Shipments (table, filters, detail drawer, create/edit form, cancel-with-reason) · Drivers & vehicles (tabs: Drivers, Vehicles, Approvals; add/edit forms; reset PIN; revoke device) · Reports · Audit log · alerts popover · appearance popover (4 themes × Light/Dark/Auto) · toasts.

Views the mockup does **not** draw but the console must have (design them from the same tokens/components, no new visual language): **Alerts page**, **Releases** (app versions, rollout %, halt, force update, minimum supported), **Team** (admin/dispatcher accounts), **Settings** (profile, change password, 2-step, appearance, notification preferences), **Driver detail** and **Vehicle detail** pages (trips, documents with expiry, device, activity), **Shipment full page** (same content as the drawer plus tabs: Details · Timeline · Route · Documents · Proof · Activity), **Route playback**, **command palette** (Ctrl/⌘ K search across shipments, drivers, vehicles), **saved places / customers** (address book), **empty / error / loading / forbidden / offline** states.

---

## 2. Architecture

* Monorepo package `apps/console`: **React 18 + TypeScript (strict) + Vite**, React Router, **TanStack Query**, React Hook Form + **Zod**, a typed API client **generated from the backend OpenAPI** (no hand-written fetch calls in components), **MapLibre GL JS** for maps, a charting library bundled locally (Chart.js via `react-chartjs-2` is acceptable; match the mockup's chart styling exactly).
* Styling: the mockup's **CSS variables are the design system** (colours, radii, spacing, type, shadows). Use CSS variables + CSS Modules or Tailwind mapped to those tokens; do not import a UI kit that changes the look. Self-host Plus Jakarta Sans; no CDN scripts (strict CSP).
* Folder shape: `app/ (shell, routes) · features/{auth,dashboard,map,shipments,fleet,approvals,alerts,reports,releases,team,audit,settings} · shared/{ui,api,realtime,theme,i18n,utils}`. Each feature owns its queries, forms and tests.
* Config by environment variables (`API_URL`, `SUPABASE_URL`, `SUPABASE_ANON_KEY`, map style URL, ORS proxy); no secrets in the bundle; separate dev/staging/prod.

---

## 3. Themes (exact, same as the Android app)

Tokens are the `:root` blocks in the mockup: 4 accents (Indigo `#4F46E5`, Emerald `#047857`, Graphite `#27272A`, Sunset `#D9480F`; dark variants `#8B83FF`, `#34D399`, `#D4D4D8`, `#FB923C`) × Light / Dark / Auto, plus status colours, chart colours `--c1…--c8` (light and dark sets), map colours, shadows and the `accent-ink` rules. Requirements:
* `data-theme` / `data-mode` are set **before first paint** (inline script) from a local cookie/localStorage value to avoid a flash; Auto follows `prefers-color-scheme` live.
* Choice persists locally **and syncs to the user's profile** (`PATCH /me`), so it matches the phone app after login.
* The login screen has the same theme controls as the mockup.
* Charts, the MapLibre style (light/dark × 4 themes), toasts, drawers, focus rings and scrollbars all follow tokens. Add a unit test that compares computed tokens in all 8 combinations with the mockup's values, and a contrast test (WCAG AA) for text/background pairs.

---

## 4. Authentication, sessions, roles

* **Login screen** exactly as in the mockup (email, password, keep-me-signed-in, theme controls, forgot-password note). Server errors, lockout countdown and validation shown inline; no demo credentials in the product.
* API: `POST /auth/login` (+ `POST /auth/2fa/verify` when TOTP is enabled), `POST /auth/refresh`, `POST /auth/logout`. Access token short-lived and **kept in memory**; refresh token in an **httpOnly, Secure, SameSite cookie** with rotation and reuse detection; CSRF protection on cookie-authenticated routes. Never store tokens in localStorage.
* Lockout and rate limiting server-side (5 failures → 15-minute lock, per account and IP); idle timeout 30 min with a "stay signed in?" dialog; "sign out everywhere"; session list in Settings.
* **Roles (server-enforced; UI only hides)** — defaults, confirm with the owner:

| Capability | Owner | Dispatcher |
|---|---|---|
| Dashboard, map, alerts, reports view | ✔ | ✔ |
| Create/edit/assign/reassign/cancel shipments | ✔ | ✔ |
| Edit in-progress shipments (see 5) | ✔ | ✔ with reason |
| Status override, reopen, correct delivered | ✔ | ✘ |
| Drivers/vehicles add/edit/status | ✔ | ✔ (no deactivate/delete) |
| Driver approvals, reset PIN, revoke device | ✔ | ✘ (view only) |
| Releases, Team, Audit log, company settings | ✔ | ✘ |
| Export reports | ✔ | ✔ |

A forbidden route shows a styled 403 page; the API returns 403 regardless of the UI.

---

## 5. Everything editable — shipment rules

Every field of a shipment is editable from the console (route, addresses, contacts, goods, quantity, weight, schedule, driver, vehicle, notes, documents) subject to these rules, enforced by the API and mirrored in the form:

| Status | Editable | Behaviour |
|---|---|---|
| Created, Assigned, Rejected | All fields | Changing driver re-sends the job push; clearing the driver returns it to Created |
| Accepted → Reached Drop (in progress) | Goods, quantity, weight, contacts, notes, documents, drop address, driver, vehicle, route | **Reason required** for route/driver/vehicle change; confirm dialog; driver notified (push + in-app); capacity/availability checks; shown in Activity |
| Delivered | Notes, documents | Owner-only "correction" with mandatory reason; original values kept in audit |
| Cancelled, Failed | Read-only | Owner can **Reopen** (→ Created) with reason |

* **Optimistic concurrency:** `GET` returns `ETag`/`version`; `PATCH` sends `If-Match`; on 409 the form shows who changed it and offers "reload" / "review differences". Never silently overwrite.
* **Validation:** shared Zod schemas (phone format, positive weight, schedule rules, capacity vs vehicle, same pickup/drop city warning); server remains the authority.
* **Warnings** (non-blocking where the PRD allows): driver busy/on leave/offline, vehicle in maintenance or on another trip, over capacity, expired licence or vehicle documents.
* Add: create, **duplicate** (SHP-6), attach documents (invoice, e-way bill, LR copy; upload via presigned URL, preview, replace), saved places and recent addresses with address search (Nominatim policy: search on submit, cached, no autocomplete), **pick on map**.
* Status overrides (Owner): a typed `POST /shipments/{id}/override-status` with reason; writes a `shipment_events` row with the actor.
* Every mutation writes an `audit_logs` row with **before/after JSON**; the console's Activity tab and Audit page render field-level diffs.

---

## 6. Other admin capabilities (parity with the Android admin app, and more)

| Capability | Console behaviour |
|---|---|
| Assign / reassign | Same form as the mockup; availability + capacity warnings; success toast; driver notified |
| Cancel | Reason select + note (as mockup) → API; driver notified |
| Approvals | Queue of pending driver registrations; review page with licence photos (signed URLs), approve/reject with reason; approval pushes the driver app; require a phone-verification call note (owner decision) |
| Drivers | Add/edit (name, phone, licence no./type/expiry, emergency contact, photo, default vehicle, availability), **reset PIN (server generates, shown once)**, revoke device, deactivate (Owner), trips and on-time history |
| Vehicles | Add/edit (reg no., type, capacity, status, default driver, odometer), documents RC/insurance/fitness/permit/PUC with expiry chips and reminders |
| Live map | MapLibre with real tiles (same tile decision as the app), vehicle markers by status (on trip / delayed / stale), list ↔ map sync, vehicle panel (speed, last update, ETA, Timeline, Call), route polyline, filters, follow mode; Realtime updates |
| Alerts | Page + popover: SOS, rejected, no response, delayed, expiring documents; actions (call, reassign, view, open); mark handled with note |
| Reports | Charts as in the mockup from real aggregates (`/reports/summary?range=`); 90-day targets; **export CSV / Excel / PDF generated by the API** and downloaded (no clipboard) |
| Releases (Owner) | List versions, rollout %, per-driver adoption, halt rollout, force update, set minimum supported; confirm dialogs |
| Team (Owner) | Create dispatcher/admin, reset password, deactivate (signs out immediately), role change |
| Audit (Owner) | Filter by user/action/entity/date, field diffs, export |
| Settings | Profile, change password, 2-step (TOTP + recovery codes), notification preferences, appearance, active sessions |
| Search | Header search + command palette → server-side search over shipments, drivers, vehicles |

---

## 7. Backend integration (what to add or confirm in the API)

Reuse PRD §12 and the additions listed in `ANTIGRAVITY_FIX_PROMPT_v2.md` §9.2, then provide, with OpenAPI + tests:

* `GET /shipments` with **server-side** `q, status[], driver, vehicle, from, to, sort, cursor, limit`; `GET/PATCH /shipments/{id}`; `POST /shipments/{id}/{assign|reassign|cancel|reopen|override-status|duplicate}`; `GET /shipments/{id}/{events|route|documents|proof|activity}`; `POST /shipments/bulk` (assign, cancel) with per-item results.
* Drivers/vehicles/users/approvals/releases/alerts/audit/reports/search/places/files endpoints with the same pagination and error shape.
* **Error format:** RFC 7807 problem+json with field errors (`errors[].field`, `code`), mapped to form fields automatically. **Idempotency-Key** on all writes. Rate limits per role.
* **Exports:** `/reports/export?format=csv|xlsx|pdf` streamed; large exports as background jobs (pg-boss) with a status endpoint.
* **Realtime:** reuse the RLS design (`vehicle_positions`, `shipments`, `alerts`); the console subscribes with a Supabase-compatible JWT minted for admin/dispatcher only; a test proves a driver token cannot subscribe. Events invalidate TanStack Query caches (targeted, not global refetch); reconnect with backoff; a visible "Live / Reconnecting / Offline" indicator replaces the mockup's fake "Live · just now"; stale vehicles flagged by the server threshold.
* **CORS** limited to console origins; **CSP** without `unsafe-inline`; `Referrer-Policy`, `X-Content-Type-Options`; cookie flags as in section 4.

---

## 8. Quality bars

* **Accessibility:** WCAG 2.2 AA; full keyboard use (tables, drawers, modals with focus trap and Esc, command palette); `aria-live` for toasts and realtime updates; visible focus; reduced-motion respected; axe checks in CI.
* **Responsive:** from 360 px to 4K. Sidebar collapses to a rail < 1100 px and a top scroller < 760 px as in the mockup CSS; tables become stacked cards < 760 px; forms single-column on small screens; the map and list stack.
* **Performance:** initial JS ≤ 250 KB gzip, route-level code splitting, LCP ≤ 2.5 s on a mid laptop over fast 3G-throttled test, server-side pagination, virtualised tables > 200 rows, debounced search, no layout shift when data loads (skeletons).
* **Security:** no tokens in web storage, output encoding everywhere (no `innerHTML` with data), dependency scan in CI, signed URLs for private files with short expiry, audit of every write, idle timeout.
* **i18n-ready:** all strings via a message catalogue (English now; Malayalam later), locale-aware dates (en-IN), numbers and units.
* **Observability:** error boundary per route, error reporting service (free tier), web-vitals, structured API request IDs shown in error dialogs.

---

## 9. Verification (how "perfect" is proven)

1. **Visual baseline:** with Playwright, render every view of `v2.html` for 4 themes × light/dark at 1440 × 900 and 390 × 844 into `reference/console/`; compare the real console (seeded with the same fixture data) using a pixel diff (≤ 1.5 % pixels off after a 1 px blur; fixed masks only for chart animation frames and live timestamps). Paste the table.
2. **Click-through test:** every control gets `data-testid="<view>:<id>"`; `docs/console_action_map.csv` lists each control and its expected effect; a Playwright test clicks all of them against a seeded staging backend (no mocks) and asserts the result (route, dialog, API call, cache update). Coverage must be 100 % of controls; unlisted controls fail CI.
3. **Contract tests:** generated client vs OpenAPI; backend e2e for each RBAC cell (Owner/Dispatcher × endpoint) including 403s.
4. **Realtime test:** a simulated driver (synthetic pings) moves a marker; measure end-to-end latency and report it.
5. **Concurrency test:** two sessions edit the same shipment; the second gets the 409 flow.
6. **Cross-product test:** create/edit/assign a shipment in the console → the Android app (emulator) receives the job alert; driver milestones appear in the console timeline within seconds.
7. Unit/component tests (Vitest + Testing Library), axe, Lighthouse, bundle report, dependency scan.

---

## 10. Phases and gates

| Phase | Work | Gate (paste outputs) |
|---|---|---|
| C0 | Repo/package, tokens, theme engine, layout shell, generated client, CI, reference captures | Token tests green in 8 theme/mode combos; shell screenshots match baseline |
| C1 | Auth (login, 2-step, refresh, lockout, idle), RBAC guards, 403/401 handling | Auth e2e + RBAC matrix tests green |
| C2 | Shipments: list, drawer/page, create/edit with rules, assign, cancel, reopen, documents, optimistic concurrency | Click-through rows for shipments 100 %; 409 test; audit diffs visible |
| C3 | Fleet: drivers, vehicles, approvals, PIN reset, revoke, document expiry | Click-through + RBAC tests |
| C4 | Realtime, live map, alerts (page + popover), route playback | Latency report; RLS leak test; map screenshots on all themes |
| C5 | Reports + exports; Releases; Team; Audit; Settings; command palette | Export files opened and described; Releases actions verified against the app updater |
| C6 | Responsive, a11y, performance, error/empty/offline states | Lighthouse, axe, bundle numbers vs budgets |
| C7 | Cross-product tests with the Android app; security review; docs/runbook; staging → production deploy | Cross-product evidence; `docs/console_release_readiness.md` |

Deploy the static build to a free static host (verify current terms; document the choice and the rollback procedure); the API stays on the backend host from the fix prompt. Final report: tables and pasted outputs only — every control, every role, every theme, plus anything NOT RUN.

## 11. Open owner questions (implement the stated default, flag it)

1. Dispatcher permissions (section 4 table). 2. May dispatchers edit in-progress shipments (default: yes, with reason)? 3. Is a verification call mandatory before approving a driver (default: note required)? 4. Console hosting and domain. 5. Export formats and branding (logo, letterhead for PDF). 6. Data retention for audit logs and proofs.
