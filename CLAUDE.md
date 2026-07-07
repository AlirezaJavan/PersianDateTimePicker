# CLAUDE.md — ShamsiPicker

Guidance for any AI/coding agent (and humans) working in this repository. Read
this before writing code. These rules exist because **ShamsiPicker is a published
library** (Maven Central: `io.github.alirezajavan:shamsi-core` / `:shamsi-picker`)
consumed by apps we don't control — correctness, API stability, and clarity
matter more than speed.

Roadmap and open work live in [`docs/ROADMAP.md`](docs/ROADMAP.md). Pick tasks
from there and follow its "done" contract.

---

## 1. Project shape

- **`shamsi-core`** — pure Kotlin/JVM. Date logic, calendar conversions,
  formatting. **Zero Android dependencies.** Never import `android.*` or
  `androidx.*` here. If something needs Android, it belongs in `shamsi-picker`.
- **`shamsi-picker`** — Android library. Jetpack Compose UI (dialogs, wheels,
  calendar grid). Depends on `shamsi-core`.
- **`sample`** — the demo app. Every user-facing feature must be showcased here.

Keep the dependency direction one-way: `picker → core`, `sample → picker`. Core
must stay portable (it's the seam for future Kotlin Multiplatform — see roadmap).

---

## 2. Non-negotiables

- **Explicit API mode is on.** Every public declaration needs an explicit
  visibility modifier and explicit return types. Default to `internal`/`private`;
  make something `public` only when a consumer genuinely needs it — public API is
  a permanent contract.
- **Backward compatibility.** Don't break or rename existing public types,
  functions, or parameters without a major-version bump. Prefer additive changes;
  keep old names as `@Deprecated` typealiases/wrappers when you must rename.
- **Formatting is enforced.** Spotless + ktlint. Run `./gradlew spotlessApply`
  before finishing. Do not hand-format around the linter.
- **Tests must pass.** Run `./gradlew test`. New logic ships with tests in the
  same change (see §7).
- **Docs stay in sync.** If you change public API or behavior, update KDoc,
  `README.md`, and the relevant roadmap checkbox in the same task.

---

## 3. Engineering principles

Apply these deliberately — cite the one you're following in non-obvious code.

### SOLID
- **S — Single Responsibility:** one reason to change per class/function. A
  composable that both computes bounds and renders UI should be split.
- **O — Open/Closed:** extend via new types, not by editing switch-ladders. The
  calendar-system abstraction (roadmap Phase 0) exists so new calendars are
  *added*, not bolted into `ShamsiCalendar` with `if (gregorian)` branches.
- **L — Liskov:** every `CalendarSystem` implementation must be fully substitutable
  — no impl that throws on a method the interface promises.
- **I — Interface Segregation:** keep interfaces narrow; UI depends only on the
  calendar operations it actually calls.
- **D — Dependency Inversion:** UI and high-level logic depend on abstractions
  (`CalendarSystem`, formatters), not concrete `ShamsiCalendar`.

### DRY / KISS / YAGNI
- **DRY:** one source of truth. Don't duplicate the wheel/calendar rendering per
  calendar type — parameterize it. (Duplicated `min/max` clamping logic is a smell.)
- **KISS:** the simplest thing that correctly solves the problem. No clever
  one-liners that need a paragraph to explain.
- **YAGNI:** build what the current roadmap task needs. Don't add config flags,
  generic layers, or "future-proofing" hooks nobody asked for.

### Clean Code
- **CQS (Command–Query Separation):** a function either returns a value **or**
  mutates state, not both. Query functions (`monthLength`, `dateKey`, `bounds`)
  must be pure and side-effect-free. In Compose, keep derivation pure and route
  state changes through `on*` callbacks / state hoisting.
- **Function length:** aim for functions that fit on one screen (~30 lines). A
  long `@Composable` is fine only when it's a flat layout tree; extract logic and
  sub-sections into named helpers.
- **Class/file length:** if a file mixes several responsibilities or scrolls
  forever, split it. Model classes stay small and data-focused.
- **Naming:** intention-revealing, calendar-neutral where the concept is neutral.
  Booleans read as predicates (`isLeapYear`, `enabled`). No abbreviations that
  aren't domain-standard.
- **Nesting:** prefer early returns / guard clauses over deep `if` pyramids.
- **Immutability:** prefer `val`, `data class`, and pure transformations. Model
  types are immutable; produce new instances via `copy`.
- **Nullability:** encode "absent" with types (nullable, sealed) not sentinels.
  Don't swallow nulls; `require`/`check` invalid arguments with clear messages
  (the codebase already does, e.g. `require(month in 1..12)`).
- **Error handling:** fail fast on programmer errors (`require`/`check`); never
  catch-and-ignore.

### Comments
- Comments explain **why**, not **what** — the code already says what.
- Public API gets **KDoc** (this is a library; consumers read it). Keep the
  existing KDoc style with usage examples.
- Delete commented-out code and stale TODOs; use the roadmap for future work.
- No redundant comments restating the line below them.

---

## 4. Maintainable · Testable · Scalable (the prime directive)

Every change should leave the library **more** maintainable, testable, and
scalable — never less. When two designs are equally correct, pick the one that:

- **Maintainable:** localizes future change (add a calendar/theme/feature by
  adding a type, not editing ten call sites); has small, well-named units;
  minimizes public surface.
- **Testable:** puts logic in pure `shamsi-core` functions that need no Compose
  runtime or Android; hoists state so UI is a thin, verifiable shell; avoids
  hidden singletons/`now()` calls buried in UI (inject the clock/zone).
- **Scalable:** works as data grows (year ranges, event lists), as calendars are
  added, and across configurations (RTL/LTR, locales, themes) without rewrites.

If a task is hard to test, treat that as a design smell and refactor the seam
before adding the feature.

---

## 5. Android / Compose specifics

- **State hoisting:** composables are stateless where practical; state lives in
  the caller or a holder, passed down with `on*` callbacks up. Follow the
  existing `year/month/day + onYear/onMonth/onDay` pattern.
- **Recomposition hygiene:** don't do heavy work in composition; remember derived
  values (`remember`, `derivedStateOf`). Resolve `Now` once at open time — the
  code already does this; preserve it.
- **No hardcoded styling** in new UI once the theming API (Phase 3) lands — read
  colors/typography from the theming objects, not raw `MaterialTheme.*`.
- **RTL/LTR:** Persian is RTL, Gregorian typically LTR. Verify layout, wheel
  order, and arrow directions in both.
- **Localization:** user-visible strings live in `strings.xml`, not literals in
  Kotlin. Numerals go through the number formatter (Persian vs Latin digits).
- **Accessibility:** interactive elements need `contentDescription`/`semantics`.
  Don't regress this (roadmap Phase 6).
- **Previews:** add `@Preview` composables for new UI to aid review.

---

## 6. Sample app rule

Every feature-adding task must add or update a demo in `sample/` so the feature
is visible and tappable — this is a required part of the task, not follow-up
work. See `docs/ROADMAP.md` for the per-phase sample requirements and the planned
bottom-navigation redesign (Phase 8).

---

## 7. Testing expectations

- Pure logic (`shamsi-core`) gets JUnit tests: conversions, leap years, month
  lengths, bounds/clamping, formatting — cover edge cases (Esfand 29/30, year
  boundaries, min==max, Gregorian Feb 29).
- Picker state machines (selection, auto-swap, range clamping) get logic tests —
  mirror the existing `ShamsiRangePickerLogicTest` style.
- New UI behavior gets Compose UI / semantics tests where feasible.
- A bug fix ships with a regression test that fails before and passes after.

---

## 8. Workflow checklist (before you call a task done)

1. `./gradlew test` passes.
2. `./gradlew spotlessApply` run; no lint diffs left.
3. Public API changes are intentional, documented (KDoc), and backward-compatible
   (or the break is flagged for a major bump).
4. `sample/` updated to showcase the change.
5. `README.md` updated if public API/behavior changed.
6. The roadmap checkbox is ticked with a dated note, per its contract.
7. Commit only when the user asks; branch off `master` first if needed.
