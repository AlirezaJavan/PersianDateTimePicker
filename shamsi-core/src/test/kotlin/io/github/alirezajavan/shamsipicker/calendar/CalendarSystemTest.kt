package io.github.alirezajavan.shamsipicker.calendar

import com.google.common.truth.Truth.assertThat
import org.junit.jupiter.api.Test
import java.time.LocalDate

class CalendarSystemTest {
    @Test
    fun `ShamsiCalendarSystem round-trip via epoch days`() {
        val system = ShamsiCalendarSystem
        val year = 1403
        val month = 5
        val day = 15

        val epochDay = system.toEpochDay(year, month, day)
        val (ry, rm, rd) = system.fromEpochDay(epochDay)

        assertThat(ry).isEqualTo(year)
        assertThat(rm).isEqualTo(month)
        assertThat(rd).isEqualTo(day)
    }

    @Test
    fun `GregorianCalendarSystem round-trip via epoch days`() {
        val system = GregorianCalendarSystem
        val year = 2024
        val month = 8
        val day = 5

        val epochDay = system.toEpochDay(year, month, day)
        val (ry, rm, rd) = system.fromEpochDay(epochDay)

        assertThat(ry).isEqualTo(year)
        assertThat(rm).isEqualTo(month)
        assertThat(rd).isEqualTo(day)
    }

    @Test
    fun `shared epoch day equality test`() {
        val shamsi = ShamsiCalendarSystem
        val gregorian = GregorianCalendarSystem

        // 2024-03-20 is 1403-01-01
        val gDate = LocalDate.of(2024, 3, 20)
        val epochDay = gDate.toEpochDay()

        val (sy, sm, sd) = shamsi.fromEpochDay(epochDay)
        assertThat(sy).isEqualTo(1403)
        assertThat(sm).isEqualTo(1)
        assertThat(sd).isEqualTo(1)

        val (gy, gm, gd) = gregorian.fromEpochDay(epochDay)
        assertThat(gy).isEqualTo(2024)
        assertThat(gm).isEqualTo(3)
        assertThat(gd).isEqualTo(20)
    }

    @Test
    fun `Shamsi leap years`() {
        assertThat(ShamsiCalendarSystem.isLeapYear(1403)).isTrue()
        assertThat(ShamsiCalendarSystem.isLeapYear(1402)).isFalse()
    }

    @Test
    fun `Gregorian leap years`() {
        assertThat(GregorianCalendarSystem.isLeapYear(2024)).isTrue()
        assertThat(GregorianCalendarSystem.isLeapYear(2023)).isFalse()
    }

    @Test
    fun `month lengths for both systems`() {
        assertThat(ShamsiCalendarSystem.monthLength(1403, 1)).isEqualTo(31)
        assertThat(ShamsiCalendarSystem.monthLength(1403, 12)).isEqualTo(30)
        assertThat(ShamsiCalendarSystem.monthLength(1402, 12)).isEqualTo(29)

        assertThat(GregorianCalendarSystem.monthLength(2024, 2)).isEqualTo(29)
        assertThat(GregorianCalendarSystem.monthLength(2023, 2)).isEqualTo(28)
        assertThat(GregorianCalendarSystem.monthLength(2024, 1)).isEqualTo(31)
    }

    @Test
    fun `first weekday of month`() {
        // 1403-01-01 was Wednesday. Shamsi Sat=0, Sun=1, Mon=2, Tue=3, Wed=4.
        assertThat(ShamsiCalendarSystem.firstWeekdayOfMonth(1403, 1)).isEqualTo(4)

        // 2024-08-01 was Thursday. Gregorian Sun=0, Mon=1, Tue=2, Wed=3, Thu=4.
        assertThat(GregorianCalendarSystem.firstWeekdayOfMonth(2024, 8)).isEqualTo(4)
    }
}
