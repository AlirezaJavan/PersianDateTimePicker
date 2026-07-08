# ShamsiPicker — Roadmap

A living, checkbox-driven backlog for `shamsi-core` and `shamsi-picker`.

## How to use this file (for coding agents)

- Pick the **lowest-numbered unchecked item** in the highest-priority phase unless
  the user asks otherwise. Phases are ordered so earlier ones unblock later ones.
- Work one task at a time. A task is only "done" when it **compiles, passes
  `./gradlew test`, and (for UI) is wired into the `sample/` app**.
- **Every phase must update the `sample/` app** to showcase the new capability —
  this is a required task in each phase, never optional. New features get a
  visible, interactive demo a user can tap through, not just library code.
- When a task is genuinely complete, change `- [ ]` to `- [x]` and append
  ` — done <YYYY-MM-DD> (<one-line note / commit>)`.
- Do **not** check a box for partial work. If you stop mid-task, leave it
  unchecked and add a `> NOTE:` line under it describing what remains.
- Keep public API changes source-compatible where possible; this is a published
  Maven Central library (`io.github.alirezajavan:shamsi-*`). Breaking changes go
  behind a major-version bump and must be called out in the task note.
- Update `README.md` and add/adjust tests as part of the same task, not later.

---

## Phase 0 — Calendar-system abstraction (foundation for multi-calendar)

> **Why this is first:** the whole library hardcodes Shamsi (`ShamsiCalendar`
> object, `ShamsiDate` model, Persian month/weekday names). To offer a Gregorian
> (and later Hijri) picker that reuses the exact same wheel/calendar UI, the
> calendar-specific logic must sit behind a common interface. Everything in
> Phase 1 depends on this. If the user only wants a quick Gregorian picker and
> accepts code duplication, they can skip to the "lightweight" note at the end of
> Phase 1.

- [x] Define a `CalendarSystem` interface in `shamsi-core` exposing the operations
      the UI needs, calendar-agnostic:
      `yearRange`, `monthNames(year)`, `weekdayNames`, `monthLength(year, month)`,
      `firstWeekdayOfMonth(year, month)`, `isLeapYear(year)`,
      `toEpochDay(y,m,d)` / `fromEpochDay(epochDay)`, and `today(zone)`. — done 2026-07-07
- [x] Implement `ShamsiCalendarSystem : CalendarSystem` by delegating to the
      existing `ShamsiCalendar` algorithm (no behavior change). — done 2026-07-07
- [x] Implement `GregorianCalendarSystem : CalendarSystem` backed by
      `java.time.LocalDate` (weekday order configurable — see Phase 1 first-day-of-week). — done 2026-07-07
- [x] Introduce a calendar-agnostic value type (e.g. `CivilDate(system, year, month, day, hour, minute)`)
      **or** decide `ShamsiDate` stays the canonical internal type and Gregorian
      values convert at the UI boundary. Record the decision in a `> NOTE:` here. — done 2026-07-07
      > NOTE: Decided to keep `ShamsiDate` as the internal carrier for now to
      > maintain backward compatibility, but treat its fields as "calendar-agnostic"
      > year/month/day when a non-Shamsi `CalendarSystem` is in use. We may rename
      > it to `CivilDate` in Phase 1 with a `ShamsiDate` typealias.
- [x] Add unit tests: round-trip conversions, leap years, and month lengths for
      **both** systems, plus a shared cross-system epoch-day equality test
      (same epoch day ⇒ same calendar day in each system). — done 2026-07-07
- [x] Keep `ShamsiCalendar`'s current public API intact (delegate to it) so no
      existing consumer breaks. — done 2026-07-07
- [x] **Sample app:** add a small "developer/debug" section to `sample/` that
      prints, for a picked date, the value resolved through **both**
      `CalendarSystem` implementations (Shamsi + Gregorian) side by side — proves
      the abstraction works before Phase 1 builds real UI on it. — done 2026-07-07

---

## Phase 1 — Gregorian & selectable calendar type (the headline feature)

