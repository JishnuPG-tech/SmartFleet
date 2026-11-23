# TruckConnect — Fix, Complete and Productionize (Antigravity Master Prompt v2)

The first build of the app exists, but it is **not acceptable**. Screenshots from a real phone (`evidence/bugs/E1…E5`, `evidence/build_vs_reference.png`) show broken system-bar handling, dead buttons, wrong navigation, broken icons, a fake map and screens that do not match the approved design. Your job is to **fix every defect, finish every unfinished flow, and turn the app into a fully functional, production-ready, enterprise-grade Android app** — without changing the approved look.

Read this entire file before touching code. Work in the phases in section 12. Do not skip gates. Do not claim anything works without the tool output that proves it.

---

## 0. Ground rules

1. **Do not rewrite from scratch.** Audit the existing code, find the root cause of each defect, fix it, and add a regression test that would have caught it. One defect = one commit, referencing its ID (`BUG-07`, …).
2. **The approved look does not change.** `docs/ANTIGRAVITY_PROMPT_v1.md` (fidelity prompt) + `mockup/index.html` + `reference/png|spec` remain the visual source of truth, including the known-defect exceptions F1–F4. The v1 screenshot tests must stay green. If a functional need forces a visual change, stop and list it for the owner instead of deciding alone.
3. **"Not implemented" is a defect.** The mockup contains controls whose target screen was never drawn (search, filters, add driver, cancel, ⋯ menus, edit, audit log…). In v1 these were stubs. In v2 they must work. Build the missing screens/sheets from the existing design system only (same tokens, components, spacing, type); they are listed in section 8 as **X-screens**.
4. **No dead taps — enforced by the compiler.** Every clickable `Tc*` component takes a **required, non-default `onClick`** (no `= {}`, no `null` for visible controls). Add a CI grep/lint rule that fails on `onClick = {}` and on empty click lambdas. A control that is intentionally disabled must be visibly disabled and carry a reason (`contentDescription`/tooltip).
5. **Evidence over summaries.** Every claim in a report is backed by pasted tool output (test summary, `report.md`, `adb`/moreplay logs, screenshots with file names). If you did not run it, write "NOT RUN". Never edit thresholds, masks, references or tests to make something pass. If something cannot be done, say exactly what and why.
6. **Reproduce first.** For each defect in section 2, capture a "before" screenshot on an emulator profile (section 3.5) *before* fixing, and an "after" screenshot at the end. Store both under `evidence/<BUG-ID>/`.
7. **Why the v1 gates missed these bugs (fix the harness, not only the app):** the v1 screenshot tests rendered screens at 324 × 648 dp *without system bars* and with static fixtures, and never tapped anything. So they could not see content under the status/navigation bars, dead buttons or wrong routes. v2 adds an **insets audit**, a **click-through (navigation matrix) test**, and **device-matrix** captures (sections 3, 4, 12).
8. Keep a running `docs/DECISIONS.md` (ADR style) and `docs/PRD_ADDENDUM.md` for every decision that changes or extends PRD v2.1. Anything the owner must decide goes in `docs/OWNER_QUESTIONS.md`; do not block on it — implement the default stated here and flag it.

---

## 1. What the screenshots show (evidence)

Device: ~1080 × 2412 px (~393 × 877 dp), punch-hole/pill status area with a system "2 devices" pill, 3-button navigation (≡ □ ◁), dark theme, Indigo.

| File | Screen | What is visible |
|---|---|---|
| E1 `physical_screen.png` | Shipments | Title and search button drawn *under* the clock/pill/battery; bottom-nav labels (Map, Shipments, Fleet) overlapped by the system ≡ □ ◁ buttons; third text line of each card missing the date; FAB present |
| E2 `…05-32…jpg` | Settings (admin) | Back button/title under status bar; avatar shows **"Anil Kum"** instead of **AK**; Notifications, Security and Help icons wrong (broken shapes); Language/Theme/etc. rows dead; note text left-aligned instead of centred at the bottom |
| E3 `…05-36…jpg` | Live map | Fake drawn polygon, no city labels; bottom sheet missing the 3 stat tiles and the Timeline/Call buttons, text wrapping, sheet not docked to the bottom nav (gap); Map tab icon broken; title/filter under status bar |
| E4 `…05-46…jpg` | Dashboard | Header says "Active trips · 7 / See all" (design: "Live trips" / "View map"); SOS button says "Track" (design: "Call"); trip cards have no progress bar or On time/Delayed chip; KPI sub-text colours missing; a FAB that the design does not have |
| E5 `…09-59…jpg` | Driver Active trip | Timeline card stretched to full height showing only 1 of 5 steps with a full-height rail; **Navigate, call and "Reached drop" buttons missing**; SOS pinned half off the right edge; card runs under the navigation bar; title under status bar |

Reported by the owner (no screenshot): **+ does nothing; Fleet tab opens Settings; More does nothing; no Settings row works; History is not what was needed; map is not the correct (real) map; many buttons/functions do not work.**

---

## 2. Defect register (fix all; each needs a regression test)

