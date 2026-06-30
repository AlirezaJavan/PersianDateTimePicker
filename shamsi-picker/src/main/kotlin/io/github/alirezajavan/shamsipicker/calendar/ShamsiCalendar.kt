package io.github.alirezajavan.shamsipicker.calendar

import io.github.alirezajavan.shamsipicker.model.ShamsiDate
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId

/**
 * Conversions and helpers for the Shamsi (Jalali / Persian) calendar.
 */
public object ShamsiCalendar {
    public val MONTH_NAMES: List<String> =
        listOf(
            "فروردین",
            "اردیبهشت",
            "خرداد",
            "تیر",
            "مرداد",
            "شهریور",
            "مهر",
            "آبان",
            "آذر",
            "دی",
            "بهمن",
            "اسفند",
        )

    /** Weekday names indexed Saturday..Friday (the Persian week starts on Saturday). */
    public val WEEKDAY_NAMES: List<String> =
        listOf(
            "شنبه",
            "یکشنبه",
            "دوشنبه",
            "سه‌شنبه",
            "چهارشنبه",
            "پنجشنبه",
            "جمعه",
        )

    public fun monthName(month: Int): String {
        require(month in 1..12) { "month must be in 1..12 but was $month" }
        return MONTH_NAMES[month - 1]
    }

    /** Whether [year] is a Shamsi leap year (the year containing a 30-day Esfand). */
    public fun isLeapYear(year: Int): Boolean = jalCal(year).leap == 0

    /** Number of days in the given Shamsi [month] of [year] (29..31). */
    public fun monthLength(
        year: Int,
        month: Int,
    ): Int {
        require(month in 1..12) { "month must be in 1..12 but was $month" }
        return when {
            month <= 6 -> 31
            month <= 11 -> 30
            isLeapYear(year) -> 30
            else -> 29
        }
    }

    /** Persian weekday name for the given Shamsi date. */
    public fun weekdayName(date: ShamsiDate): String {
        val gregorian = toGregorian(date)
        // java.time: MONDAY=1 .. SUNDAY=7. Persian week starts Saturday.
        val isoDow = gregorian.dayOfWeek.value // 1..7 Mon..Sun
        val index = (isoDow + 1) % 7 // Sat->0, Sun->1, ... Fri->6
        return WEEKDAY_NAMES[index]
    }

    /** Converts a Gregorian [LocalDate] to a [ShamsiDate] (time fields default to 0). */
    public fun fromGregorian(date: LocalDate): ShamsiDate {
        val jdn = gregorianToJdn(date.year, date.monthValue, date.dayOfMonth)
        val (jy, jm, jd) = jdnToJalali(jdn)
        return ShamsiDate(year = jy, month = jm, day = jd)
    }

    /** Converts a [ShamsiDate] to a Gregorian [LocalDate] (ignores the time fields). */
    public fun toGregorian(date: ShamsiDate): LocalDate {
        val jdn = jalaliToJdn(date.year, date.month, date.day)
        val (gy, gm, gd) = jdnToGregorian(jdn)
        return LocalDate.of(gy, gm, gd)
    }

    /** The current Shamsi date & time in the given [zone]. */
    public fun now(zone: ZoneId = ZoneId.systemDefault()): ShamsiDate {
        val nowDateTime = LocalDateTime.now(zone)
        return fromGregorian(nowDateTime.toLocalDate())
            .copy(hour = nowDateTime.hour, minute = nowDateTime.minute)
    }

    /** Epoch millis for the start of the given Shamsi date (00:00 plus its time fields) in [zone]. */
    public fun toEpochMillis(
        date: ShamsiDate,
        zone: ZoneId = ZoneId.systemDefault(),
    ): Long {
        val gregorian = toGregorian(date)
        val dateTime = gregorian.atTime(date.hour, date.minute)
        return dateTime.atZone(zone).toInstant().toEpochMilli()
    }

