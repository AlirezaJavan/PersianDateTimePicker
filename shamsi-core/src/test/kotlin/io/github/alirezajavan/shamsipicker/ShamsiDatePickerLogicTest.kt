package io.github.alirezajavan.shamsipicker

import com.google.common.truth.Truth.assertThat
import com.google.common.truth.Truth.assertWithMessage
import io.github.alirezajavan.shamsipicker.calendar.ShamsiCalendar
import io.github.alirezajavan.shamsipicker.model.ShamsiDate
import org.junit.jupiter.api.Test

class ShamsiDatePickerLogicTest {
    @Test
    fun `first 6 months always have 31 days`() {
        for (year in listOf(1399, 1400, 1402, 1403)) {
            for (month in 1..6) {
                assertWithMessage("year=$year month=$month")
                    .that(ShamsiCalendar.monthLength(year, month))
                    .isEqualTo(31)
            }
        }
    }

    @Test
    fun `months 7 to 11 always have 30 days`() {
        for (year in listOf(1399, 1400, 1402, 1403)) {
            for (month in 7..11) {
                assertWithMessage("year=$year month=$month")
                    .that(ShamsiCalendar.monthLength(year, month))
                    .isEqualTo(30)
            }
        }
    }

    @Test
    fun `Esfand has 29 days in common year and 30 in leap year`() {
        for (year in listOf(1400, 1401, 1402)) {
            assertWithMessage("Esfand $year").that(ShamsiCalendar.monthLength(year, 12)).isEqualTo(29)
        }
        for (year in listOf(1399, 1403, 1408)) {
            assertWithMessage("Esfand $year").that(ShamsiCalendar.monthLength(year, 12)).isEqualTo(30)
        }
    }

    @Test
    fun `day 31 clamps to 30 when switching to month 7`() {
        val maxDay = ShamsiCalendar.monthLength(1403, 7)
        val clamped = 31.coerceAtMost(maxDay)
        assertThat(clamped).isEqualTo(30)
    }

    @Test
    fun `day 30 clamps to 29 when switching to Esfand of a common year`() {
        val maxDay = ShamsiCalendar.monthLength(1402, 12)
        val clamped = 30.coerceAtMost(maxDay)
        assertThat(clamped).isEqualTo(29)
    }

    @Test
    fun `day 30 is NOT clamped in Esfand of a leap year`() {
        val maxDay = ShamsiCalendar.monthLength(1403, 12)
        val clamped = 30.coerceAtMost(maxDay)
        assertThat(clamped).isEqualTo(30)
    }

    @Test
    fun `all valid days in 1403 are never clamped`() {
        for (month in 1..12) {
            val maxDay = ShamsiCalendar.monthLength(1403, month)
            for (day in 1..maxDay) {
                assertThat(day.coerceAtMost(maxDay)).isEqualTo(day)
            }
        }
    }

    @Test
    fun `weekday of 1403-01-01 is Wednesday`() {
        assertThat(ShamsiCalendar.weekdayName(ShamsiDate(1403, 1, 1))).isEqualTo("چهارشنبه")
    }

    @Test
    fun `known weekday dates are correct`() {
        assertThat(ShamsiCalendar.weekdayName(ShamsiDate(1403, 1, 1))).isEqualTo("چهارشنبه")
        assertThat(ShamsiCalendar.weekdayName(ShamsiDate(1402, 12, 29))).isEqualTo("سه‌شنبه")
    }

    @Test
    fun `last day of each month in 1403 round-trips correctly`() {
        for (month in 1..12) {
            val lastDay = ShamsiCalendar.monthLength(1403, month)
            val shamsi = ShamsiDate(1403, month, lastDay)
            val back = ShamsiCalendar.fromGregorian(ShamsiCalendar.toGregorian(shamsi))
            assertWithMessage("1403/$month/$lastDay").that(back).isEqualTo(shamsi)
        }
    }

    @Test
    fun `Esfand 29 in common year round-trips`() {
        val shamsi = ShamsiDate(1402, 12, 29)
        assertThat(ShamsiCalendar.fromGregorian(ShamsiCalendar.toGregorian(shamsi))).isEqualTo(shamsi)
    }

    @Test
    fun `Esfand 30 in leap year round-trips`() {
        val shamsi = ShamsiDate(1403, 12, 30)
        assertThat(ShamsiCalendar.fromGregorian(ShamsiCalendar.toGregorian(shamsi))).isEqualTo(shamsi)
    }

    @Test
    fun `day after Esfand 30 leap year is 1 Farvardin next year`() {
        val nextDay = ShamsiCalendar.toGregorian(ShamsiDate(1403, 12, 30)).plusDays(1)
        assertThat(ShamsiCalendar.fromGregorian(nextDay)).isEqualTo(ShamsiDate(1404, 1, 1))
    }

    @Test
    fun `day after Esfand 29 common year is 1 Farvardin next year`() {
        val nextDay = ShamsiCalendar.toGregorian(ShamsiDate(1402, 12, 29)).plusDays(1)
        assertThat(ShamsiCalendar.fromGregorian(nextDay)).isEqualTo(ShamsiDate(1403, 1, 1))
    }

    @Test
    fun `all 12 month names are correct`() {
        val expected =
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
        for ((i, name) in expected.withIndex()) assertThat(ShamsiCalendar.monthName(i + 1)).isEqualTo(name)
    }

    @Test
    fun `weekday names cover all 7 days in Persian week order`() {
        assertThat(ShamsiCalendar.WEEKDAY_NAMES)
            .containsExactly(
                "شنبه",
                "یکشنبه",
                "دوشنبه",
                "سه‌شنبه",
                "چهارشنبه",
                "پنجشنبه",
                "جمعه",
            ).inOrder()
    }

    @Test
    fun `known leap years are detected correctly`() {
        for (y in listOf(1391, 1395, 1399, 1403, 1408)) {
            assertWithMessage("$y should be leap").that(ShamsiCalendar.isLeapYear(y)).isTrue()
        }
        for (y in listOf(1392, 1393, 1394, 1396, 1400, 1401, 1402, 1404)) {
            assertWithMessage("$y should be common").that(ShamsiCalendar.isLeapYear(y)).isFalse()
        }
    }
}
