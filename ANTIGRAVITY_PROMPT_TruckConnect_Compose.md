# TruckConnect — Pixel-Exact Jetpack Compose Build (Antigravity Master Prompt)

You are building the **complete UI of the TruckConnect Android app in Kotlin + Jetpack Compose**, matching the HTML mockup `mockup/index.html` **screen for screen, pixel for pixel**: 47 screens, from the splash screen to settings and the update flow, in 4 colour themes × light/dark × English/Malayalam.

This is a **UI-fidelity task with fake data**. No backend, no real maps tiles, no FCM, no real updater. Everything the mockup shows must exist, look identical, and be navigable.

Read this entire file before writing any code. Then work phase by phase (section 11). Do not skip the verification gates. Do not claim anything passes without the tool output that proves it.

---

## 0. Repository layout you are given

```
mockup/index.html                    SOURCE OF TRUTH #2 (read its CSS and every S.* screen function)
reference/png/<theme>-<mode>-<lang>/<ID>_<slug>.png   SOURCE OF TRUTH #1 (rendered screens, 2x, 648 px wide)
reference/spec/<lang>/<ID>_<slug>.html                exact HTML of each screen, inline styles included
reference/spec/<lang>/<ID>_<slug>.layout.json         every element: x,y,w,h (css px), font-size, weight, colour,
                                                      background, radius, border, padding, gap, text
reference/strings_pairs.json                          EN→ML pairs for every string that differs (102)
reference/strings_untranslated_in_ml.json             strings that stay English in the mockup's ML mode
reference/masks.json                                  fixed masks for mockup defects F1/F2 (used by compare.py)
fonts/plus_jakarta_sans_variable.ttf, fonts/noto_sans_malayalam_variable.ttf   (OFL, bundle in res/font)
app/src/main/res/drawable/ic_*.xml   45 generated stroke icons (regenerate with tools/gen_icons.py)
tools/capture_mockup.py   re-render references (add --all for all 4 themes × light/dark × en/ml)
tools/gen_icons.py        IC dictionary → VectorDrawables
tools/gen_strings.py      rebuilds the string tables from the captured layouts
tools/compare.py          pixel-compares your Compose screenshots with reference/png
docs: TruckConnect_Full_Project_PRD.md (v2.1) and Fleet_Management_Platform_PRD.md (background only)
```

**Precedence when sources disagree:** (1) `reference/png` and `reference/spec` → (2) `mockup/index.html` → (3) PRD v2.1 → (4) this prompt's prose. The one exception is section 2 (known mockup defects): there you implement the **intended** design, not the broken rendering.

First action: run `python tools/capture_mockup.py --all` (needs `pip install playwright && playwright install chromium`) so every theme/mode/language reference exists locally. Do not edit anything under `reference/`.

---

## 1. Fidelity rules (non-negotiable)

1. **No redesign, no "improvements", no Material defaults showing through.** Use Material 3 only as plumbing (modal sheet behaviour, text field internals). Every visible pixel comes from our tokens.
2. **Units: 1 CSS px in the mockup = 1 dp; font-size px = sp.** The mockup phone is 324 × 680 px, of which the top 32 px is a *fake status bar* (do not build it; use the real, transparent system bars — edge-to-edge). So the design canvas is **324 × 648 dp**.
3. **Flexible, not fixed.** Real devices are wider/taller than 324 × 648. Reproduce the mockup's flex behaviour: wherever the CSS uses `flex:1`, `margin-top:auto`, `justify-content:center|space-between`, use `Modifier.weight(1f)` / spacers / `Arrangement` — never hard-code a screen height. Primary action buttons that are bottom-pinned in the mockup stay pinned. A screen whose content does not fit (small phone, large font scale) may scroll its content region only; pinned buttons and bars stay visible.
4. **No magic numbers in screens.** Every dp/sp/colour/radius lives in `:core-design` (`TcColors`, `TcType`, `TcDimens`, `TcShapes`). A grep for `\.dp` or `\.sp` or `Color(0x` outside `:core-design` must return nothing (add this as a CI/unit check).
5. **Same text, same order, same hierarchy.** Copy strings exactly from `reference/spec/<lang>/*.layout.json` (`text` fields). Same icon, same size, same position.
6. **Mock data is part of the design.** Names, plates, codes, times and numbers must be exactly those in the mockup (fixtures in section 9).
7. **Do not invent screens.** There are exactly 47 (section 8). If a tap leads somewhere the mockup has no screen for (search, filter, ⋯ menus, edit pencil, Audit log, add-driver +, Cancel shipment…), show a `ComingSoonSnackbar` using the app's snackbar style; never a new UI.
8. **Accessibility floor flag (default OFF).** The mockup's driver text and some buttons are smaller than the PRD minimums (driver ≥18 sp text, ≥56 dp buttons; admin ≥48 dp). Because the brief is "exact", build the mockup values, but route every size through `TcDimens`/`TcType` so that one flag `TcAccessibility.PRD_FLOORS` (BuildConfig + debug toggle) can clamp: driver text ≥ 18 sp, driver tap targets ≥ 56 dp, admin tap targets ≥ 48 dp. Default `false`. Screenshot tests run with it `false`.

---

## 2. Known defects in the mockup — implement the INTENDED design

The mockup HTML has bugs. Reproducing them would break the app. Where the reference PNG shows a defect, match everything else and implement the intended behaviour below. Mark each as `// MOCKUP-FIX:` in code and list them in the final report.

