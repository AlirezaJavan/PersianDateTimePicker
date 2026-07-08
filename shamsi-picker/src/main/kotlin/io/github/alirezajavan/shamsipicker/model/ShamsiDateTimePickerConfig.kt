package io.github.alirezajavan.shamsipicker.model

import io.github.alirezajavan.shamsipicker.calendar.CalendarType
import java.time.DayOfWeek

/**
 * Configuration for [io.github.alirezajavan.shamsipicker.ui.ShamsiDateTimePickerDialog].
 *
 * All date and time parameters accept any [ShamsiDateLimit]:
 * - A [ShamsiDate] — a fixed Shamsi date/time, e.g. `ShamsiDate(1403, 1, 1, 10, 30)`
 * - [ShamsiDate.Now] or [ShamsiDateLimit.Now] — evaluates to current date/time at open time
 * - `LocalDateTime.of(...).asLimit()` — a fixed Gregorian date/time converted to Shamsi
 * - `LocalDateTime.now().asLimit()` — the current Gregorian date/time converted to Shamsi
 *
 * [compactCalendar] shrinks the Calendar-style grid (smaller day cells, tighter
 * spacing) so it fits alongside the time wheel. Defaults to `true` since date and
 * time are always shown together in this dialog.
 *
 * [compactWheel] shows only the selected row of the date/time wheels, with no
 * dimmed rows above/below, for even tighter layouts. Defaults to `false`,
 * preserving the existing 3-row wheel look.
 */
public data class ShamsiDateTimePickerConfig(
    val initialDateTime: ShamsiDateLimit = ShamsiDate.Now,
    val minDateTime: ShamsiDateLimit? = null,
    val maxDateTime: ShamsiDateLimit? = null,
    val style: ShamsiDatePickerStyle = ShamsiDatePickerStyle.Wheel,
    val calendarType: CalendarType = CalendarType.Shamsi,
    val firstDayOfWeek: DayOfWeek? = null,
    val compactCalendar: Boolean = true,
    val compactWheel: Boolean = false,
)