| ID | Symptom | Likely cause (verify, do not assume) | Required fix | Proof |
|---|---|---|---|---|
| BUG-01 | Content under status bar/cutout/pill (E1–E5) | `enableEdgeToEdge()` with no top insets consumed | One `TcScaffold` that applies status-bar + display-cutout insets to every app bar (section 3) | Insets audit green on all screens × device matrix |
| BUG-02 | Content/bottom nav under system navigation bar (E1,E3,E4,E5) | No navigation-bar inset on bottom nav / scroll content / FAB / SOS | Bottom nav height = 68 dp (driver 76) **+ navigationBars inset**; FABs/SOS anchored inside the scaffold content area; content bottom padding = inset | Insets audit; screenshots on 3-button and gesture nav |
| BUG-03 | Fleet tab opens Settings | Tab→route mapping wrong (index/enum mismatch) | Type-safe route per tab with a single source-of-truth `AdminTab` enum; unit test for each tab → destination | Click-through test |
| BUG-04 | More tab does nothing | Missing destination/onClick | Implement M14 (More) and all of its rows | Click-through test |
| BUG-05 | "+" does nothing; FAB wrongly on Dashboard | FAB not wired; FAB shared across tabs | FAB only on M3 (Shipments) → New-shipment wizard; plus buttons on M7/M11 → X3/X4 | Click-through test; screenshot M1 has no FAB |
| BUG-06 | Settings rows dead | No click handlers | Wire per section 7 | Click-through test |
| BUG-07 | Avatar "Anil Kum" | Initials built with `take(n)` on the name | `initials(name)` = first letter of the first two words, uppercased; Unicode/Malayalam safe (use grapheme/code-point aware), fallback "?" | Unit tests: "Anil Kumar"→AK, "Suresh"→S, "അനിൽ കുമാർ"→ first grapheme of each word |
| BUG-08 | Broken icons (Map, Notifications, Security, Help, others) | Compact SVG arc flags (`a7 7 0 0114 0`) mis-parsed in VectorDrawable path data | **Replace all `ic_*.xml` with the regenerated set in this pack** (`app/src/main/res/drawable`, produced by the fixed `tools/gen_icons.py` + `tools/svgpath.py`: every path is absolute M/L/C/Q/Z, arcs converted to cubics, verified pixel-equal to the mockup SVGs — see `reference/icon_sheet_original_vs_converted.png`). Never hand-edit pathData | Icon-sheet screenshot test comparing every icon with the reference render |
| BUG-09 | Dashboard deviates from design | Screen built from memory, not from `M1` spec | Rebuild M1 from `reference/spec/en/M1_dashboard.*` : KPI sub-text colours, SOS card with **Call**, "Live trips" + "View map", trip cards with chip + progress bar (62 % / 38 %, bar colour per spec), no FAB | v1 compare PASS for M1 (light/dark) |
| BUG-10 | Fake map, undocked sheet, wrong sheet content | Canvas placeholder shipped as final | Real map (section 6) + sheet exactly per M2 spec, docked at `bottom = bottom-nav top`, stat tiles, Timeline/Call | Emulator screenshots + v1 compare for the non-map chrome |
| BUG-11 | Shipments cards missing "· 22 Nov, 6:00 am"; search dead | Incomplete row | Row per M3 spec; search = X1; tabs filter data | Screenshot + click-through |
| BUG-12 | Driver Active trip broken (E5) | Timeline uses `fillMaxSize/weight` and clips children; action row not laid out | Rebuild V5 per spec + F1/F2 fixes: timeline card wraps content (all 5 steps), buttons pinned bottom, SOS above them with safe margins | v1 compare PASS (with the fixed masks only), insets audit |
| BUG-13 | History "not like we needed" | Unknown — see 7.4 | Implement per 7.4 and ask the owner (`OWNER_QUESTIONS.md` Q1) | Screenshots + owner confirmation |
| BUG-14 | Many controls dead | Handlers not wired | Section 4 (Action Map) + compiler rule in 0.4 | Navigation-matrix test = 100 % of controls |
| BUG-15 | Settings footnote left-aligned | Missing `TextAlign.Center`/pinned position | Per S1 spec | v1 compare |

Add any further defect you find while reproducing; give it the next ID.

---

## 3. Universal device support (the app must work on any Android phone/tablet)

### 3.1 Principle
The mockup is a 324 × 648 dp *design canvas*, **not a device size**. The real layout is fluid: reproduce spacing, sizes and proportions exactly, but never depend on the screen being 324 dp wide or 648 dp tall.

### 3.2 `TcScaffold` — the only way to build a screen
All 47 + X screens use one scaffold in `:core-design`. Feature code must not use raw `Scaffold`/`Box` as a root.

```
TcScaffold(
  topBar    = { TcAppBar(...) },            // gets statusBars ∪ displayCutout(top/left/right) insets
  bottomBar = { TcBottomNav(...) } | null,  // height + navigationBars inset; draws its own background behind the bar
  fab       = { ... },                      // anchored inside content, offset by insets
  overlays  = { SOS button, banners },      // same rule
  accentFullBleed = true|false,             // for A1 splash / V2 job alert: colour behind system bars, content padded by insets
  imeAware  = true,                         // forms: content + pinned CTA move above the keyboard
) { padding -> content }
```
Rules:
* `enableEdgeToEdge()` in `MainActivity`; transparent system bars; status/navigation bar **icon colours follow the screen background luminance** (light icons on dark, dark on light, per theme and per accent-filled screens).
* Top insets: `WindowInsets.statusBars ∪ WindowInsets.displayCutout` — also left/right cutout insets in landscape.
* Bottom insets: `WindowInsets.navigationBars` (3-button ≈ 48 dp, gesture ≈ 16–24 dp, varies). Never hard-code them.
* IME: `imePadding()` on forms; the pinned primary button sits above the keyboard.
* Sheets (`TcSheet`) pad their content by the navigation-bar inset; the scrim covers the full window including the system bars.
* Dialogs/snackbars appear above the bottom nav and inside the safe area.
* `SOS`/FAB offsets: `right = 14/16 dp + horizontal safe inset`, `bottom = bottomNavHeight + navInset + 88/92 dp` as in the spec (relative to the content area, not the window).
* Predictive back enabled (`enableOnBackInvokedCallback`), correct back-stack behaviour per tab.