> Goal: the user picks Shamsi **or** Gregorian (extensible to more), and the same
> dialogs render either. Wheel and Calendar styles both work.

- [x] Add a `CalendarType` enum (`Shamsi`, `Gregorian`) mapping to the
      `CalendarSystem` implementations from Phase 0. — done 2026-07-07
- [x] Add `calendarType: CalendarType = CalendarType.Shamsi` to every picker
      config (`ShamsiDatePickerConfig`, `ShamsiTimePickerConfig`,
      `ShamsiDateRangePickerConfig`, `ShamsiTimeRangePickerConfig`). Default keeps
      existing behavior. — done 2026-07-07
- [x] Refactor `ShamsiDatePicker.kt` (`WheelDatePicker`, `CalendarDatePicker`,
      `NavRow`) to read month names, weekday names, year range, and bounds from the
      active `CalendarSystem` instead of calling `ShamsiCalendar.*` directly. — done 2026-07-07
- [x] Localize numerals: introduce a `NumberFormatter` abstraction so Gregorian
      renders Latin digits while Shamsi keeps Persian digits (currently hardcoded
      via `PersianNumber`). Wire it through the wheel labels and calendar cells. — done 2026-07-07
- [x] Localize static strings: dialog title/confirm/cancel and the از/تا range
      labels need English (and default) variants. Add English `strings.xml` values
      and a locale/`CalendarType`-aware selection, or expose them as config text. — done 2026-07-07
- [x] Handle first-day-of-week: Shamsi starts Saturday; Gregorian commonly starts
      Sunday or Monday. Add `firstDayOfWeek` to the config with a sensible default
      per calendar system, and use it in the calendar-grid layout. — done 2026-07-07
- [x] Consider renaming public-facing types to calendar-neutral names
      (e.g. `DatePickerDialog` wrappers) **without** removing the `Shamsi*` names —
      keep `Shamsi*` as typealiases/thin wrappers for backward compatibility.
      Record the decision in a `> NOTE:`. — done 2026-07-07
      > NOTE: Decided to keep `ShamsiDate` as the canonical internal carrier for
      > simplicity and backward compatibility. Added `DatePickerDialog` etc. as
      > neutral wrappers/aliases to the UI layer.
- [x] **Sample app:** add a `CalendarType` toggle to `sample/MainActivity.kt`
      demonstrating Shamsi vs Gregorian for date, time, and range pickers, with
      the confirmed value shown formatted in the chosen calendar. — done 2026-07-07
- [x] Update `README.md`: new "Calendar type" section, update feature list and
      the badges/description ("Shamsi **and** Gregorian"). — done 2026-07-07
- [x] Tests: repeat the existing picker-logic tests against `Gregorian` bounds
      (year/month/day clamping, leap-year Feb 29, min/max enforcement). — done 2026-07-07

> **Lightweight alternative (only if Phase 0 is skipped):** ship a parallel
> `GregorianDatePickerDialog` that duplicates the wheel/calendar UI backed by
> `java.time`. Faster to land, but doubles maintenance and diverges over time —
> not recommended.

---

## Phase 2 — Combined Date + Time picker

> Common real-world need (appointments/reminders) that today means chaining two
> dialogs and merging results manually. Builds cleanly on Phase 1.

- [x] Confirm `ShamsiDate` (which already carries `hour`/`minute`) is the value
      type, or add an explicit `ShamsiDateTime`. Record decision in a `> NOTE:`. — done 2026-07-08
      > NOTE: Confirmed `ShamsiDate` as the value type for the combined picker, as
      > it already includes `hour` and `minute` fields and is used consistently
      > across the library.
- [x] Add `ShamsiDateTimePickerConfig` (initial value, min/max, style,
      `calendarType`). — done 2026-07-08
- [x] Build `ShamsiDateTimePickerDialog` as a unified UI with date and time
      wheels visible simultaneously (3-item visible count for compactness). — done 2026-07-08
      > NOTE: Originally planned as a tabbed/two-step UI, but pivoted to a
      > stacked wheel layout based on user feedback for better "all-at-once"
      > visibility and quicker selection. Reuses `WheelDatePicker` and
      > `TimePicker` with a new `visibleCount` parameter.
