package io.github.alirezajavan.shamsipicker.model

import io.github.alirezajavan.shamsipicker.calendar.CalendarType

/**
 * Configuration for [io.github.alirezajavan.shamsipicker.ui.ShamsiTimePickerDialog].
 *
 * All time parameters accept any [ShamsiTimeLimit]:
 * - A [ShamsiTime] — a fixed time, e.g. `ShamsiTime(8, 30)`
 * - [ShamsiTime.Now] or [ShamsiTimeLimit.Now] — evaluates to the current time at open time
 * - `LocalTime.of(...).asLimit()` — a fixed Gregorian time
 * - `LocalTime.now().asLimit()` — the current system time
 *
 * [compactWheel] shows only the selected row of each wheel, with no dimmed rows
 * above/below, for space-constrained layouts.
 */
public data class ShamsiTimePickerConfig(
    val initialTime: ShamsiTimeLimit = ShamsiTime.Now,
    val minTime: ShamsiTimeLimit? = null,
    val maxTime: ShamsiTimeLimit? = null,
    val calendarType: CalendarType = CalendarType.Shamsi,
    val compactWheel: Boolean = false,
)