| # | Defect in mockup render | Intended implementation |
|---|---|---|
| F1 | **Two-button rows overflow the screen.** `.btn` has `width:100%; flex:none`, which beats `.g{flex:1}`, so the second button is pushed off-screen. Affects 9 screens: **V2 (Accept button hidden!), V4 (Call office), V5 (phone button), M4 (Cancel), M8 (Revoke device), M10 (Approve), M12 (Export Excel), M15 (Force update), U1 (Update)**. | Equal-width pair: `Row(horizontalArrangement = spacedBy(10.dp))` with both buttons `Modifier.weight(1f)`. Button height/radius/font exactly as the mockup gives each. V5's second button is the icon-only call button, equal width. |
| F2 | **Timeline rail and dots are misaligned.** Pseudo-elements use content-box (`*{box-sizing}` does not reach `::before`), so the 12 px dot + 3 px border renders 18 px wide and sits off the 2 px rail (clearly in V5's card; slightly in M4). | Dot centred on the rail. Done step: 12 dp accent filled circle + 3 dp ring in the screen background colour + 1 dp accent outer ring. Pending step: background-coloured circle with 1 dp `border-2` outer ring. Rail: 2 dp, `border` colour, from the first to the last dot centre. Row spacing and text positions exactly per the reference PNG. |
| F3 | D4's DPDP consent toggle is **pre-switched on** in the render. | Fixture for the screenshot shows it ON (to match the PNG). The real default is **OFF**, and "Submit for approval" is disabled until it is ON. |
| F4 | Some mock data disagree with each other (update card offers **v2.1.1** while App versions says latest **v2.1.0**). | Keep as in mockup. Do not "fix". |

Anything else that looks odd in the PNGs is **intentional** — keep it (e.g. Alerts centre highlights "More" in the bottom bar; the M5 capacity warning appears before a vehicle is chosen; the driver bell on V1 has no pip).

---

## 3. Project setup (UI-only scope)

* Package `com.truckconnect.app`, single Activity (`MainActivity`, edge-to-edge), `minSdk 26`, target/compile latest stable SDK, Kotlin 2.x, Compose BOM latest stable, Hilt, Navigation Compose, DataStore (theme/mode/lang), Coil only if needed.
* Modules exactly as PRD §4.3: `:app`, `:core-design`, `:core-data` (fakes + fixtures), `:feature-auth`, `:feature-admin`, `:feature-driver`, `:feature-settings`. (`:core-network` can be an empty shell now.)
* MVVM: each screen = `XxxScreen(state, onEvent)` stateless composable + `XxxViewModel` exposing `StateFlow<XxxUiState>`; data from `interface` repositories implemented by `Fake*Repository` returning the fixtures. Real backend plugs in later without touching UI.
* One build variant now. (PRD flavours `play`/`direct` come later; do not add.)
* Debug-only **Gallery** (section 10) is part of this task.

---

## 4. Design system (`:core-design`)

### 4.1 Colours — exact hex

`TcColors` is a data class provided through `CompositionLocal`; it is chosen by `(theme, mode)`. `mode = Auto` follows the system.

**Light base (all themes)**

| token | hex | token | hex |
|---|---|---|---|
| bg | #F4F5F8 | ok | #0B7D45 |
| surface | #FFFFFF | warn | #A96B00 |
| surface2 | #F6F7FA | crit | #C9342F |
| border | #E7E9EE | info | #2563EB |
| border2 | #D7DBE3 | neutral | #6B7280 |
| text | #101319 | accentInk | #FFFFFF |
| text2 | #4A5160 | mapLand | #EEF1F6 |
| muted | #8A90A0 | mapSea | #F8FAFD |
| shadow | 0 1 2 rgba(16,24,40,.05) | mapLine | #CFD5E1 |

**Light accents:** indigo `#4F46E5` / soft `#EEF0FF` · emerald `#047857` / `#E6F6EF` · graphite `#27272A` / `#F0F0F1` · sunset `#D9480F` / `#FFF0E8`.

**Dark base (all themes)**

| token | hex | token | hex |
|---|---|---|---|
| bg | #0C0E12 | ok | #3CC47A |
| surface | #14171D | warn | #E0A63A |
| surface2 | #1B1F26 | crit | #F0716C |
| border | #262B34 | info | #6AA1FF |
| border2 | #343A46 | neutral | #9AA1B0 |
| text | #ECEEF2 | accentInk | #0C0E12 |
| text2 | #B6BCC9 | mapLand | #1C212B |
| muted | #7F8696 | mapSea | #12151B |
| | | mapLine | #323948 |

**Dark accents:** indigo `#8B83FF` / soft `#232245` · emerald `#34D399` / `#0F2A22` · graphite `#D4D4D8` / `#25262B` · sunset `#FB923C` / `#33200F`.

Derived colours (compute, do not hard-code): status chip background = status colour at **12 %** alpha; tinted icon box background = status colour at **13 %**; scrim `rgba(10,12,16,.45)` = `#0A0C10` @ 45 %. Add a unit test asserting every token above for all 4 themes × 2 modes.

### 4.2 Typography

* Latin: **Plus Jakarta Sans** (variable, weights 400/500/600/700/800). Malayalam: **Noto Sans Malayalam** (400/600/700). Bundle both from `/fonts` in `res/font` with `FontVariation.weight(...)` (minSdk 26 supports variable fonts).
* Font family order: Latin text → Plus Jakarta Sans with Noto Sans Malayalam as fallback. When the app locale is `ml` and the screen is a driver-sized screen, Noto Sans Malayalam is first (`.ph.ml`).
* Base size: admin screens **13 sp**, line-height 1.35; driver screens **16 sp**, line-height 1.35 (driver-sized = screens flagged `drv` in section 8).

| style | size / weight / tracking | notes |
|---|---|---|
| h1 | 22 / 800 / −0.02em, lh 1.2 | driver 26 |
| appBarTitle | 19 / 800 / −0.01em | driver 22 |
| btn | 14 / 700 | lg 17, xl 20, sm 12 |
| chip | 11 / 700 | driver 13 |
| label (`.lbl`) | 11.5 / 600, colour text2, mb 6 | driver 14 |
| small (`.sm`) | 11.5 | |
| xsmall (`.xs`) | 10.5 | |
| note | 11, muted, centred, lh 1.5 | |
| kpi value | 24 / 800 / −0.02em | |
| key (keypad) | 24 / 700 | |
| bottom-nav label | 10.5 / 600 | driver 12.5 |
| avatar | 12.5 / 800 | large avatar 24 |
| input text | inherits (13 admin / 16 driver) | driver input forced 18 |
| splash title | 28 / 800 | |
| job-alert destination | 30 / 800 | |

`.b` = weight 700, `.xb` = 800, `.mut` = colour muted. Letter-spacing in `em` → convert (`TextUnit` em works).

### 4.3 Dimensions / shapes / elevation

Taken from the CSS; verify against the layout JSON for every screen.

* App bar: padding 8/16/10/16, items gap 10, title flex 1. Icon button (`ib`): **38 × 38**, radius 12, 1 dp `border`, `surface` fill, icon 20 in `text2`; optional notification pip: 8 dp `crit` circle, top 8 / right 9, 2 dp `surface` ring.
* Screen content padding: 0 / 16 / 14 / 16; vertical gap 12.
* Card (`c1`): radius 16, padding 14, 1 dp `border`, `surface`. KPI cards: padding 12, 2-column grid gap 10.
* Buttons: Md height 46 radius 14 · Lg 56 / 18 · Xl 64 / 20 · Sm 34 / 10 (padding 0/12, wraps content) · Text 36 (accent text, no fill). Content gap 8. Variants: Primary (accent / accentInk), Outline (surface, 1 dp border2, text), Ok (`ok`, white), No (`crit`, white).
* Input (`inp`): height 48, radius 12, 1 dp border2, padding 0/14, gap 10, weight 500; driver: height 58, radius 14, 18 sp. Placeholder variant colour muted.
* Chip: height wraps; padding 3/9/3/7, radius 99, 6 dp status dot, gap 5; driver padding 4/11/4/9.
* Avatar 38 circle (soft accent, accent text); large 72. Icon box (`ico`) 40 radius 12.
* Progress bar (`bar0`): height 6, radius 3, surface2 track with 1 dp border, accent fill.
* List rows: padding 13/0 (driver 16/0), bottom divider 1 dp border, last none, gap 12.
* Segmented (`seg2`): surface2, 1 dp border, radius 12, padding 3, gap 2; item padding 8/4 radius 9, 12 sp/600; selected = surface + shadow + text colour.
* Bottom sheet: top radius 26, padding 10/18/20, gap 12, shadow 0 −10 40 rgba(0,0,0,.18), 38 × 4 dp handle (border2, radius 2, margin-bottom 4). Scrim over everything above.
* Toggle 42 × 24 radius 99 (off border2 / on accent), 18 dp white knob inset 3. Radio 22 circle, 2 dp border2 (on: accent border + 10 dp accent dot).
* Steps bar: 3 segments, height 5, radius 3, gap 6, on = accent, off = border2.
* PIN dots: 18 dp circles, 2 dp border2 (filled = accent fill + accent border), gap 12, centred. Keypad: 3 columns gap 10, key height 56 radius 16 surface + 1 dp border; empty key (index 9) invisible; ⌫ on index 11.
* Photo box: height 96, radius 14, dashed 1.5 dp border2, surface; filled variant `photo.f`: no border, 135° gradient accentSoft → surface2, accent content.
* Hero: accent fill, radius 20, padding 16 (Help video card).
* FAB: 54 × 54, radius 18, accent, shadow 0 8 20 rgba(0,0,0,.22), right 16, bottom 88.
* SOS button: 62 circle, `crit`, white 15/800 "SOS", shadow 0 8 20 rgba(201,52,47,.4), right 14, bottom 92.
* Pulse (job alert): 110 dp, rings white@18 % (full) and @22 % (inset 14), centre white circle (inset 28) with accent bell 32.
* Bottom nav: height 68 (driver 76) + system nav-bar inset, top 1 dp border, `surface`; item = icon over label, gap 3; **active** = accent label and the icon sits in a 46 × 28 pill, radius 14, accentSoft (icon 20, padding 4/13).
* Map box: radius 18, 1 dp border, mapSea background; attribution text 9 sp muted, left 8 bottom 5: "© OpenStreetMap · MapLibre".
* Shadows: use `shadow` token (0 1 2 rgba(16,24,40,.05)) only for cards flagged `.card` and selected segmented items.

### 4.4 Components to build in `:core-design` (all stateless, all previewed)

`TcAppBar` · `TcIconButton` · `TcButton(variant,size)` · `TcCard` · `TcChip(kind)` · `TcAvatar` · `TcIconBox(kind)` · `TcField` · `TcSegmented` · `TcToggle` · `TcRadio` · `TcStepsBar` · `TcPinDots` · `TcKeypad` · `TcTimeline` · `TcBottomNav(role)` · `TcSheet` + `TcScrim` · `TcFab` · `TcSosButton` · `TcPulse` · `TcPhotoBox` · `TcProgressBar` · `TcKpiTile` · `TcListRow` · `TcHeroCard` · `TcNote` · `TcLogoTile` · `TcMapSurface` · `TcBarChart` · `TcDonut` · `TcSnackbar`. Status kinds: `ok, warn, crit, info, accent, neutral`.

---

## 5. Icons

Run `python tools/gen_icons.py` (already done once): converts the mockup's `IC` dictionary (45 stroke icons: home, map, box, truck, users, chart, bell, cog, user, phone, nav, check, x, plus, search, filter, camera, chev, back, warn, clock, doc, lock, globe, palette, help, download, shield, vol, logout, star, edit, cal, id, more, sos, battery, mail, whatsapp, play, upload, key, list, tag, mic) into `res/drawable/ic_<name>.xml` — 24 × 24 viewport, stroke 1.8, round caps/joins, no fill.

* Render with `Icon(painterResource(...), tint = <current colour>, modifier = Modifier.size(20.dp))`; the mockup default is 20 dp, other sizes are passed per use (see the HTML `ic('name', size)` calls).
* Verify the compact arc syntax (`a2 2 0 004 0`) renders correctly on API 26 **and** latest by screenshotting an **Icon Sheet** (debug) next to the HTML icons from the mockup; fix any broken path by expanding arc flags. Report the result.

---

## 6. Languages and strings

* App languages: **Malayalam** (default for drivers) and **English**; switch at runtime (Settings → Language, and the chip on the driver login) via `AppCompatDelegate.setApplicationLocales`, no restart.
* Build `values/strings.xml` (en) and `values-ml/strings.xml` from `reference/strings_pairs.json` (102 EN→ML pairs: the 53-entry mockup dictionary plus the inline Malayalam on D2–D5, V1, V3, V5, V8, V10, S2…). Resource key = `<screen>_<slug>` or shared `common_<slug>`.
* `reference/strings_untranslated_in_ml.json` lists strings that are **English in the mockup's Malayalam mode** (registration labels, permission descriptions, help rows, settings rows, consent text…). Keep the **English text** in `values-ml` for exactly these (to match the reference), tagged `<!-- TODO-ML -->`, and generate `docs/ml_translation_todo.md` listing them. **Do not invent Malayalam translations.**
* In the mockup, only driver-sized screens use `t()`; admin screens look identical in both languages. Keep that: admin screens render English text in both locales.
* Malayalam text must not clip at default and largest font scale (use `lineHeight` ≥ 1.35 for ml, no fixed heights on text containers).

---

## 7. Navigation and behaviour

```
A1 Splash ──(session? → role home)──► A2 Role select ──Admin──► A3 Admin sign in ──► Admin shell
                                         └──Driver──► D1 Driver login ──► (first run) D6 Permissions ──► Driver shell
D1 ──Register as Driver──► D2 ─► D3 ─► D4 ─► D5 Pending approval ──(approved)──► D1
```

* **Admin shell** (bottom nav, in order): Home(M1) · Map(M2) · Shipments(M3) · Fleet(M7) · More(M14). Fleet also reaches Vehicles(M11); More reaches Reports(M12), Vehicles(M11), Driver approvals(M9 → M10), App versions(M15), Settings(S1). Dashboard bell → Alerts(M13) (keeps *More* highlighted — as the mockup). Shipments row → M4; FAB → M5 → "Choose driver & vehicle" → M6 sheet. Fleet row → M8. M14 "Sign out" → A2.
* **Driver shell** (bottom nav): Home(V1) · History(V9) · Alerts(V11) · Profile(V12). Profile cog → S2. A new job opens V2 as a **full-screen** (lock-screen capable) alert; Accept → V4; Reject → V3 sheet. V4 Start trip → V5; V5 Navigate → V6 sheet; V5 "Reached drop" → V7 → V8 → V1. SOS button (V1, V5) → V10 sheet (**hold to send**). S2 → S3, S4, S5-style rows, S7. Update card → U1 sheet; U2 explainer before first install; U3 blocks the app.
* Sheets (M6, V3, V6, V10, U1) are modal bottom sheets over their parent screen with the 45 % scrim; the parent app bar is visible but dimmed exactly as in the PNG.
* Interactive in this build: keypad (fills/clears PIN dots, 4 digits), segmented controls, toggles, radios, theme tiles, language radios, role buttons, all navigation above, light/dark/auto, EN/ML. Everything else is static fixture data.
* Theme/mode/lang persist in DataStore. Theme screen (S3) shows live preview and changes the app theme immediately.
* Edge-to-edge: accent-filled screens (A1, V2) draw behind the status bar; choose status-bar icon colour from luminance. Content starts below the status-bar inset; bottom nav sits above the navigation-bar inset.

---

## 8. Screen manifest — all 47

`drv` = driver-sized (16 sp base, bigger controls). File name of the reference = `<ID>_<slug>.png`. For every screen, open the matching `.html` + `.layout.json` and copy structure, spacing and text exactly; the notes below only call out what is hard to see.

### Onboarding & access (9)

| ID | slug | Composable | drv | Notes |
|---|---|---|---|---|
| A1 | splash | `SplashScreen` | – | Full accent background; centred logo tile 84 dp r26 white@16 % with truck icon 44; title 28/800; tagline 80 % opacity (ML variant "ലോഡ് · യാത്ര · ഡെലിവറി"); "v2.1.0 · Kerala" 11.5 sp 70 % opacity, bottom 34 dp. Also configure the Android 12+ SplashScreen API with accent background so there is no white flash. |
| A2 | role_select | `RoleSelectScreen` | – | Two cards (gap 12, padding 18): `ico` + 16 sp bold title + small muted line + button (Primary "Continue as Admin"; Outline "Continue as Driver"; the driver icon box uses the `ok` tint). Legal note pinned to the bottom. |
| D1 | driver_login | `DriverLoginScreen` | ✔ | Top row: ok icon box (truck 22) + neutral chip "മലയാളം · EN" (taps toggle language). Field with phone icon. PIN dots **3 of 4 filled** in fixture. Keypad 3×4. Button Lg "Login"; Text button "Register as Driver". |
| D2 | register_mobile_pin | `RegisterMobilePinScreen` | ✔ | Back app bar; steps 1/3; caption "1 / 3 · Mobile & PIN"; field; "Set a 4-digit PIN" + dots (2 filled) + keypad; Lg "Next". |
| D3 | register_identity | `RegisterIdentityScreen` | ✔ | Steps 2/3; Full name; Emergency contact name/phone; Relation segmented (Spouse selected · Parent · Child · Other). |
| D4 | register_licence | `RegisterLicenceScreen` | ✔ | Steps 3/3; licence number; Type + Expiry side by side; two photo boxes (front = filled "Front captured", back = dashed "Back · tap to capture"); Vehicle segmented (Company fleet / Owner-operator); consent row (toggle + 11.5 sp muted text); Lg "Submit for approval". See F3. |
| D5 | pending_approval | `PendingApprovalScreen` | ✔ | Centred: warn icon box with clock 40; h1; muted body; status card with 3 rows (chip warn `PENDING_VERIFICATION`); Outline button with phone icon "Call office". |
| D6 | permissions | `PermissionsScreen` | ✔ | Title "Permissions"/"അനുമതികൾ"; three cards (Location, Notifications, Battery): icon box + title + description + Sm button ("Allow" ×2, Outline "Open guide"); Lg "Next". |
| A3 | admin_sign_in | `AdminSignInScreen` | – | Icon box (chart 22); h1 "Admin sign in"; fields Email (mail icon) and Password (lock icon, dots); "Keep me signed in" toggle row + "Forgot?"; Primary "Sign in"; card with shield "2-step verification"; note. |

### Admin & dispatcher (15)

| ID | slug | Composable | Notes |
|---|---|---|---|
| M1 | dashboard | `DashboardScreen` (nav: Home) | App bar "Hello, Anil" + bell with pip. 2×2 KPI grid (On trip 7 "+2 today", Available 2, Pending 5 "1 overdue", Delayed 2 "check now"; sub-text colours per reference). SOS card (crit-bordered, crit icon box, Sm "Call"). "Live trips" + "View map" link. Two trip cards with chip (On time ok / Delayed warn), progress bars 62 %/38 % — bar colour per the reference. |
| M2 | live_map | `LiveMapScreen` (nav: Map) | Title + filter icon button. Map card fills the space; **vehicle sheet** is a *non-modal* panel docked just above the bottom nav (bottom = 68 dp): avatar SK, plate, driver · code, chip "Moving"; 3 stat tiles (Speed 54 km/h, Updated 12 s, ETA 2:40 pm); Outline "Timeline" + Primary "Call" (Sm). Map = `TcMapSurface` (see 8.1). |
| M3 | shipments | `ShipmentsScreen` (nav: Shipments) | Search icon button; segmented All/Active/Pending/Done; 5 cards (code · chip, route bold, "driver · 22 Nov, 6:00 am"); FAB plus. |
| M4 | shipment_detail | `ShipmentDetailScreen` | Back + more. Summary card (chip In transit, "8.5 t · Vegetables", route, driver · plate). "Timeline" heading + `TcTimeline` 7 steps (Created … Delivered; last two pending). Buttons Reassign / Cancel — equal width (F1), pinned bottom. |
| M5 | new_shipment | `NewShipmentScreen` | Back title; steps 2/3 "2 / 3 · Goods & schedule"; fields Pickup, Drop, Goods + Weight (t) side by side, Consignee, Pickup time; capacity warning card (warn icon box + "**Over capacity.** 8.5 t exceeds Mini truck 3 t. Pick another vehicle."); Primary "Choose driver & vehicle". |
| M6 | assign_driver | `AssignDriverSheet` over `NewShipmentScreen` | Sheet "Assign driver" + close ib; 4 driver rows (first selected with accent border + check; others chips Free/Busy/Offline); Primary "Assign & notify driver". |
| M7 | drivers | `DriversScreen` (nav: Fleet) | Plus ib; search field (placeholder); segmented All/Free/On trip/Leave; 5 rows (avatar, name, plate, chip). |
| M8 | driver_detail | `DriverDetailScreen` | Large avatar, name, phone, chip; 3 stat tiles (Trips 49, On-time 82 %, Rating 4.6); details list (Vehicle, Licence, Emergency, Device); Outline "Reset PIN" / "Revoke device" (F1). |
| M9 | driver_approvals | `DriverApprovalsScreen` (nav: More) | Back; chip warn "2 new"; 2 request cards (avatar, name, meta, time; two neutral chips; Sm "Review" + Sm Ok "Approve"); info card with bell. |
| M10 | review_driver | `ReviewDriverScreen` | Header row; details list (Licence no., Type / expiry, Vehicle, Emergency); two filled photo boxes with `id` icon ("Licence front/back"); shield info card; **No** "Reject" + **Ok** "Approve" equal width (F1). |
| M11 | vehicles | `VehiclesScreen` (nav: Fleet) | Back + plus; segmented All/Available/On trip/Service; 4 rows (truck icon box, plate, "Lorry · 10 t · Insurance in 300 d", chip). |
| M12 | reports | `ReportsScreen` | Back + download ib; segmented Week/Month(selected)/Quarter; 2 KPI tiles (On-time 94 % "+2 pt", Accept time 4.2 m "target 5"); `TcBarChart`; `TcDonut` card; Export PDF / Export Excel equal width (F1). |
| M13 | alerts_centre | `AlertsCentreScreen` (nav: More highlighted) | Chip crit "4 open"; segmented All/SOS/Delays/Docs; 5 alert rows with tinted icon box + Sm outline button (Call, Reassign, View, Reassign, Open). |
| M14 | more | `MoreScreen` (nav: More) | Profile card (avatar AK, "Anil Kumar", "Owner · Super Admin", chevron); list (Reports, Vehicles, Driver approvals + warn chip "2", App versions, Audit log, Settings); "Sign out" card with logout icon. |
| M15 | app_versions | `AppVersionsScreen` | Release card (v2.1.0 · latest, chip ok "Rollout 100 %", bar, "9 of 12 drivers updated · minimum supported v2.0.3"); 4 driver rows with chips Latest/Outdated/Outdated/Below minimum; Outline "Halt rollout" + Primary "Force update" (F1). |

### Driver (12, all `drv`)

| ID | slug | Composable | Notes |
|---|---|---|---|
| V1 | home_no_job | `DriverHomeEmptyScreen` (nav: Home) | App bar "Good morning, Suresh" (ML "സുപ്രഭാതം, Suresh") + bell (no pip). Centred: 110 dp icon box r36 with truck 54; h1 "No job right now"; muted line (lh 1.5); chip ok "Online · Available"; SOS button. |
| V2 | job_alert | `JobAlertScreen` | Full-screen accent. Pulse; "NEW JOB" (uppercase, 85 %, tracking .06em); destination 30/800 "Kasaragod / → Malappuram"; translucent info strip (Goods Vegetables · Weight 8.5 t · Today 6:00 am); Lg translucent "Speak job" with vol icon; **Reject (No, Xl) + Accept (Ok, Xl) equal width** (F1). Declare as a full-screen-intent activity with a notification fallback. |
| V3 | reject_reason | `RejectReasonSheet` | Sheet h1 "Reason"; 3 radio rows (Vehicle problem selected, On leave, Too far); Xl No "Reject"; Lg Outline "Later". |
| V4 | job_details | `JobDetailsScreen` | Back + title code + chip info "Accepted". Card with Pickup/Drop rows (ok / crit icon boxes with map icon 22). Goods/Weight card. Navigate + Call office (F1). Xl Primary "Start trip" with play icon, pinned. |
| V5 | active_trip | `ActiveTripScreen` | Title "Kasaragod → Malappuram". Timeline card (Reached pickup 7:50 am, Loaded 8:45 am, In transit "Now", Reached drop, Delivered; F2). Navigate + call icon button (F1). Xl "Reached drop" with check icon. SOS button. |
| V6 | choose_navigation_app | `NavAppSheet` | Sheet "Navigate"; rows Google Maps (map icon), Vela (nav icon, chip ok "Default"), OsmAnd (globe); note. Real behaviour (later): geo intent. |
| V7 | proof_of_delivery | `ProofOfDeliveryScreen` | Back + title; "Photo of goods" label + filled photo box ("Photo taken · 280 KB"); Receiver name input; OTP row (key icon, "OTP (optional)", chip neutral "Skip"); Xl Ok "Complete delivery". |
| V8 | delivered | `DeliveredScreen` | Centred: ok icon box with check 58; h1 "Delivered"; summary card (TC-2026-000131 · 2:36 pm; Receiver name · Mathew Traders); Xl Primary "Back to home". |
| V9 | history | `HistoryScreen` (nav: History) | Two stat tiles (Trips 49, On time 82 %); 4 rows (ok icon box with check, route bold, date muted, chevron). |
| V10 | sos | `SosSheet` | Sheet: crit icon box (sos 44), h1 "Send SOS?", muted explanation; Xl No "Hold to send" (**long-press 1.5 s with visible progress**); Lg Outline "Cancel"/"വേണ്ട". |
| V11 | alerts | `DriverAlertsScreen` (nav: Alerts) | One list card, 4 rows (icon box, bold title, muted time). |
| V12 | profile | `DriverProfileScreen` (nav: Profile) | Cog ib; large avatar, name, chip ok "Approved"; list (Vehicle, Licence, Language); Lg Outline "Log out" with logout icon. |

### Settings & system (11)

| ID | slug | Composable | drv | Notes |
|---|---|---|---|---|
| S1 | settings_admin | `SettingsAdminScreen` | – | Back; profile card with edit icon; list rows (Profile, Theme "Indigo · Auto", Language "English", Notifications, Security, Help, App version "v2.1.0") each with icon box + chevron; note "Theme choice syncs to your profile". |
| S2 | settings_driver | `SettingsDriverScreen` | ✔ | Larger rows (Theme, Language, Voice read-out "On", Notifications, Help); "Text size" card with segmented Normal / Large (selected) / Extra large. |
| S3 | theme | `ThemeScreen` | – | Segmented Light / Dark / Auto(selected); caption; 2×2 theme tiles (Indigo selected with check and accent border, Emerald, Graphite, Sunset; each = colour swatch + name); Preview card (primary button + 3 chips). **Functional.** |
| S4 | language | `LanguageScreen` | ✔ | Caption; radio rows മലയാളം (selected) / English; info card with globe. **Functional.** |
| S5 | notifications | `NotificationsScreen` | – | List with 6 toggles (first four ON, last two OFF); info card with bell. |
| S6 | security | `SecurityScreen` | – | List: App lock (toggle on), 2-step verification (toggle on), Change password (chevron); "Signed-in devices": two cards (current device chip ok "Current"; other with Sm Outline "Revoke"). |
| S7 | help | `HelpScreen` | ✔ | Hero accent card (play icon 30, "Watch the 2-minute guide", "In Malayalam"); list (WhatsApp support, Call office, How to accept a job, Fix tracking on my phone, Privacy & location). |
| S8 | app_version | `AppVersionScreen` | – | Logo tile + name + "v2.1.0 (build 21) · direct"; list (Channel, Minimum supported, Last checked); What's new card; Primary "Check for updates". |
| U1 | update_available | `UpdateSheet` over `DriverHome` | ✔ | Sheet: download icon box, "New version available", "v2.1.1 · 18 MB"; release-notes card; progress bar at 64 % "Downloading… 64%"; Later (Outline) + Update (Primary) equal width (F1). |
| U2 | install_permission | `InstallPermissionScreen` | – | Centred warn icon box (shield 42); h1; explanation; 3 numbered steps card (avatar-style numbers); Primary "Open settings". |
| U3 | forced_update | `ForcedUpdateScreen` | ✔ | Centred crit icon box (warn 44); h1 "Update required"; body; card "Installed v2.0.1 → v2.1.1"; Xl Primary "Update"; note "Never shown during an active trip". |

### 8.1 Map and charts

* **TcMapSurface**: Phase A — Canvas implementation replicating the mockup exactly: viewBox 290 × 470 `xMidYMid slice`; land polygon from `COAST` points via `P(lng,lat) = ((lng−74.55)·92+8, (12.95−lat)·92+8)`, fill `mapLand`, stroke `mapLine` 1.5 round join; 6 city dots (r 2.5, `surface` fill, `muted` stroke) with 8 sp / 600 / `text2` labels, text anchored end at x−7, y+3 (Kannur, Kozhikode, Thrissur, Kochi, Kollam, Thiruvananthapuram); vehicle pins: 9 dp circle in status colour + 2 dp `surface` stroke + white truck glyph; selected pin gets a 15 dp halo @20 %. Pins (lng, lat, colour, id): (75.1,12.1,accent,a) (75.8,11.1,crit,b) (76.2,10.1,accent,c)* (76.8,9.2,warn,d) (76.5,10.7,accent,e); `*` selected. Copy the exact `COAST` array from `mockup/index.html`. Phase B (later, not this task): swap the internals to MapLibre behind the same composable signature.
* **TcBarChart** (M12): 270 × 100 viewBox, scaled to card width; 6 bars, values `[49,44,42,40,36,34]`, `x = 6 + i·44`, height `v·1.8`, bottom-aligned, width 30, radius 5, accent at opacity `1 − i·0.1`.
* **TcDonut** (M12): 84 dp; track r 15.9 stroke 6 `border2`; completed arc `ok` (96 of 100 of circumference) starting at −90°; cancelled arc `crit` (3 of 100) following; legend "● 246 completed" (ok), "● 8 cancelled" (crit), title "Completed vs cancelled" bold.

---

## 9. Fixtures

Create `:core-data/fixtures/MockData.kt` with **exactly** the mockup data, served by the `Fake*Repository` classes. Source them from the `layout.json`/`html` of the screens (names, plates, codes, times, counts). Highlights: admin **Anil Kumar** (owner); driver **Suresh Kumar** (KL-07-AZ-1200, 49 trips, 82 %, 4.6); registrant **Rajan Nair** (+91 98470 55501); shipments TC-2026-000125/131/133/136/138/141; vehicles KL-07-AZ-1200 (10 t), KL-08-BX-1571 (3 t), KL-02-HR-3797 (10 t), KL-13-JQ-4168 (1.5 t); app versions v2.1.0 / v2.0.3 / v2.0.1.

The Gallery uses **state fixtures** that reproduce each screenshot (e.g. PIN with 3 dots filled, consent ON, selected tabs). Real app defaults can differ (F3), the fixtures must not.

---

## 10. Verification harness (this is how "100 % perfect" is proven)

### 10.1 Debug Gallery
`GalleryActivity` (debug only) lists all 47 screens grouped like the mockup tabs and opens any screen directly in its fixture state via deep link `truckconnect://gallery/<ID>?theme=indigo&mode=light&lang=en`. It has the same controls as the mockup header (4 theme swatches, Light/Dark, EN/ML).

### 10.2 Screenshot tests (primary gate)
* Use **Roborazzi + Robolectric** (native graphics) — or Paparazzi if Roborazzi cannot load the variable fonts — with qualifiers `w324dp-h648dp-xhdpi`, **no system bars**, font scale 1.0. Output PNG must be **648 × 1296**.
* One test per screen per matrix cell; output file name exactly `<ID>_<slug>.png` into `build/compose_png/<theme>-<mode>-<lang>/`.
* Matrix: **indigo-light-en, indigo-light-ml, indigo-dark-en, indigo-dark-ml** for all 47 screens; plus **emerald, graphite, sunset** (light and dark, en) for at least A2, M1, M2, M12, V1, V2, V5, S3.
* Compare: `python tools/compare.py build/compose_png/indigo-light-en reference/png/indigo-light-en --out build/compare/indigo-light-en` for every cell. A screen passes only if the report says PASS (≤ 1.5 % of pixels off by > 32/255 after a 1 px blur). Missing files and size mismatches fail.
* **Screens where the mockup is defective (F1, F2)** — V2, V4, V5, M4, M8, M10, M12, M15, U1 — cannot match the reference in the broken rows. `reference/masks.json` contains the **fixed, pre-computed masks** (button rows for F1, timeline card for V5 and M4 for F2); `compare.py` applies them automatically to both images. These are the only masked areas allowed. Do not edit the file, do not add masks, do not enlarge them. For these 9 screens additionally attach a side-by-side of your screenshot vs the reference so the intended fix (equal-width pair, aligned timeline) is visible.
* **Never loosen the threshold or enlarge masks to make a screen pass.** If a screen fails, fix the code.

### 10.3 Emulator checks (moreplay MCP, per PRD §16.1)
After each phase: build the debug APK, install on an API 26 low-end emulator and a current-API emulator, open each Gallery deep link, `observe` the screen text, save one screenshot per screen to `evidence/` (git-ignored), and confirm: labels/order/primary action match the manifest; no clipped or overlapping text at default and largest font scale; no crash in logs. Navigation flows to record and commit under `e2e/mobile`: `splash-to-role`, `driver-login-to-home`, `driver-register`, `admin-login-to-dashboard`, `admin-bottom-nav`, `create-shipment-to-assign`, `job-alert-accept-to-delivered`, `reject-reason`, `settings-theme`, `settings-language`, `forced-update`.

### 10.4 Rules for you, the agent
1. After a failed check: read the diff image and the layout JSON of that screen **before** changing code; make one fix; rerun that screen.
2. Do not edit `reference/`, `mockup/`, or comparison thresholds.
3. Every claim in your reports is backed by pasted tool output (report.md tables, test summaries, `grep` results). **No summaries without evidence.** If you did not run it, say "not run".
4. If you cannot match something, say exactly what and why; do not silently approximate.
5. Add a unit test that fails if any of the 47 manifest IDs has no composable, no Gallery entry or no screenshot test.

---

## 11. Phases and exit gates

| Phase | Work | Exit gate (all must be shown with output) |
|---|---|---|
| **P0 Foundation** | Project + modules, fonts, icons, strings (en/ml tables), `TcColors/TcType/TcDimens/TcShapes`, theme switching, Gallery shell, test harness, `capture_mockup.py --all` | Debug build installs and runs; token unit test green for all 8 theme/mode combinations; Icon Sheet matches the mockup icons; zero `.dp/.sp/Color(0x` outside `:core-design` |
| **P1 Components** | Every `Tc*` component with `@Preview` and a Component Gallery page | Visual check of each component in light/dark/4 themes vs the matching mockup element crops; attach screenshots |
| **P2 Onboarding & access** | A1, A2, A3, D1–D6 | compare.py PASS 9/9 in all 4 matrix cells (no masks) |
| **P3 Driver** | V1–V12 | PASS 12/12 per cell (only the fixed masks in `reference/masks.json` for V2, V4, V5) |
| **P4 Admin** | M1–M15 (+ map and charts) | PASS 15/15 per cell (only the fixed masks for M4, M8, M10, M12, M15) |
| **P5 Settings & updates** | S1–S8, U1–U3 | PASS 11/11 per cell (only the fixed mask for U1) |
| **P6 Theme matrix & languages** | Emerald/graphite/sunset, all dark variants, ML runs, largest font scale check | Report tables for every cell; `docs/ml_translation_todo.md` generated |
| **P7 Navigation & behaviour** | Wire section 7 flows, persistence, role routing, stubs for missing targets | moreplay flows green; list of every `ComingSoonSnackbar` target |
| **P8 Final audit** | Re-run everything from clean; produce `docs/fidelity_report.md` | Table: 47 screens × 4 cells with numbers; list of `MOCKUP-FIX` sites (masks are the fixed ones in `reference/masks.json`); list of anything not run |

Do not start a phase until the previous gate is shown. Commit after each phase with the report attached.

---

## 12. Out of scope (do not build now)

Backend/API calls, Supabase, FCM, real GPS/foreground service, real MapLibre tiles, real APK updater/PackageInstaller, Play flavour, admin web console, new screens not in the 47. Keep the architecture ready (repository interfaces, ViewModels, state classes) so these slot in later without UI changes.

## 13. Final report format

`docs/fidelity_report.md`: (1) the 47 × 4 compare table pasted from `report.md`; (2) the F1–F4 sites; (3) side-by-sides for the 9 defect screens; (4) strings still English in ML (`TODO-ML` count); (5) anything not run or not matching, stated plainly; (6) how to run each tool. Nothing else.
