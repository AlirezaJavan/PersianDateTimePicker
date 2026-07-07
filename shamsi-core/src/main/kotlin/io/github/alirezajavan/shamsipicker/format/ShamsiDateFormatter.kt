package io.github.alirezajavan.shamsipicker.format

import io.github.alirezajavan.shamsipicker.calendar.CalendarType
import io.github.alirezajavan.shamsipicker.model.MonthKey
import io.github.alirezajavan.shamsipicker.model.ShamsiDate

/** Display formatting for dates. Pure functions, unit-testable. */
public object DateFormatter {
    /** e.g. «چهارشنبه ۱ فروردین ۱۴۰۳» (Shamsi) or «Wednesday, March 20, 2024» (Gregorian). */
    public fun long(
        date: ShamsiDate,
        type: CalendarType = CalendarType.Shamsi,
    ): String {
        val system = type.system
        val numberFormatter = NumberFormatter.get(type)
        val weekday = system.weekdayName(date.year, date.month, date.day)
        val day = numberFormatter.format(date.day.toLong())
        val month = system.monthNames(date.year)[date.month - 1]
        val year = numberFormatter.format(date.year.toLong())
        return if (type == CalendarType.Shamsi) {
            "$weekday $day $month $year"
        } else {
            "$weekday, $month $day, $year"
        }
    }

    /** e.g. «۱۴۰۳/۰۱/۰۱» (Shamsi) or «2024/03/20» (Gregorian). */
    public fun short(
        date: ShamsiDate,
        type: CalendarType = CalendarType.Shamsi,
    ): String {
        val numberFormatter = NumberFormatter.get(type)
        val year = numberFormatter.format(date.year.toLong(), minDigits = 4)
        val month = numberFormatter.format(date.month.toLong(), minDigits = 2)
        val day = numberFormatter.format(date.day.toLong(), minDigits = 2)
        return "$year/$month/$day"
    }

    /** e.g. «۱۳:۴۵». */
    public fun time(
        date: ShamsiDate,
        type: CalendarType = CalendarType.Shamsi,
    ): String {
        val numberFormatter = NumberFormatter.get(type)
        val hour = numberFormatter.format(date.hour.toLong(), minDigits = 2)
        val minute = numberFormatter.format(date.minute.toLong(), minDigits = 2)
        return "$hour:$minute"
    }

    /** e.g. «فروردین ۱۴۰۳» (Shamsi) or «March 2024» (Gregorian). */
    public fun monthTitle(
        monthKey: MonthKey,
        type: CalendarType = CalendarType.Shamsi,
    ): String {
        val system = type.system
        val numberFormatter = NumberFormatter.get(type)
        val month = system.monthNames(monthKey.year)[monthKey.month - 1]
        val year = numberFormatter.format(monthKey.year.toLong())
        return if (type == CalendarType.Shamsi) "$month $year" else "$month $year"
    }
}

/** Persian display formatting for Shamsi dates. Pure functions, unit-testable. */
@Deprecated("Use DateFormatter instead", ReplaceWith("DateFormatter"))
public object ShamsiDateFormatter {
    /** e.g. «چهارشنبه ۱ فروردین ۱۴۰۳». */
    public fun long(date: ShamsiDate): String = DateFormatter.long(date, CalendarType.Shamsi)

    /** e.g. «۱۴۰۳/۰۱/۰۱». */
    public fun short(date: ShamsiDate): String = DateFormatter.short(date, CalendarType.Shamsi)

    /** e.g. «۱۳:۴۵». */
    public fun time(date: ShamsiDate): String = DateFormatter.time(date, CalendarType.Shamsi)

    /** e.g. «فروردین ۱۴۰۳». */
    public fun monthTitle(monthKey: MonthKey): String = DateFormatter.monthTitle(monthKey, CalendarType.Shamsi)
}