- [x] Add a combined formatter (`ShamsiDateFormatter.longWithTime`, `shortWithTime`). — done 2026-07-08
- [x] **Sample app:** add a "Date + Time" demo entry that opens the combined
      dialog and displays the merged result. Tests: step navigation, min/max
      spanning date+time. — done 2026-07-08

---

## Phase 3 — Theming & customization API

> Apps with brand colors currently can't restyle pickers without forking.

- [x] Define `ShamsiPickerColors` (selected/unselected text, wheel highlight,
      range-strip colors, calendar today/selected backgrounds, disabled alpha). — done 2026-07-08
- [x] Define `ShamsiPickerTypography` (or override slots over
      `MaterialTheme.typography`) for weekday labels, numerals, headers. — done 2026-07-08
- [x] Add `ShamsiPickerDefaults.colors(...)` / `.typography(...)` with
      Material-derived defaults so existing call sites keep working. — done 2026-07-08
- [x] Thread `colors`/`typography` through the pickers (prefer a separate optional
      composable parameter over config bloat). — done 2026-07-08
- [x] Replace hardcoded `MaterialTheme.*` references in `WheelPicker.kt`,
      `ShamsiDatePicker.kt` (`DayCell`, `SegmentButton`) with the new objects. — done 2026-07-08
- [x] **Sample app:** add a demo that switches between the default theme and a
      custom-branded `ShamsiPickerColors`/typography so the difference is visible
      live. README "Theming" section. — done 2026-07-08
      > NOTE: Also went beyond the original scope of this phase and added
      > per-dialog `ShamsiPickerStrings` (title/confirm/cancel/labels), so
      > consumers can fully re-word each dialog independently of calendar type
      > or device locale, not just restyle colors/typography. Also extracted all
      > previously-hardcoded dp/alpha/count literals in the wheel and calendar
      > grid UI into an internal `ShamsiPickerDimens` object to remove
      > duplication (this was a separate ask, not a roadmap item, but touched
      > the same files so it's noted here).

---

## Phase 4 — Holiday / event markers (Calendar style)

> Frequently requested for Persian calendars: Iranian official holidays + custom
> app events visually marked on the grid.

- [x] Add a `CalendarEvent`/`ShamsiHoliday` model (date + label + optional color). — done 2026-07-08
      > NOTE: `CalendarEvent(date: ShamsiDate, label: String, colorArgb: Int? = null)`
      > lives in `shamsi-core`. Uses a packed ARGB `Int` instead of a Compose `Color`
      > to keep `colorArgb` out of `androidx.*`; the UI layer converts it to `Color`
      > when rendering.
- [x] Add optional `events: List<CalendarEvent>` to the date picker config
      (Calendar style). — done 2026-07-08
- [x] Render a marker dot/underline on matching `DayCell`s; expose the label via
      `contentDescription` (ties into Phase 6). — done 2026-07-08
- [ ] Ship an **opt-in** official Iranian holiday dataset as a separate artifact
      (e.g. `shamsi-holidays`) so `shamsi-core` stays free of yearly data churn.
      > NOTE: Deferred — out of scope for this task. An accurate Iranian holiday
      > calendar needs Hijri (lunar) calculations for religious holidays, which
      > isn't implemented yet (see Phase 7 "Additional calendars"). Do this once
      > Hijri support lands so the dataset isn't fixed-date-only from day one.
- [x] **Sample app:** add a demo that seeds a few holidays/events and opens the
      calendar-style picker so the markers are visible. Tests for event matching
      across month/year boundaries. — done 2026-07-08

---

## Phase 5 — Inline (non-dialog) picker composables

> Lets apps embed pickers directly in a screen (booking forms), mirroring
> Material3's `DatePicker` vs `DatePickerDialog` split.

- [ ] Extract dialog content into standalone `ShamsiDatePicker` / `ShamsiTimePicker`
      composables taking `config` + `onValueChange`, no dialog chrome.
- [ ] Re-implement the `*Dialog` composables as thin wrappers (chrome + confirm/
      dismiss only) around the inline versions.
- [ ] Do the same for the range pickers.
- [ ] **Sample app:** add a screen that embeds an inline picker directly in the
      layout (no dialog) alongside a live-updating result label. README "Inline
      usage" section.

---

## Phase 6 — Accessibility pass

> Quality/compliance item; blocks enterprise/government adoption if missing.

- [ ] Audit `WheelPicker.kt` and calendar cells for `contentDescription` /
      `semantics {}` (selected state, day name, today, disabled).
- [ ] Expose wheel scroll semantics (`scrollBy`/`setProgress`) so TalkBack users
      can adjust values without precise flings.
- [ ] Add instrumented accessibility tests (Compose `SemanticsMatcher`) for date,
      time, and range pickers.
- [ ] **Sample app:** verify every demo screen is fully TalkBack-navigable and
      add a visible "font scale / large text" toggle (or document testing steps)
      so the accessibility work is demonstrable.
- [ ] Document the accessibility support level in `README.md`.

---

## Phase 7 — Nice-to-haves / backlog (unordered)

- [ ] **Kotlin Multiplatform**: move `shamsi-core` off `java.time` (or behind
      `expect`/`actual`) to enable iOS/JS targets; keep the JVM path unchanged.
- [ ] **Additional calendars**: `Hijri` (Islamic) `CalendarSystem` once the
      Phase 0 abstraction is proven.
- [ ] **RTL/LTR correctness sweep**: verify wheel order, arrow direction, and grid
      layout in both LTR and RTL for each calendar type.
- [ ] **Relative/humanized formatting**: "۲ روز پیش" / "in 3 days" helpers in
      `shamsi-core`.
- [ ] **Parsing**: `ShamsiDateFormatter.parse(String)` to complement formatting.
- [ ] **Configurable time granularity**: minute step (5/15/30) and optional
      seconds in the time picker.
- [ ] **12h/24h + AM/PM (ق.ظ/ب.ظ)** display option for the time picker.
- [ ] **Screenshot/UI regression tests** (e.g. Paparazzi/Roborazzi) covering both
      calendar types and both styles.
- [ ] **Docs site / API KDoc publish** (Dokka) linked from the README.

---

## Phase 8 — Sample app UI redesign (do last)

> **Why last:** by now the sample has accumulated demos from every phase, likely
> crammed into one `MainActivity` screen. This phase turns it into a polished,
> showcase-quality app that makes the library look professional in screenshots
> and store listings. Do this only after the features exist so nothing is
> redesigned twice.

- [ ] Introduce a **bottom navigation bar** (Material3 `NavigationBar` +
      `NavHost`) splitting demos into clear destinations — e.g.
      **Date**, **Time**, **Range**, **Date+Time**, **Gallery/Settings**.
- [ ] Give each destination its **own screen** with a clean layout: a short intro,
      the interactive picker trigger(s), and a nicely formatted result card —
      no more single scrolling wall of buttons.
- [ ] Add a **calendar-type + style switcher** in a top app bar or a Settings
      destination so every screen respects the chosen `CalendarType`, style,
      first-day-of-week, and (if built) theme.
- [ ] Build a small **design system** for the sample: consistent spacing,
      typography, cards, and a light/dark theme toggle; ensure full RTL polish for
      Persian and LTR for Gregorian.
- [ ] Add tasteful **motion/transitions** between destinations and when results
      update (respecting reduced-motion / accessibility settings).
- [ ] Showcase **theming** (Phase 3) with 2–3 preset brand themes the user can
      switch between live.
- [ ] Refresh the **screenshots** in `README.md` from the redesigned app; consider
      adding a short demo GIF.
- [ ] Ensure the redesigned sample still exercises **every** feature shipped in
      Phases 1–7 so it doubles as a manual QA surface.