### 3.3 Size classes and adaptive layout
Use Material window size classes on **width**:
* **Compact (< 600 dp)** — phones: the approved mockup layout, stretched fluidly.
* **Medium (600–839 dp)** — small tablets / large foldables / phone landscape: keep bottom nav; centre content in a column of `maxContentWidth = 560 dp`; sheets become centred dialogs-style sheets max 560 dp wide.
* **Expanded (≥ 840 dp)** — tablets: bottom nav → `NavigationRail` (same icons/labels/accent pill), list screens (shipments, drivers, vehicles, history) become 2 columns, Live map shows the vehicle sheet as a 360 dp side panel.
Heights: any content taller than the viewport scrolls its content region only; pinned CTAs/nav stay put. Landscape on phones is supported (no forced orientation lock except the job-alert screen may stay portrait).

### 3.4 Fonts, density, accessibility
* Font scale 0.85 → 1.5: **no clipping, no overlap**; text containers use `heightIn(min=…)`, never fixed heights; at 2.0 layouts may scroll. Test Malayalam at every scale (taller glyphs: line-height ≥ 1.35).
* Display-size (density) changes are handled by dp; verify at Small and Largest display size.
* Touch targets: obey `TcAccessibility.PRD_FLOORS` (see v1 §1.8); decide the default with the owner (`OWNER_QUESTIONS.md` Q2) — until answered, **turn the flag ON for release builds** (driver text ≥ 18 sp, driver targets ≥ 56 dp, admin ≥ 48 dp) and keep it OFF only for the v1 pixel-test configuration.
* TalkBack: every interactive element has a role and a label; decorative icons are hidden; focus order = reading order; contrast ≥ WCAG AA in all 4 themes × light/dark (add a unit test computing contrast for token pairs).
* RTL not required; locale-aware numbers/dates (en-IN, ml-IN; 12-hour as in mockup).

### 3.5 Device test matrix (mandatory evidence)
Create AVD configs (documented in `docs/devices.md`) and run the audits on **all** of them:

| Profile | Size (dp) | Notes |
|---|---|---|
| Small/low-end | 360 × 640, API 26, 2 GB | worst case for jank/ANR |
| Narrow | 320 × 568 | smallest supported |
| Owner's phone class | 393 × 877, API 34/35, **3-button nav**, tall cutout overlay | reproduces E1–E5 |
| Large phone | 412 × 915, gesture nav | |
| Tablet portrait | 800 × 1280 | rail + 2 columns |
| Foldable unfolded / landscape phone | 673 × 841 / 915 × 412 | medium class, cutout on the side |
Each in light + dark, font scale 1.0 and 1.5, display size default and largest. Capture every screen on every profile into `evidence/devices/<profile>/…`.

### 3.6 Insets audit (automated)
`InsetsAuditTest` (instrumented, runs on every profile): for every screen, read the real `WindowInsets` (status bar, display cutout, navigation bar, IME when open), collect semantics bounds, and assert that **no `Text`, icon, button or other interactive node intersects a system-bar/cutout rectangle** (full-bleed backgrounds are exempt), and that the bottom nav's labels and the pinned CTAs are fully inside the safe area. Failures print the screen, node and overlap. This test must be red on the original build and green after the fix.

---

## 4. Navigation and interaction completeness

### 4.1 Architecture
* Navigation Compose with **type-safe routes** (`@Serializable` objects) and nested graphs: `AuthGraph`, `AdminGraph` (tabs: Home, Map, Shipments, Fleet, More — each with its own saved back stack), `DriverGraph` (Home, History, Alerts, Profile), `SettingsGraph`.
* One `AdminTab`/`DriverTab` enum drives bottom nav labels, icons, routes, and test tags (fixes BUG-03).
* Deep links for every screen: `truckconnect://shipment/{id}`, `…/alert/{id}`, `…/driver/{id}`, `…/gallery/{ID}` (debug), used by FCM notifications and tests.
* Sheets and dialogs are destinations too (restorable after process death).
* Session routing at start: valid session → role home; none → A2; below minimum version → U3 (except during an active trip, section 9.8).

### 4.2 Test tags and the click-through test
Every interactive element gets `testTag = "tc:<ScreenId>:<elementId>"` (e.g. `tc:M3:fab`, `tc:Admin:tab_fleet`). Create `docs/action_map.csv` with one row per control: `screenId, elementId, action, expectedDestinationOrEffect`. The generated `NavigationMatrixTest` (instrumented, runs on 2 profiles) **taps every row** and asserts the destination or effect (route, sheet shown, intent fired, API call enqueued, state change). **Coverage target: 100 % of controls in the action map; any control not in the map fails the build** (the test enumerates all nodes with `onClick` semantics and compares with the CSV).

