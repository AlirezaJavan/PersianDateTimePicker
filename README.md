# ShamsiPicker

[![Android CI](https://github.com/AlirezaJavan/PersianDateTimePicker/actions/workflows/android.yml/badge.svg)](https://github.com/AlirezaJavan/PersianDateTimePicker/actions/workflows/android.yml)
[![Maven Central (Picker)](https://img.shields.io/maven-central/v/io.github.alirezajavan/shamsi-picker?label=shamsi-picker)](https://central.sonatype.com/artifact/io.github.alirezajavan/shamsi-picker)
[![Maven Central (Core)](https://img.shields.io/maven-central/v/io.github.alirezajavan/shamsi-core?label=shamsi-core)](https://central.sonatype.com/artifact/io.github.alirezajavan/shamsi-core)
[![API](https://img.shields.io/badge/API-26%2B-brightgreen.svg)](https://android-arsenal.com/api?level=26)
[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](LICENSE)

A modern, highly customizable Shamsi (Persian/Jalali) date and time picker library for Jetpack Compose, built on a pure-Kotlin core.

## Project Structure

This project is split into two modules:

- **`shamsi-core`**: A pure-Kotlin/JVM library containing all date logic, calendar conversions, and formatting. It has **zero Android dependencies** and can be used in JVM, KMP, or non-UI layers.
- **`shamsi-picker`**: The Android library providing **Jetpack Compose** dialogs and UI components. It depends on `shamsi-core`.

## Features

- **ShamsiDatePickerDialog**: Supports both Wheel (iOS-style) and Calendar (grid) styles.
- **ShamsiTimePickerDialog**: iOS-style infinite spinning wheel for hours and minutes.
- **ShamsiDateRangePickerDialog**: Pick a from→to date range in Wheel or Calendar style. Calendar mode renders a Material-style color strip across the selected range; Wheel mode enforces that "to" is always ≥ "from".
- **ShamsiTimeRangePickerDialog**: Pick a from→to time range with two stacked wheel rows. The "to" wheel minimum is always clamped to the current "from" time.
- **Limit Aware**: Set dynamic boundaries (e.g. `ShamsiDate.Now`) or fixed limits (Gregorian or Shamsi).
- **Leap Year Aware**: Automatically handles 29/30 day Esfand.
- **Persian Formatting**: Built-in formatters for long/short date and time strings.
- **Modern API**: Clean configuration objects and `java.time` interoperability.

## Screenshots

|                        Calendar Style                        |                        Wheel Style                        |                        Time Picker                        |
|:------------------------------------------------------------:|:---------------------------------------------------------:|:---------------------------------------------------------:|
|    <img src="screenshots/datecalendar.png" width="100%"/>    |    <img src="screenshots/datewheel.png" width="100%"/>    |    <img src="screenshots/timewheel.png" width="100%"/>    |
|                            :---:                             |                           :---:                           |                           :---:                           |
| <img src="screenshots/dateperiodcalendar.png" width="100%"/> | <img src="screenshots/dateperiodwheel.png" width="100%"/> | <img src="screenshots/timeperiodwheel.png" width="100%"/> |

## Installation

Add the following to your `build.gradle.kts`:

### Android (Compose)
```kotlin
dependencies {
    // Includes shamsi-core automatically
    implementation("io.github.alirezajavan:shamsi-picker:1.3.0")
}
```

### JVM / Non-UI
```kotlin
dependencies {
    implementation("io.github.alirezajavan:shamsi-core:1.3.0")
}
```

## Usage (Compose)

All pickers follow the same pattern: pass callbacks and a config object.

### Date Picker

```kotlin
var selectedDate by remember { mutableStateOf(ShamsiDate.Now) }
var showDatePicker by remember { mutableStateOf(false) }

if (showDatePicker) {
    ShamsiDatePickerDialog(
        onConfirm = { date ->
            selectedDate = date
            showDatePicker = false
        },
        onDismiss = { showDatePicker = false },
        config = ShamsiDatePickerConfig(
            initialDate = selectedDate,
            style = ShamsiDatePickerStyle.Calendar, // or .Wheel
        ),
    )
}
```

### Time Picker

```kotlin
var showTimePicker by remember { mutableStateOf(false) }

if (showTimePicker) {
    ShamsiTimePickerDialog(
        onConfirm = { time ->
            selectedDate = selectedDate.copy(hour = time.hour, minute = time.minute)
            showTimePicker = false
        },
        onDismiss = { showTimePicker = false },
        config = ShamsiTimePickerConfig(
            initialTime = selectedDate.toTime(),
        ),
    )
}
```

### Date Range Picker

```kotlin
var selectedDateRange by remember { mutableStateOf<ShamsiDateRange?>(null) }
var showDateRangePicker by remember { mutableStateOf(false) }

if (showDateRangePicker) {
    ShamsiDateRangePickerDialog(
        onConfirm = { range ->
            selectedDateRange = range
            showDateRangePicker = false
        },
        onDismiss = { showDateRangePicker = false },
        config = ShamsiDateRangePickerConfig(
            style = ShamsiDatePickerStyle.Calendar, // or .Wheel
        ),
    )
}
```

### Time Range Picker

```kotlin
var selectedTimeRange by remember { mutableStateOf<ShamsiTimeRange?>(null) }
var showTimeRangePicker by remember { mutableStateOf(false) }

if (showTimeRangePicker) {
    ShamsiTimeRangePickerDialog(
        onConfirm = { range ->
            selectedTimeRange = range
            showTimeRangePicker = false
        },
        onDismiss = { showTimeRangePicker = false },
        config = ShamsiTimeRangePickerConfig(
            initialFrom = ShamsiTime(9, 0),
            initialTo = ShamsiTime(17, 0),
        ),
    )
}
```

---

## Configuration

### How date and time values work

Every date or time field in a config object accepts a **limit type** — a sealed interface that can hold one of three kinds of values:

| What you want | Date (`ShamsiDateLimit`) | Time (`ShamsiTimeLimit`) |
|---|---|---|
| Fixed Shamsi value | `ShamsiDate(1403, 6, 15)` | `ShamsiTime(8, 30)` |
| Current date/time (dynamic) | `ShamsiDate.Now` or `ShamsiDateLimit.Now` | `ShamsiTime.Now` or `ShamsiTimeLimit.Now` |
| Fixed Gregorian value | `LocalDate.of(2024, 9, 5).asLimit()` | `LocalTime.of(8, 30).asLimit()` |
| Current Gregorian (dynamic) | `LocalDate.now().asLimit()` | `LocalTime.now().asLimit()` |

Dynamic values (`Now`) are resolved **once when the dialog opens**, not on every recomposition.

---

### `ShamsiDatePickerConfig`

```kotlin
ShamsiDatePickerConfig(
    initialDate: ShamsiDateLimit = ShamsiDate.Now,
    minDate: ShamsiDateLimit? = null,    // no lower bound if omitted
    maxDate: ShamsiDateLimit? = null,    // no upper bound if omitted
    style: ShamsiDatePickerStyle = ShamsiDatePickerStyle.Wheel,
)
```

#### Examples

```kotlin
// Open on today, no bounds
ShamsiDatePickerConfig()

// Fixed Shamsi initial date
ShamsiDatePickerConfig(
    initialDate = ShamsiDate(1403, 1, 1),
    style = ShamsiDatePickerStyle.Calendar,
)

// Gregorian initial date with a "today onwards" lower bound
ShamsiDatePickerConfig(
    initialDate = LocalDate.of(2025, 3, 21).asLimit(),
    minDate = ShamsiDate.Now,
)
```

---

### `ShamsiTimePickerConfig`

```kotlin
ShamsiTimePickerConfig(
    initialTime: ShamsiTimeLimit = ShamsiTime.Now,
    minTime: ShamsiTimeLimit? = null,   // no lower bound if omitted
    maxTime: ShamsiTimeLimit? = null,   // no upper bound if omitted
)
```

#### Examples

```kotlin
// Business hours: 08:30 → 17:00
ShamsiTimePickerConfig(
    initialTime = ShamsiTime(9, 0),
    minTime = ShamsiTime(8, 30),
    maxTime = ShamsiTime(17, 0),
)

// "Until now" — user can only pick a past time
ShamsiTimePickerConfig(
    maxTime = ShamsiTimeLimit.Now,
)
```

---

## Core API

### Formatting

```kotlin
import io.github.alirezajavan.shamsipicker.format.ShamsiDateFormatter

val longDate = ShamsiDateFormatter.long(selectedDate)   // چهارشنبه ۱ فروردین ۱۴۰۳
val shortDate = ShamsiDateFormatter.short(selectedDate) // ۱۴۰۳/۰۱/۰۱
val time = ShamsiDateFormatter.time(selectedDate)       // ۱۳:۴۵
```

### Date Conversion

Easily convert between Gregorian `java.time.LocalDate` and `ShamsiDate`.

```kotlin
import io.github.alirezajavan.shamsipicker.calendar.ShamsiCalendar

// Gregorian to Shamsi
val shamsi = ShamsiCalendar.fromGregorian(LocalDate.now())

// Shamsi to Gregorian
val gregorian = ShamsiCalendar.toGregorian(shamsi)
```

## License

Apache License 2.0
