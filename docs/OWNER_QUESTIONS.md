# TruckConnect — Owner Questions & Decisions

This log tracks architectural and design questions requiring product owner direction. In accordance with Ground Rule 8, recommended defaults are implemented immediately so work proceeds without blocking.

---

### Question 1: Driver History Screen Requirements (BUG-13)
* **Status:** Open (Default implemented per Section 7.4)
* **Context:** The owner reported that History was "not like we needed" but did not specify the exact deficiency.
* **Current Implementation:** Full fidelity V9 screen with:
  - Header statistics: "Trips (42)", "On time (95%)"
  - Range filter chips: "This week", "This month", "All"
  - Status segmented control: "Delivered", "Cancelled"
  - Sticky grouped list by month with route, timestamps, and chevron
  - Tapping an item navigates to Trip Detail (`X10`) with full milestone timestamps, GPS verification checkmarks, receiver name, and Proof of Delivery photo viewer.
* **Specific Clarification Requested for Owner:**
  What specific change is needed for History compared with the approved V9 design?
  - (A) Data fields (e.g. driver earnings / trip payouts / fuel expenses)?
  - (B) Grouping / aggregation (e.g. weekly breakdown, vehicle-specific totals)?
  - (C) Filtering options (e.g. specific date range picker, customer/consignee filter)?
  - (D) Visual layout or export capability?

---

### Question 2: Accessibility Floor Flag (`TcAccessibility.PRD_FLOORS`)
* **Status:** Open (Default: Enabled for Release builds, Disabled for strict v1 pixel test harness)
* **Context:** PRD v2.1 requires driver text ≥ 18 sp and tap targets ≥ 56 dp (admin ≥ 48 dp). In the original HTML mockup, some text and buttons were slightly smaller (14–16 sp, 44–48 dp).
* **Current Implementation:** All dimensions are parameterized through `TcDimens` and `TcType`. `TcAccessibility.PRD_FLOORS` dynamically clamps values to meeting PRD accessibility floors.
* **Clarification:** Confirm whether `PRD_FLOORS` should be permanently ON for all production APK distributions.