### 4.3 Action Map (authoritative; extend as you add X-screens)
Format: control → behaviour.

**Auth & access**
* **A1** auto-advance after the session check (min 600 ms, max 1.5 s) → role home / A2 / U3.
* **A2** Admin → A3 · Driver → D1 · Terms/Privacy link → Custom Tab to the configured URLs.
* **A3** Sign in → API login → (2-step code if enabled) → biometric/app-lock setup prompt on first login → M1; inline errors; "Forgot?" → inline help "Ask the owner to reset your password" (AUTH-4) with a Call button; Keep-me-signed-in toggle persists session length.
* **D1** Keypad fills/clears 4–6 digits; Login → API; wrong PIN shows remaining attempts; **5 failures → 15-minute lockout** (server-enforced) with countdown; language chip toggles ml/en live; Register → D2.
* **D2** mobile validation (+91, 10 digits, server uniqueness check) → PIN (reject trivially weak: 0000/1234/repeated) → D3.
* **D3** validate names/phones → D4. **D4** camera/gallery for licence photos (compress ≈ 300 KB), consent toggle **default OFF**, Submit disabled until ON → API register → D5.
* **D5** polls/receives push; Approved → D1 prefilled; Rejected → message + reason + Call office; Call office → dial.
* **D6** each Allow → real runtime request (fine location, notifications on API 33+, full-screen-intent special access, battery-optimisation exemption); status chips update when granted; "Open guide" → OEM autostart/battery guide article; Next → V1 (allowed even if skipped, with a persistent banner on V1 listing what is missing).

**Admin**
* **Bottom nav** Home/Map/Shipments/Fleet/More → their roots; re-tap pops to root; state restored.
* **M1** bell → M13 · KPI tiles → M3 (Active / Pending / Delayed filters) or M7 (Free) · SOS "Call" → dial driver; card tap → M2 focused on that vehicle · "View map" → M2 · trip card → M4 · pull-to-refresh.
* **M2** filter → X2 · pin tap → vehicle sheet · sheet Timeline → M4 · Call → dial · "locate me", compass, zoom controls (section 6).
* **M3** segmented filters (client+server) · search → X1 · row → M4 · FAB → wizard (X6) · pull-to-refresh · pagination.
* **M4** ⋯ → sheet (Duplicate, Route playback X11, Share summary, Cancel) · Reassign → M6 sheet (with reason) · Cancel → X5 · proof photo → viewer (X10) · driver row → M8 · call driver/consignee.
* **M5/X6 wizard** step 1 Route (address picker X7, contacts) → step 2 goods & schedule (M5: date-time picker, weight validation) → step 3 M6 assignment → review → Create → success → M4 of the new shipment. Back/up never loses typed data (saved draft).
* **M6** driver list from API (Available/Free/Busy/Offline), busy and over-capacity warnings per SHP-2; "Assign & notify driver" → API → FCM → snackbar → M4.
* **M7** tabs · search X1 · plus → X3 · row → M8. **M8** call · Reset PIN → confirm → new PIN shown **once** (copy / share via WhatsApp) · Revoke device → confirm → API · edit → X3 · view trips → M3 filtered.
* **M9** Review → M10 · Approve (list) → confirm sheet → API → push to driver · info card. **M10** Approve / Reject (reason sheet) · photo tap → viewer.
* **M11** tabs · plus → X4 · row → vehicle detail (X4b: docs with expiry chips, default driver, status).
* **M12** range tabs reload data · Export PDF/Excel → API generates file → system share sheet (FileProvider) · pull-to-refresh · download icon = same export menu.
* **M13** tabs filter · Call/Reassign/View/Open per alert type · swipe or long-press → mark handled · deep links from push.
* **M14** profile card → X13 · Reports → M12 · Vehicles → M11 · Driver approvals → M9 · App versions → M15 · Audit log → X9 · Settings → S1 · Sign out → confirm → clear tokens + FCM token → A2.
* **M15** data from API · Halt rollout / Force update → confirm dialogs → API; per-driver rows → M8.

**Driver**
* **V1** bell → V11 · online chip toggles availability (haptic + snackbar) · SOS → V10 · banner for missing permissions/updates/offline queue.
* **V2** opens from FCM via full-screen intent on the lock screen; alarm sound + vibration loop (stop on any action, max 60 s then notification persists); **Speak job** → TTS in app language; Accept → API → V4; Reject → V3; timeout → "missed" state.
* **V3** radio → Reject (API with reason) → V1 + snackbar · Later → dismiss sheet.
* **V4** Navigate → V6/direct · Call office → dial · Start trip → API start + **start location foreground service** → V5 · pickup/drop rows tappable → maps.
* **V5** primary button label = next milestone (Reached pickup → Loaded → In transit → Reached drop) with **5-second Undo snackbar before commit**; offline allowed (queued); after "Reached drop" → V7 · Navigate → V6/direct · call button → sheet: Office / Consignor / Consignee · SOS → V10 · timeline rows show captured time and a GPS ✓.
* **V6** lists installed navigation apps that resolve a `geo:` intent (show Google Maps / Vela / OsmAnd with their icons when installed, others by label), "Default" is remembered; launches with destination; not installed → hidden.
* **V7** camera capture (compress ≈ 300 KB, retake) · receiver name required · OTP optional/Skip · Complete delivery → queue (works offline) → V8.
* **V8** Back to home. **V9** see 7.4. **V10** hold 1.5 s with progress ring → API `/sos` with last location (offline → queue + vibrate + on-screen "will send when network returns") · Cancel. **V11** notification list, unread dots, tap → target, mark all read. **V12** cog → S2 · Log out → confirm (warn if a trip is active).

