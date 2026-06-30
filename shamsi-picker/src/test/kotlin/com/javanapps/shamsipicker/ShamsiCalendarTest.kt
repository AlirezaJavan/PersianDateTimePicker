package com.javanapps.shamsipicker

import com.google.common.truth.Truth.assertThat
import com.javanapps.shamsipicker.calendar.ShamsiCalendar
import com.javanapps.shamsipicker.model.ShamsiDate
import org.junit.jupiter.api.Test
import java.time.LocalDate

class ShamsiCalendarTest {
    @Test
    fun `gregorian to shamsi for known Nowruz dates`() {
        assertThat(ShamsiCalendar.fromGregorian(LocalDate.of(2024, 3, 20)))
            .isEqualTo(ShamsiDate(1403, 1, 1))
        assertThat(ShamsiCalendar.fromGregorian(LocalDate.of(2021, 3, 21)))
            .isEqualTo(ShamsiDate(1400, 1, 1))
        assertThat(ShamsiCalendar.fromGregorian(LocalDate.of(2011, 3, 21)))
            .isEqualTo(ShamsiDate(1390, 1, 1))
    }

    @Test
    fun `shamsi to gregorian round trips`() {
        assertThat(ShamsiCalendar.toGregorian(ShamsiDate(1403, 1, 1)))
            .isEqualTo(LocalDate.of(2024, 3, 20))
        assertThat(ShamsiCalendar.toGregorian(ShamsiDate(1357, 11, 22)))
            .isEqualTo(LocalDate.of(1979, 2, 11))
    }

    @Test
    fun `conversion is reversible across a multi-year range`() {
        var date = LocalDate.of(1995, 1, 1)
        val end = LocalDate.of(2035, 12, 31)
        while (!date.isAfter(end)) {
            val shamsi = ShamsiCalendar.fromGregorian(date)
            val back = ShamsiCalendar.toGregorian(shamsi)
            assertThat(back).isEqualTo(date)
            date = date.plusDays(1)
        }
    }

    @Test
    fun `leap years are detected`() {
        assertThat(ShamsiCalendar.isLeapYear(1399)).isTrue()
        assertThat(ShamsiCalendar.isLeapYear(1403)).isTrue()
        assertThat(ShamsiCalendar.isLeapYear(1400)).isFalse()
        assertThat(ShamsiCalendar.isLeapYear(1401)).isFalse()
        assertThat(ShamsiCalendar.isLeapYear(1402)).isFalse()
    }

    @Test
    fun `month lengths follow the 31-30-29 pattern`() {
        for (m in 1..6) assertThat(ShamsiCalendar.monthLength(1402, m)).isEqualTo(31)
        for (m in 7..11) assertThat(ShamsiCalendar.monthLength(1402, m)).isEqualTo(30)
        assertThat(ShamsiCalendar.monthLength(1402, 12)).isEqualTo(29)
        assertThat(ShamsiCalendar.monthLength(1403, 12)).isEqualTo(30)
    }

    @Test
    fun `weekday name is correct`() {
        assertThat(ShamsiCalendar.weekdayName(ShamsiDate(1403, 1, 1))).isEqualTo("چهارشنبه")
        assertThat(ShamsiCalendar.weekdayName(ShamsiDate(1402, 12, 29))).isEqualTo("سه‌شنبه")
    }

    @Test
    fun `month names are in Persian`() {
        assertThat(ShamsiCalendar.monthName(1)).isEqualTo("فروردین")
        assertThat(ShamsiCalendar.monthName(12)).isEqualTo("اسفند")
    }

    @Test
    fun `epoch millis round trips with time fields`() {
        val date = ShamsiDate(1403, 5, 15, hour = 13, minute = 45)
        val millis = ShamsiCalendar.toEpochMillis(date)
        assertThat(ShamsiCalendar.fromEpochMillis(millis)).isEqualTo(date)
    }
}
