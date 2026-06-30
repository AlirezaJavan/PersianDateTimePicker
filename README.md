# ShamsiPicker

A modern, highly customizable Shamsi (Persian/Jalali) date and time picker library for Jetpack Compose.

## Features

- **ShamsiDatePickerDialog**: Supports both Wheel (iOS-style) and Calendar (grid) styles.
- **ShamsiTimePickerDialog**: iOS-style infinite spinning wheel for hours and minutes.
- **Leap Year Aware**: Automatically handles 29/30 day Esfand.
- **Date Bounds**: Set `minDate` and `maxDate` to restrict selection.
- **Persian Formatting**: Built-in formatters for long/short date and time strings.
- **Clean Architecture**: Decoupled calendar logic, easily unit-testable.

## Installation

Add the following to your `build.gradle.kts`:

```kotlin
dependencies {
    implementation("io.github.alirezajavan:shamsi-picker:1.0.0")
}
```

## Usage

### Date Picker

```kotlin
var selectedDate by remember { mutableStateOf(ShamsiCalendar.now()) }
var showDatePicker by remember { mutableStateOf(false) }

if (showDatePicker) {
    ShamsiDatePickerDialog(
        initialDate = selectedDate,
        onConfirm = {
            selectedDate = it
            showDatePicker = false
        },
        onDismiss = { showDatePicker = false },
        style = ShamsiDatePickerStyle.Calendar // or .Wheel
    )
}
```

### Time Picker

```kotlin
var showTimePicker by remember { mutableStateOf(false) }

if (showTimePicker) {
    ShamsiTimePickerDialog(
        initialHour = selectedDate.hour,
        initialMinute = selectedDate.minute,
        onConfirm = { h, m ->
            selectedDate = selectedDate.copy(hour = h, minute = m)
            showTimePicker = false
        },
        onDismiss = { showTimePicker = false }
    )
}
```

## Formatting

```kotlin
val longDate = ShamsiDateFormatter.long(selectedDate) // چهارشنبه ۱ فروردین ۱۴۰۳
val shortDate = ShamsiDateFormatter.short(selectedDate) // ۱۴۰۳/۰۱/۰۱
val time = ShamsiDateFormatter.time(selectedDate) // ۱۳:۴۵
```

## License

MIT License
