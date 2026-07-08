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
 *
 * [compactCalendar] shrinks the Calendar-style grid (smaller day cells, tighter
 * spacing) for layouts where the picker shares space with other controls.
 *
 * [compactWheel] shows only the selected row of the Wheel-style picker, with no
 * dimmed rows above/below, for the same space-constrained layouts.
 *
 * [events] marks days with a holiday/event indicator in the Calendar-style grid.
 * Each event's date is matched by calendar day (year/month/day), ignoring time.
 */
public data class ShamsiDatePickerConfig(
    val initialDate: ShamsiDateLimit = ShamsiDate.Now,
    val minDate: ShamsiDateLimit? = null,
    val maxDate: ShamsiDateLimit? = null,
    val style: ShamsiDatePickerStyle = ShamsiDatePickerStyle.Wheel,
    val calendarType: CalendarType = CalendarType.Shamsi,
    val firstDayOfWeek: DayOfWeek? = null,
    val compactCalendar: Boolean = false,
    val compactWheel: Boolean = false,
    val events: List<CalendarEvent> = emptyList(),
)