**Settings & system**
* **S1** Profile/edit → X13 · Theme → S3 · Language → S4 · Notifications → S5 · Security → S6 · Help → S7 · App version → S8.
* **S2** rows as S1 (driver-sized) · Voice read-out toggle persists and affects V2 · Text-size segmented (Normal 1.0 / Large 1.15 / Extra large 1.3) scales app typography immediately and persists.
* **S3/S4** apply instantly (no restart), persist locally and sync to profile.
* **S5** toggles persist per notification channel (Android channels) and sync to server.
* **S6** App-lock toggle → BiometricPrompt/PIN enrol · 2-step verification toggle → API · Change password → X14 · devices list from API · Revoke → confirm → API.
* **S7** WhatsApp support (`wa.me`), Call office, help articles (X22), video link · **S8** Check for updates → API → U1 if newer else "You're up to date" · What's new.
* **U1** Update → download with progress (resumable) → verify SHA-256 → PackageInstaller (needs U2 if install permission missing) · Later dismisses. **U2** Open settings → `ACTION_MANAGE_UNKNOWN_APP_SOURCES`. **U3** blocks navigation; never shown during an active trip (9.8).

---

## 5. Premium, enterprise-grade polish

Without changing static visuals, add the polish below (all behind tokens):
* **Motion tokens**: durations 120 / 200 / 300 ms, `FastOutSlowIn`; screen transitions = 300 ms shared-axis (slide 24 dp + fade); sheets spring; list `animateItem`; card → detail with `SharedTransitionLayout`; skeleton shimmer 1.2 s on every list while loading; number tickers on KPI changes; marker position interpolation on the map (1 s). Respect the system animator-duration scale (0 = no animation).
* **Haptics** (CONFIRM on accept/milestone, REJECT on reject, LONG_PRESS on SOS hold, tick on keypad).
* **States for every screen**: loading skeleton, empty (illustration-free, icon box + title + hint, per tokens), error (retry), offline banner (APP-8), stale data indicator. Components `TcEmptyState`, `TcErrorState`, `TcSkeleton`, `TcOfflineBanner`, `TcDialog`, `TcSnackbar(undo)`.
* **Pull-to-refresh**, back-gesture preview, keyboard-aware forms, input masks (phone, vehicle reg `KL-07-AZ-1200`), validation messages under fields (no toasts for validation).
* **Strings**: complete Malayalam (section 11); no English left in driver screens.

---

## 6. The map (replace the fake canvas with the real thing)

* **Engine:** MapLibre Native Android behind `TcMapSurface` (same signature as the v1 canvas version so screens do not change). The canvas version stays as a **debug/offline-failure fallback** and for the pixel tests of the surrounding chrome.
* **Tile source — day-1 spike (document in `docs/DECISIONS.md`):** evaluate (a) a free community OSM vector-tile service, (b) a Kerala PMTiles extract hosted on free storage, (c) a free-tier provider with an API key. Criteria: legal for commercial use, Kerala detail down to town/road level, MapLibre-compatible, size, availability guarantees, attribution rules. **Do not use the public OSM raster tile server.** Verify current terms yourself; do not rely on memory. Put the choice behind a `MapStyleProvider` so it can change without touching screens.
* **Style:** generate light and dark styles from tokens (`mapLand`, `mapSea`, `mapLine`, text colours) for all 4 themes; roads (major → minor by zoom), water, place labels (English; Malayalam labels where the data has them), no POI clutter. Attribution "© OpenStreetMap contributors" + provider on every map.
* **Admin live map (M2):** camera fits Kerala bounds on first load; vehicle symbols = the design's truck pin (status colours: accent=on trip, crit=SOS/problem, warn=delayed, neutral=offline/stale) with the selected halo; clustering when zoomed out; tap → sheet (stat tiles: speed, updated, ETA; Timeline/Call); **stale flag** when no ping within the configured time (TRK-5); follow-selected toggle; locate-me; zoom ± ; compass; filter (X2); markers move smoothly as Realtime updates arrive (≤ 5 s end-to-end latency target, measure it).
* **Routes:** OpenRouteService directions (free key, cached per shipment, straight-line fallback) drawn as a polyline (accent, 4 dp, casing in `surface`); pickup/drop pins; ETA from route + current speed.
* **Route playback (X11, P1):** polyline from stored pings, scrubber, play/pause, speed ×1/×4/×8.
* **Address search:** Nominatim — **no autocomplete on the public instance**: search on submit (or ≥ 1 s idle debounce with caching and a visible search button), User-Agent set, results cached, saved/recent addresses first, "pick on map" fallback. Wrap all map/geocode/route calls in one client with rate limiting.
* **Offline:** cache the Kerala region (size-capped, e.g. ≤ 150 MB, user-triggered) and show "map offline" gracefully.
* **Performance:** 60 fps pan/zoom on the low-end profile, ≤ 50 markers without clustering lag, memory stable after 30 min.
* Verification: screenshot of the map on every device profile; a scripted check that markers update when a fake driver pushes pings (section 9.6).

---

## 7. Screen-level corrections and additions

