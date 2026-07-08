package io.github.alirezajavan.shamsipicker.ui.theme

/**
 * User-facing text for [io.github.alirezajavan.shamsipicker.ui.ShamsiDatePickerDialog].
 *
 * Build one with [ShamsiPickerDefaults.dateStrings] to override only the strings you need —
 * the rest fall back to the library's localized resources.
 */
public data class ShamsiDatePickerStrings(
    val title: String,
    val confirmText: String,
    val cancelText: String,
    val styleWheelLabel: String,
    val styleCalendarLabel: String,
    val prevMonthDescription: String,
    val nextMonthDescription: String,
    val prevYearDescription: String,
    val nextYearDescription: String,
    val weekendDescription: String = "Weekend",
)

/**
 * User-facing text for [io.github.alirezajavan.shamsipicker.ui.ShamsiDateRangePickerDialog].
 *
 * Build one with [ShamsiPickerDefaults.dateRangeStrings].
 */
public data class ShamsiDateRangePickerStrings(
    val title: String,
    val confirmText: String,
    val cancelText: String,
    val styleWheelLabel: String,
    val styleCalendarLabel: String,
    val prevMonthDescription: String,
    val nextMonthDescription: String,
    val prevYearDescription: String,
    val nextYearDescription: String,
    val selectToHint: String,
    val weekendDescription: String = "Weekend",
)

/**
 * User-facing text for [io.github.alirezajavan.shamsipicker.ui.ShamsiTimePickerDialog].
 *
 * Build one with [ShamsiPickerDefaults.timeStrings].
 */
public data class ShamsiTimePickerStrings(
    val title: String,
    val confirmText: String,
    val cancelText: String,
    val amLabel: String,
    val pmLabel: String,
)

/**
 * User-facing text for [io.github.alirezajavan.shamsipicker.ui.ShamsiTimeRangePickerDialog].
 *
 * Build one with [ShamsiPickerDefaults.timeRangeStrings].
 */
public data class ShamsiTimeRangePickerStrings(
    val title: String,
    val confirmText: String,
    val cancelText: String,
    val amLabel: String,
    val pmLabel: String,
)

/**
 * User-facing text for [io.github.alirezajavan.shamsipicker.ui.ShamsiDateTimePickerDialog].
 *
 * Build one with [ShamsiPickerDefaults.dateTimeStrings].
 */
public data class ShamsiDateTimePickerStrings(
    val title: String,
    val confirmText: String,
    val cancelText: String,
    val styleWheelLabel: String,
    val styleCalendarLabel: String,
    val prevMonthDescription: String,
    val nextMonthDescription: String,
    val prevYearDescription: String,
    val nextYearDescription: String,
    val amLabel: String,
    val pmLabel: String,
    val weekendDescription: String = "Weekend",
)
