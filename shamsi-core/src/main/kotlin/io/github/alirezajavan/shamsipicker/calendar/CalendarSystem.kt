package io.github.alirezajavan.shamsipicker.calendar

import io.github.alirezajavan.shamsipicker.model.ShamsiDate
import java.time.DayOfWeek
import java.time.ZoneId

/**
 * Interface representing a calendar system (e.g., Shamsi, Gregorian).
 * Provides operations needed by the UI in a calendar-agnostic way.
 */
public interface CalendarSystem {
    /** The supported range of years for this calendar system. */
    public val yearRange: IntRange

    /** The default first day of the week for this calendar system. */
    public val defaultFirstDayOfWeek: DayOfWeek

    /**
     * Short names of the days of the week, starting from [firstDayOfWeek].
     */
    public fun weekdayNames(firstDayOfWeek: DayOfWeek = defaultFirstDayOfWeek): List<String>

    /** Names of the months for the given [year]. */
    public fun monthNames(year: Int): List<String>

    /** Number of days in the given [month] of [year]. */
    public fun monthLength(
        year: Int,
        month: Int,
    ): Int

    /**
     * Index of the first day of the month in the list returned by [weekdayNames]
     * for the given [firstDayOfWeek].
     */
    public fun firstWeekdayOfMonth(
        year: Int,
        month: Int,
        firstDayOfWeek: DayOfWeek = defaultFirstDayOfWeek,
    ): Int

    /** Name of the weekday for the given date. */
    public fun weekdayName(
        year: Int,
        month: Int,
        day: Int,
    ): String

    /** Whether [year] is a leap year in this calendar system. */
    public fun isLeapYear(year: Int): Boolean

    /** Converts the given date to epoch days (days since 1970-01-01). */
    public fun toEpochDay(
        year: Int,
        month: Int,
        day: Int,
    ): Long

    /** Converts epoch days to (year, month, day) in this calendar system. */
    public fun fromEpochDay(epochDay: Long): Triple<Int, Int, Int>

    /** The current date in this calendar system for the given [zone]. */
    public fun today(zone: ZoneId = ZoneId.systemDefault()): Triple<Int, Int, Int>

    /** Allowed years within [yearRange] given optional date bounds, as wheel indices. */
    public fun yearEnabledRange(
        minDate: ShamsiDate?,
        maxDate: ShamsiDate?,
    ): IntRange

    /** Allowed month numbers (1..12) for [year] given optional date bounds. */
    public fun monthBounds(
        year: Int,
        minDate: ShamsiDate?,
        maxDate: ShamsiDate?,
    ): IntRange

    /** Allowed day numbers (1..[maxDay]) for [year]/[month] given optional date bounds. */
    public fun dayBounds(
        year: Int,
        month: Int,
        maxDay: Int,
        minDate: ShamsiDate?,
        maxDate: ShamsiDate?,
    ): IntRange
}