### 7.1 Rebuild from spec (not from memory)
M1, M2 chrome, M3 row, V5, S1 per `reference/spec`. Compare every rebuilt screen with `tools/compare.py`; paste the table.

### 7.2 Initials / avatar
Implement `initials()` (BUG-07) and use it everywhere (lists, profile, detail, review). Avatar text never overflows: single-line, `maxLines=1`, centre aligned, size scales with the circle.

### 7.3 Bottom nav
Exact spec (68/76 dp + inset, active pill 46 × 28 dp accent-soft, 10.5/12.5 sp labels). Badges (alerts count) support. Admin tab order **Home, Map, Shipments, Fleet, More**; driver **Home, History, Alerts, Profile**.

### 7.4 History (BUG-13)
The owner says History is "not like we needed" but has not said how. Until answered (`OWNER_QUESTIONS.md` Q1) implement the **V9 design exactly** and make it fully functional:
* Driver History: two live stat tiles (Trips, On time) for the selected range; range chips (This week · This month · All) and a status filter (Delivered/Cancelled) using `TcSegmented`; list grouped by month (sticky headers) with route, date/time, chevron; paging (Paging 3 + Room cache, works offline); pull-to-refresh; empty/error states.
* **Trip detail (X10)**: route, code, times of every milestone (timeline), distance, duration, receiver name, proof photo (zoomable viewer), consignee/consignor, "Report an issue" (opens WhatsApp support prefilled).
* Admin-side history = Shipments → "Done" tab with the same detail (M4) incl. route playback.
Ask the owner one precise question in the final report: *what is wrong with History compared with V9* (layout? data? grouping? filters? per-vehicle/per-month totals? earnings?).

---

## 8. X-screens (needed for a working app; built only from existing tokens/components)

| ID | Screen | Spec in one line | P |
|---|---|---|---|
| X1 | Search | App bar turns into a search field (back, clear); recent searches; results list uses the same row as the parent list; empty state | P0 |
| X2 | Filter sheet | `TcSheet` with chips/segmented (status, date range, vehicle type, driver); Apply + Reset; active-filter count badge on the icon button | P0 |
| X3 | Add / edit driver | Name, phone, photo, emergency contact, licence no./type/expiry + photo uploads, vehicle default, availability; on create show the issued PIN once | P0 |
| X4 / X4b | Add / edit vehicle · vehicle detail | Reg no. (masked), type, capacity (kg/t), default driver, status; documents RC/insurance/fitness/permit/PUC with expiry date, photo upload, expiry chips (ok / due soon / expired) | P0 |
| X5 | Cancel shipment | Sheet: reason chips + optional note → API → driver push | P0 |
| X6 | New-shipment wizard shell | Steps bar 1/3–3/3 (existing component), draft autosave | P0 |
| X7 | Address picker | Search (section 6), saved/recent, pick on map, contact name/phone | P0 |
| X9 | Audit log | Chronological list (actor, action, entity, time), filters, owner-only | P1 |
| X10 | Trip/shipment proof viewer + trip detail | See 7.4 | P0 |
| X11 | Route playback | See section 6 | P1 |
| X12 | Document upload/preview | Camera/gallery, compress, preview, replace | P1 |
| X13 | Edit profile | Name, phone (read-only after verify), email, photo, language | P0 |
| X14 | Change password | Old/new/confirm, strength hint, sign-out other devices option | P0 |
| X16 | Confirm dialogs | `TcDialog` (title, body, destructive/primary buttons) for logout, revoke, cancel, halt rollout, force update, delete draft | P0 |
| X22 | Help articles | Static, bilingual: How to accept a job, Fix tracking on my phone (OEM battery/autostart guide), Privacy & location | P0 |
| X23 | Call chooser | Sheet: Office / Consignor / Consignee numbers with names | P0 |

Everything else the mockup shows (47 screens) stays as specified in v1.

---

## 9. Making the app really work (backend, data, realtime, offline)

### 9.1 Day-1 reality checks (document results; adapt)
1. Is the backend already in the repo/hosting? Audit it. If not, build it per PRD §5/§11/§12 plus the additions below.
2. **Hosting spike:** PRD §24 warns Docker Spaces may need a paid plan and sleep after 48 h. Verify today. If unusable, run the same NestJS container on another free container host or move logic to Supabase Edge Functions (document the choice). Latency targets (API p95 < 500 ms, push < 5 s) must be measured **including cold start**; add a health endpoint + scheduled keep-alive.
3. Supabase free limits (500 MB DB, 1 GB storage, project pause after inactivity): implement the PRD mitigations (ping retention 30 days, photos ≈ 300 KB, **photo retention policy** — archive/compress proofs older than N days, report storage usage in the admin app, weekly `pg_dump`).