    /** Builds a [ShamsiDate] from epoch millis in [zone]. */
    public fun fromEpochMillis(
        epochMillis: Long,
        zone: ZoneId = ZoneId.systemDefault(),
    ): ShamsiDate {
        val dateTime = Instant.ofEpochMilli(epochMillis).atZone(zone)
        return fromGregorian(dateTime.toLocalDate())
            .copy(hour = dateTime.hour, minute = dateTime.minute)
    }

    // region jalaali integer algorithm

    private data class JalCal(
        val leap: Int,
        val gy: Int,
        val march: Int,
    )

    private val BREAKS =
        intArrayOf(
            -61,
            9,
            38,
            199,
            426,
            686,
            756,
            818,
            1111,
            1181,
            1210,
            1635,
            2060,
            2097,
            2192,
            2262,
            2324,
            2394,
            2456,
            3178,
        )

    private fun div(
        a: Int,
        b: Int,
    ): Int = a / b

    private fun mod(
        a: Int,
        b: Int,
    ): Int = a % b

    private fun jalCal(jy: Int): JalCal {
        val bl = BREAKS.size
        val gy = jy + 621
        var leapJ = -14
        var jp = BREAKS[0]
        require(jy in jp until BREAKS[bl - 1]) { "Invalid Jalaali year $jy" }
        var jump = 0
        var jm: Int
        for (i in 1 until bl) {
            jm = BREAKS[i]
            jump = jm - jp
            if (jy < jm) break
            leapJ += div(jump, 33) * 8 + div(mod(jump, 33), 4)
            jp = jm
        }
        var n = jy - jp
        leapJ += div(n, 33) * 8 + div(mod(n, 33) + 3, 4)
        if (mod(jump, 33) == 4 && jump - n == 4) leapJ += 1
        val leapG = div(gy, 4) - div((div(gy, 100) + 1) * 3, 4) - 150
        val march = 20 + leapJ - leapG
        if (jump - n < 6) n = n - jump + div(jump + 4, 33) * 33
        var leap = mod(mod(n + 1, 33) - 1, 4)
        if (leap == -1) leap = 4
        return JalCal(leap = leap, gy = gy, march = march)
    }

    private fun gregorianToJdn(
        gy: Int,
        gm: Int,
        gd: Int,
    ): Int {
        var d =
            div((gy + div(gm - 8, 6) + 100100) * 1461, 4) +
                div(153 * mod(gm + 9, 12) + 2, 5) + gd - 34840408
        d = d - div(div(gy + 100100 + div(gm - 8, 6), 100) * 3, 4) + 752
        return d
    }

    private fun jdnToGregorian(jdn: Int): Triple<Int, Int, Int> {
        var j = 4 * jdn + 139361631
        j += div(div(4 * jdn + 183187720, 146097) * 3, 4) * 4 - 3908
        val i = div(mod(j, 1461), 4) * 5 + 308
        val gd = div(mod(i, 153), 5) + 1
        val gm = mod(div(i, 153), 12) + 1
        val gy = div(j, 1461) - 100100 + div(8 - gm, 6)
        return Triple(gy, gm, gd)
    }

    private fun jalaliToJdn(
        jy: Int,
        jm: Int,
        jd: Int,
    ): Int {
        val r = jalCal(jy)
        return gregorianToJdn(r.gy, 3, r.march) + (jm - 1) * 31 - div(jm, 7) * (jm - 7) + jd - 1
    }

    private fun jdnToJalali(jdn: Int): Triple<Int, Int, Int> {
        val gy = jdnToGregorian(jdn).first
        var jy = gy - 621
        val r = jalCal(jy)
        val jdn1f = gregorianToJdn(gy, 3, r.march)
        var k = jdn - jdn1f
        if (k >= 0) {
            if (k <= 185) {
                val jm = 1 + div(k, 31)
                val jd = mod(k, 31) + 1
                return Triple(jy, jm, jd)
            } else {
                k -= 186
            }
        } else {
            jy -= 1
            k += 179
            if (r.leap == 1) k += 1
        }
        val jm = 7 + div(k, 30)
        val jd = mod(k, 30) + 1
        return Triple(jy, jm, jd)
    }

    // endregion
}
