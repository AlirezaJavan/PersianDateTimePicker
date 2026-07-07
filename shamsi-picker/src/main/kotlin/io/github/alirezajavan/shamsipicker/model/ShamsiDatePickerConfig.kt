package io.github.alirezajavan.shamsipicker.model

import io.github.alirezajavan.shamsipicker.calendar.CalendarType
import java.time.DayOfWeek

/**
 * Configuration for [io.github.alirezajavan.shamsipicker.ui.ShamsiDatePickerDialog].
 *
 * All date parameters accept any [ShamsiDateLimit]:
 * - A [ShamsiDate] — a fixed Shamsi date, e.g. `ShamsiDate(1403, 1, 1)`
 * - [ShamsiDate.Now] or [ShamsiDateLimit.Now] — evaluates to the current date at open time
 * - `LocalDate.of(...).asLimit()` — a fixed Gregorian date converted to Shamsi
 * - `LocalDate.now().asLimit()` — the current Gregorian date converted to Shamsi
 */
public data class ShamsiDatePickerConfig(
    val initialDate: ShamsiDateLimit = ShamsiDate.Now,
    val minDate: ShamsiDateLimit? = null,
    val maxDate: ShamsiDateLimit? = null,
    val style: ShamsiDatePickerStyle = ShamsiDatePickerStyle.Wheel,
    val calendarType: CalendarType = CalendarType.Shamsi,
    val firstDayOfWeek: DayOfWeek? = null,
)