### 9.2 Backend (NestJS + Supabase Postgres/PostGIS)
* Modules: auth, users/drivers, vehicles, documents, shipments (+state machine), tracking, files (presigned), notifications (FCM), alerts, reports (+export PDF/Excel), app-releases, audit, devices, health.
* **State machine** enforced server-side; each transition writes `shipment_events` (time, geo, actor, `client_request_id` idempotency). Invalid transitions → 409.
* **Additions required by the mockup (extend PRD §12):** `POST /auth/register-driver`, `GET /drivers?status=pending`, `POST /drivers/{id}/approve|reject`, `POST /drivers/{id}/reset-pin`, `POST /devices/{id}/revoke`, `GET/PATCH /me`, `POST /me/change-password`, `GET /alerts`, `POST /alerts/{id}/handle`, `POST /sos`, `GET /notifications`, `POST /notifications/read`, `GET /app/releases` (admin), `POST /app/releases/{id}/halt|force`, `GET /audit-logs`, `GET /reports/summary|export`, `GET /vehicles/{id}/documents`.
* Migrations versioned; OpenAPI generated; **seed script that reproduces the mockup data** (Anil Kumar, Suresh Kumar, shipments TC-2026-0001xx, vehicles, versions) so a fresh install looks like the design.
* Security: argon2/bcrypt for passwords and PINs; short-lived JWT + rotating refresh tokens; per-IP and per-account **rate limits and lockout**; RBAC guard on every route (admin/dispatcher/driver); one active device per driver; deactivation invalidates tokens immediately; request size limits; helmet/CORS; no secrets in the repo (env only); audit log for admin actions.
* Tests: unit + e2e (supertest) with a test database; coverage report.

### 9.3 Realtime without leaking data (PRD gap — fix explicitly)
Admin screens subscribe via Supabase Realtime, but login is custom. Therefore:
* The API mints a **Supabase-compatible JWT** (project JWT secret, `role=authenticated`, `sub`, custom `app_role`) for the admin/dispatcher app's Realtime connection only.
* Realtime only reads narrow tables: `vehicle_positions` (latest position per driver, upserted by a trigger from `location_pings`), `shipments`, `alerts`. **RLS on each**: admin/dispatcher can select all; drivers can select only their own rows; nobody can write from the client.
* `location_pings` (raw, partitioned by month) is not in the Realtime publication.
* Add a test that connects with a *driver* token and proves it cannot receive other drivers' positions. The PRD's two mentions ("WebSocket /ws" vs Supabase Realtime) are resolved in favour of Supabase Realtime; record it in the addendum.

### 9.4 Android data layer
Room (offline cache + **outbox**) · Retrofit/OkHttp + kotlinx.serialization with a typed client generated from the OpenAPI · Hilt · repositories return `Flow` from Room with network refresh · WorkManager sync with exponential backoff · every write carries a `client_request_id` · token refresh via OkHttp `Authenticator` · tokens in Keystore-backed storage (Tink/EncryptedFile/encrypted DataStore) · `android:allowBackup="false"` · cleartext disabled in `network_security_config` · **no certificate pinning in the free phase** (third-party hosts rotate certificates and a bad pin would also break the updater; revisit with SPKI backup pins after moving to owned domains) · R8 on release · no PII in logs/Crashlytics.

### 9.5 Notifications
FCM high-priority **data** messages → `FirebaseMessagingService` → notification channels (`job_alert` max importance with alarm-style sound, `trip_updates`, `alerts`, `system`). New job → full-screen intent to V2 + persistent notification fallback. On Android 14+ full-screen-intent access is a special permission that Play only auto-grants to some app categories (as far as I know — verify); check `canUseFullScreenIntent()` and guide the driver (D6/V1 banner); test the lock-screen behaviour on real hardware; document OEM (ColorOS/Realme/Xiaomi) battery/autostart steps in X22. Repeat the push and escalate to the admin after the configured delay (default 10 min, NOT-2).

### 9.6 Live tracking
Foreground service (`foregroundServiceType=location`) started when the driver taps **Start trip** (so no background-location permission is needed), persistent notification, FusedLocation with adaptive interval 15–60 s by speed/battery, batch upload every N pings/30 s, local queue when offline, stop at Delivered/Cancelled, clear consent text and a visible tracking indicator on V5. Server stores pings, updates `vehicle_positions`, flags stale after the configured minutes. **Test:** a script that simulates a driver (API calls with synthetic pings along a Kerala route) and a test that the admin map moves.

### 9.7 Offline-first rules
All driver actions (accept, reject, milestone, POD, SOS) work offline and sync in order with idempotency; UI shows "Saved — will send when network returns" (`TcOfflineBanner`) with queue count; conflict policy: server state wins, driver is told (e.g. shipment reassigned/cancelled) via a clear sheet; photos upload resumable.

### 9.8 In-app update system (PRD §10)
`/app/version` check on launch and daily; U1 download with progress + SHA-256 verification + signature continuity (same signing key) → PackageInstaller; U2 for the install permission; U3 for `version < min_supported`. **Rule to implement and document:** a forced update never interrupts an active trip; the server keeps accepting milestone/POD/ping calls from below-minimum clients while they have an active trip (up to 24 h), then blocks. Admin M15 shows rollout %, per-driver versions, halt/force actions.

### 9.9 Privacy and consent (DPDP)
Consent text in the driver's language; consent is an explicit, specific opt-in (location during trips, documents/photos); consent log stored (who, what, when, text version); data-retention jobs (pings 30 days, proofs per policy); data export/delete request path via the owner; privacy policy link.

---

## 10. Production-readiness checklist (all must be evidenced)

