package io.github.alirezajavan.shamsipicker.calendar

import io.github.alirezajavan.shamsipicker.model.ShamsiDate
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.ZoneId

/**
 * Implementation of [CalendarSystem] for the Shamsi (Jalali / Persian) calendar.
 * Delegates to [ShamsiCalendar] for the core algorithm.
 */
public object ShamsiCalendarSystem : CalendarSystem {
    override val yearRange: IntRange = ShamsiCalendar.YEAR_RANGE
    override val defaultFirstDayOfWeek: DayOfWeek = DayOfWeek.SATURDAY
    override val weekendDays: Set<DayOfWeek> = setOf(DayOfWeek.THURSDAY, DayOfWeek.FRIDAY)

    override fun weekdayNames(firstDayOfWeek: DayOfWeek): List<String> {
        val names = ShamsiCalendar.WEEKDAY_NAMES
        // Sat=0, Sun=1, ... Fri=6 in ShamsiCalendar.WEEKDAY_NAMES
        // Map DayOfWeek (Mon=1..Sun=7) to index in WEEKDAY_NAMES (Sat=0..Fri=6)
        val startIndex = (firstDayOfWeek.value + 1) % 7
        return names.drop(startIndex) + names.take(startIndex)
    }

    override fun monthNames(year: Int): List<String> = ShamsiCalendar.MONTH_NAMES

    override fun monthLength(
        year: Int,
        month: Int,
    ): Int = ShamsiCalendar.monthLength(year, month)

    override fun firstWeekdayOfMonth(
        year: Int,
        month: Int,
        firstDayOfWeek: DayOfWeek,
    ): Int {
        val date = ShamsiDate(year, month, 1)
        val gregorian = ShamsiCalendar.toGregorian(date)
        val isoDow = gregorian.dayOfWeek.value // 1..7 Mon..Sun
        return (isoDow - firstDayOfWeek.value + 7) % 7
    }

    override fun weekdayName(
        year: Int,
        month: Int,
        day: Int,
    ): String = ShamsiCalendar.weekdayName(ShamsiDate(year, month, day))

    override fun isLeapYear(year: Int): Boolean = ShamsiCalendar.isLeapYear(year)

    override fun toEpochDay(
        year: Int,
        month: Int,
        day: Int,
    ): Long = ShamsiCalendar.toGregorian(ShamsiDate(year, month, day)).toEpochDay()

    override fun fromEpochDay(epochDay: Long): Triple<Int, Int, Int> {
        val date = LocalDate.ofEpochDay(epochDay)
        val shamsi = ShamsiCalendar.fromGregorian(date)
        return Triple(shamsi.year, shamsi.month, shamsi.day)
    }

    override fun today(zone: ZoneId): Triple<Int, Int, Int> {
        val now = ShamsiCalendar.now(zone)
        return Triple(now.year, now.month, now.day)
    }

    override fun yearEnabledRange(
        minDate: ShamsiDate?,
        maxDate: ShamsiDate?,
    ): IntRange = ShamsiCalendar.yearEnabledRange(yearRange, minDate, maxDate)

    override fun monthBounds(
        year: Int,
        minDate: ShamsiDate?,
        maxDate: ShamsiDate?,
    ): IntRange = ShamsiCalendar.monthBounds(year, minDate, maxDate)

    override fun dayBounds(
        year: Int,
        month: Int,
        maxDay: Int,
        minDate: ShamsiDate?,
        maxDate: ShamsiDate?,
    ): IntRange = ShamsiCalendar.dayBounds(year, month, maxDay, minDate, maxDate)
}
