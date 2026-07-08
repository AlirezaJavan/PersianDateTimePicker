package io.github.alirezajavan.shamsipicker.model

import kotlinx.serialization.Serializable

/**
 * A single marker for a calendar day: a holiday, anniversary, or app-defined event.
 *
 * [date]'s year/month/day are matched against picker grid cells by calendar day —
 * [ShamsiDate.hour]/[ShamsiDate.minute] are ignored. The fields follow whichever
 * [io.github.alirezajavan.shamsipicker.calendar.CalendarType] the picker is
 * configured with, matching the calendar-agnostic convention already used by
 * [ShamsiDate] elsewhere in the picker configs.
 *
 * [type] controls how the day is presented — see [CalendarEventType]. Fixed
 * weekly days off (e.g. Thursday/Friday for Shamsi, Saturday/Sunday for
 * Gregorian) don't need an event; the picker marks those automatically via
 * [io.github.alirezajavan.shamsipicker.calendar.CalendarSystem.isWeekend].
 *
 * [colorArgb] optionally overrides the marker/text color for this specific
 * event (packed ARGB, e.g. `0xFFE53935.toInt()`); when `null` the picker's
 * default theme color for [type] is used.
 */
@Serializable
public data class CalendarEvent(
    public val date: ShamsiDate,
    public val label: String,
    public val type: CalendarEventType = CalendarEventType.Event,
    public val colorArgb: Int? = null,
) {
    /** True if [other] falls on the same calendar day as [date] (hour/minute ignored). */
    public fun isOnSameDayAs(other: ShamsiDate): Boolean = date.year == other.year && date.month == other.month && date.day == other.day
}