* **Quality gates in CI (GitHub Actions):** ktlint/detekt, Android lint (warnings-as-errors for the custom rules in 0.4), unit tests, Roborazzi screenshot tests (v1 suite + insets variants), instrumented tests on an emulator matrix, backend tests, OpenAPI contract check, dependency vulnerability scan, APK size report (budget ≤ 25 MB), signed release build, versioning from tags.
* **Performance:** cold start ≤ 1.5 s on the owner's phone class and ≤ 3 s on the 2 GB API 26 profile; jank ≤ 2 % on lists/map (Macrobenchmark + baseline profile); no ANR; memory stable; tracking battery drain measured (target ≤ 5 %/h) and reported.
* **Reliability:** crash-free target ≥ 99.5 % in the pilot (Crashlytics), process-death restore for every screen (use `rememberSaveable`/SavedStateHandle), airplane-mode and flaky-network test passes, `StrictMode` clean in debug.
* **Security review:** list of checks done (token storage, lockout, RLS tests, input validation, exported components minimal, WebView none, FLAG_SECURE on PIN/consent screens, root-agnostic) in `docs/security_review.md`.
* **Accessibility & i18n:** TalkBack pass on the 10 key flows; contrast test; Malayalam complete and reviewed.
* **Docs:** `README.md` (setup in ≤ 30 min), `docs/ARCHITECTURE.md`, `docs/RUNBOOK.md` (deploy, rollback, keep-alive, backups, restore test), `docs/RELEASE.md`, API docs, `docs/PRD_ADDENDUM.md`.

---

## 11. Language completeness
`reference/strings_untranslated_in_ml.json` lists up to ~500 strings (many are names and numbers) that were English in the mockup's Malayalam mode (registration, permissions, help, settings, consent…). For production **all driver-facing text must be Malayalam**. Produce draft translations marked `<!-- DRAFT-ML -->`, in plain spoken Malayalam appropriate for truck drivers, add `docs/ml_review.md` listing every draft string for a **native-speaker review before release**, and keep the English fallback. Never ship consent or safety text (SOS, consent, permissions) without that review.

---

## 12. Phases and exit gates

| Phase | Work | Exit gate (paste the outputs) |
|---|---|---|
| **F0 Reproduce & harness** | Repro every defect on the device matrix with before-screenshots; install regenerated icons (BUG-08); add `InsetsAuditTest`, `NavigationMatrixTest` skeleton and `docs/action_map.csv`; add the no-empty-onClick lint rule | Before-evidence for BUG-01…15; new tests **red** on the old build for the right reasons |
| **F1 Insets & responsive** | `TcScaffold`, system-bar styles, size classes, rail/2-column variants, IME handling | Insets audit green on all profiles; screenshots of 12 key screens on all profiles |
| **F2 Navigation & actions** | Route graph, tab enums, all controls wired, X-screens shells, dialogs/sheets | Navigation matrix 100 % green; dead-tap lint green; BUG-03…06,14 closed |
| **F3 Visual parity regressions** | Rebuild M1, M2 chrome, M3, V5, S1, initials, bottom nav | v1 compare PASS for all affected screens (all 4 matrix cells), BUG-07,09,11,12,15 closed |
| **F4 Backend & data layer** | Backend/hosting spike, schema, auth, CRUD, seed, Room/outbox/sync, repositories | Backend test report; Android repository tests; app runs against the seeded server with the mockup data |
| **F5 Driver flows** | FCM job alert (lock screen), accept/reject, trip, milestones+undo, POD, SOS, offline queue, permissions, TTS | moreplay flows on emulator + one real-device log; offline scenario evidence |
| **F6 Tracking & real map** | Foreground service, pings, Realtime+RLS, MapLibre style/tiles, markers, routes, address search | Simulated-driver test moves the admin map; RLS leak test; map screenshots all profiles; BUG-10 closed |
| **F7 Admin operations** | Shipment wizard, assign/reassign/cancel, alerts, drivers/vehicles CRUD, approvals, reports + export, audit, versions | Action-map rows for admin 100 %; export files opened and described; BUG-05 closed |
| **F8 Settings, security, updates** | Theme/language/text-size/notifications, app-lock, 2-step, devices, change password, in-app updater incl. forced-update rule | Flow evidence incl. update from v(n) to v(n+1) on emulator |
| **F9 History & remaining X-screens** | History (7.4), trip detail, route playback, documents, help articles, Malayalam drafts | Screenshots + `ml_review.md`; Owner Q1 asked |
| **F10 Hardening & release** | Performance, a11y, security review, CI, docs, signed release build | `docs/fidelity_report.md` + `docs/release_readiness.md` with every checklist item PASS / FAIL / NOT RUN and numbers |

Do not start a phase before the previous gate is shown. Commit per phase with the evidence attached.

---

## 13. Final report format (`docs/release_readiness.md`)
1. Defect register: each BUG-ID → fixed? (before/after file names, test name).
2. Insets audit table (screens × profiles) and navigation-matrix summary (controls tested / total).
3. v1 compare tables (47 screens × 4 cells) re-run on the final build.
4. Backend: test summary, hosting decision, RLS leak test result, load/latency numbers including cold start.
5. Tracking: simulated-driver run, end-to-end latency measured.
6. Performance/battery/APK-size numbers vs budget.
7. Open items, anything NOT RUN, every deviation from the approved design, and the owner questions (History, accessibility defaults, hosting, translations).
No marketing language. Tables and pasted outputs only.

## 14. Out of scope for now
Admin web console, SMS OTP, Play flavour, multi-stop shipments (SHP-8), auto-assignment (SHP-9), expense capture (APP-11), geofence auto-arrival (TRK-6). Keep the architecture ready for them.
