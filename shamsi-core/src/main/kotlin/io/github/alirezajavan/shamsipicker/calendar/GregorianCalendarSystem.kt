package io.github.alirezajavan.shamsipicker.calendar

import io.github.alirezajavan.shamsipicker.model.ShamsiDate
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.Year
import java.time.YearMonth
import java.time.ZoneId

/**
 * Implementation of [CalendarSystem] for the Gregorian calendar.
 * Backed by [java.time.LocalDate].
 */
public object GregorianCalendarSystem : CalendarSystem {
    override val yearRange: IntRange = 1900..2100
    override val defaultFirstDayOfWeek: DayOfWeek = DayOfWeek.SUNDAY

    private val ENGLISH_WEEKDAY_NAMES =
        listOf(
            "Mon",
            "Tue",
            "Wed",
            "Thu",
            "Fri",
            "Sat",
            "Sun",
        )

    override fun weekdayNames(firstDayOfWeek: DayOfWeek): List<String> {
        // Mon=0, Tue=1, ... Sun=6 in ENGLISH_WEEKDAY_NAMES
        val startIndex = firstDayOfWeek.value - 1
        return ENGLISH_WEEKDAY_NAMES.drop(startIndex) + ENGLISH_WEEKDAY_NAMES.take(startIndex)
    }

    override fun monthNames(year: Int): List<String> =
        listOf(
            "January",
            "February",
            "March",
            "April",
            "May",
            "June",
            "July",
            "August",
            "September",
            "October",
            "November",
            "December",
        )

    override fun monthLength(
        year: Int,
        month: Int,
    ): Int = YearMonth.of(year, month).lengthOfMonth()

    override fun firstWeekdayOfMonth(
        year: Int,
        month: Int,
        firstDayOfWeek: DayOfWeek,
    ): Int {
        val date = LocalDate.of(year, month, 1)
        val isoDow = date.dayOfWeek.value // 1..7 Mon..Sun
        return (isoDow - firstDayOfWeek.value + 7) % 7
    }

    override fun weekdayName(
        year: Int,
        month: Int,
        day: Int,
    ): String {
        val date = LocalDate.of(year, month, day)
        // Return short name from ENGLISH_WEEKDAY_NAMES
        // isoDow: 1=Mon, ..., 7=Sun
        return ENGLISH_WEEKDAY_NAMES[date.dayOfWeek.value - 1]
    }

    override fun isLeapYear(year: Int): Boolean = Year.isLeap(year.toLong())

    override fun toEpochDay(
        year: Int,
        month: Int,
        day: Int,
    ): Long = LocalDate.of(year, month, day).toEpochDay()

    override fun fromEpochDay(epochDay: Long): Triple<Int, Int, Int> {
        val date = LocalDate.ofEpochDay(epochDay)
        return Triple(date.year, date.monthValue, date.dayOfMonth)
    }

    override fun today(zone: ZoneId): Triple<Int, Int, Int> {
        val now = LocalDate.now(zone)
        return Triple(now.year, now.monthValue, now.dayOfMonth)
    }

    override fun yearEnabledRange(
        minDate: ShamsiDate?,
        maxDate: ShamsiDate?,
    ): IntRange {
        val lo = maxOf(yearRange.first, minDate?.year ?: yearRange.first)
        val hi = minOf(yearRange.last, maxDate?.year ?: yearRange.last)
        return (lo - yearRange.first)..(hi - yearRange.first)
    }

    override fun monthBounds(
        year: Int,
        minDate: ShamsiDate?,
        maxDate: ShamsiDate?,
    ): IntRange {
        val lo = if (minDate != null && year == minDate.year) minDate.month else 1
        val hi = if (maxDate != null && year == maxDate.year) maxDate.month else 12
        return lo..hi
    }

    override fun dayBounds(
        year: Int,
        month: Int,
        maxDay: Int,
        minDate: ShamsiDate?,
        maxDate: ShamsiDate?,
    ): IntRange {
        val lo = if (minDate != null && year == minDate.year && month == minDate.month) minDate.day else 1
        val hi =
            if (maxDate != null && year == maxDate.year && month == maxDate.month) {
                minOf(maxDate.day, maxDay)
            } else {
                maxDay
            }
        return lo..hi
    }
}
