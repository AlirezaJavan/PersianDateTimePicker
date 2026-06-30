# ShamsiPicker

[![Android CI](https://github.com/AlirezaJavan/PersianDateTimePicker/actions/workflows/android.yml/badge.svg)](https://github.com/AlirezaJavan/PersianDateTimePicker/actions/workflows/android.yml)
[![Maven Central](https://img.shields.io/maven-central/v/io.github.alirezajavan/shamsi-picker)](https://central.sonatype.com/artifact/io.github.alirezajavan/shamsi-picker)
[![API](https://img.shields.io/badge/API-23%2B-brightgreen.svg)](https://android-arsenal.com/api?level=23)
[![License](https://img.shields.io/badge/License-MIT-blue.svg)](LICENSE)

A modern, highly customizable Shamsi (Persian/Jalali) date and time picker library for Jetpack Compose.

## Features

- **ShamsiDatePickerDialog**: Supports both Wheel (iOS-style) and Calendar (grid) styles.
- **ShamsiTimePickerDialog**: iOS-style infinite spinning wheel for hours and minutes.
- **Limit Aware**: Set dynamic boundaries (e.g. `ShamsiDate.Now`) or fixed limits (Gregorian or Shamsi).
- **Leap Year Aware**: Automatically handles 29/30 day Esfand.
- **Persian Formatting**: Built-in formatters for long/short date and time strings.
- **Modern API**: Clean configuration objects and `java.time` interoperability.

## Screenshots

|                       Calendar Style                        |                       Wheel Style                        |                       Time Picker                        |
|:-----------------------------------------------------------:|:--------------------------------------------------------:|:--------------------------------------------------------:|
|   <img src="screenshots/datecalendar.png" width="100%"/>    |   <img src="screenshots/datewheel.png" width="100%"/>    |   <img src="screenshots/timewheel.png" width="100%"/>    |
|                            :---:                            |                          :---:                           |                          :---:                           |
| <img src="screenshots/daterangecalendar.png" width="100%"/> | <img src="screenshots/daterangewheel.png" width="100%"/> | <img src="screenshots/timerangewheel.png" width="100%"/> |

## Installation

Add the following to your `build.gradle.kts`:

```kotlin
dependencies {
    implementation("io.github.alirezajavan:shamsi-picker:1.2.0")
}
```

## Usage

Both pickers follow the same pattern: pass callbacks and a config object.

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

// Fixed Shamsi bounds
ShamsiDatePickerConfig(
    minDate = ShamsiDate(1400, 1, 1),
    maxDate = ShamsiDate(1410, 12, 29),
)

// Mix Gregorian min with Shamsi max
ShamsiDatePickerConfig(
    minDate = LocalDate.now().asLimit(),
    maxDate = ShamsiDate(1410, 12, 29),
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
// Open on current time, no bounds
ShamsiTimePickerConfig()

// Fixed Shamsi initial time
ShamsiTimePickerConfig(
    initialTime = ShamsiTime(9, 0),
)

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

// Gregorian bounds
ShamsiTimePickerConfig(
    minTime = LocalTime.of(8, 0).asLimit(),
    maxTime = LocalTime.of(20, 0).asLimit(),
)
```

---

## Formatting

```kotlin
val longDate = ShamsiDateFormatter.long(selectedDate)   // چهارشنبه ۱ فروردین ۱۴۰۳
val shortDate = ShamsiDateFormatter.short(selectedDate) // ۱۴۰۳/۰۱/۰۱
val time = ShamsiDateFormatter.time(selectedDate)       // ۱۳:۴۵
```

## Date Conversion

Easily convert between Gregorian `java.time.LocalDate` and `ShamsiDate`.

```kotlin
// Gregorian to Shamsi
val shamsi = ShamsiCalendar.fromGregorian(LocalDate.now())

// Shamsi to Gregorian
val gregorian = ShamsiCalendar.toGregorian(shamsi)
```

## License

MIT License
