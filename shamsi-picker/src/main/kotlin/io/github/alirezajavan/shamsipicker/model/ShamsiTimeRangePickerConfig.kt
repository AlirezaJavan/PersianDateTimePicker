package io.github.alirezajavan.shamsipicker.model

import io.github.alirezajavan.shamsipicker.calendar.CalendarType

/**
 * Configuration for [io.github.alirezajavan.shamsipicker.ui.ShamsiTimeRangePickerDialog].
 *
 * All time parameters accept any [ShamsiTimeLimit]:
 * - `ShamsiTime(8, 30)` — a fixed time
 * - `ShamsiTime.Now` / `ShamsiTimeLimit.Now` — current time, resolved at open time
 * - `LocalTime.of(9, 0).asLimit()` — a fixed Gregorian time
 * - `LocalTime.now().asLimit()` — current system time, resolved at open time
 */
public data class ShamsiTimeRangePickerConfig(
    val initialFrom: ShamsiTimeLimit = ShamsiTime.Now,
    val initialTo: ShamsiTimeLimit = ShamsiTime.Now,
    val minTime: ShamsiTimeLimit? = null,
    val maxTime: ShamsiTimeLimit? = null,
    val calendarType: CalendarType = CalendarType.Shamsi,
)
