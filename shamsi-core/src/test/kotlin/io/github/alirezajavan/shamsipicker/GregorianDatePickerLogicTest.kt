package io.github.alirezajavan.shamsipicker

import com.google.common.truth.Truth.assertThat
import io.github.alirezajavan.shamsipicker.calendar.GregorianCalendarSystem
import io.github.alirezajavan.shamsipicker.model.ShamsiDate
import org.junit.jupiter.api.Test
import java.time.DayOfWeek

class GregorianDatePickerLogicTest {
    private val system = GregorianCalendarSystem

    @Test
    fun `months have correct lengths`() {
        val months31 = listOf(1, 3, 5, 7, 8, 10, 12)
        val months30 = listOf(4, 6, 9, 11)
        for (m in months31) {
            assertThat(system.monthLength(2024, m)).isEqualTo(31)
        }
        for (m in months30) {
            assertThat(system.monthLength(2024, m)).isEqualTo(30)
        }
    }

    @Test
    fun `February has 28 days in common year and 29 in leap year`() {
        assertThat(system.monthLength(2023, 2)).isEqualTo(28)
        assertThat(system.monthLength(2024, 2)).isEqualTo(29)
        assertThat(system.monthLength(2100, 2)).isEqualTo(28) // divisible by 100 but not 400
        assertThat(system.monthLength(2000, 2)).isEqualTo(29) // divisible by 400
    }

    @Test
    fun `known leap years are detected correctly`() {
        assertThat(system.isLeapYear(2024)).isTrue()
        assertThat(system.isLeapYear(2023)).isFalse()
        assertThat(system.isLeapYear(2000)).isTrue()
        assertThat(system.isLeapYear(2100)).isFalse()
    }

    @Test
    fun `weekday names are correct in English`() {
        assertThat(system.weekdayNames(DayOfWeek.SUNDAY))
            .containsExactly("Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat")
            .inOrder()
        assertThat(system.weekdayNames(DayOfWeek.MONDAY))
            .containsExactly("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun")
            .inOrder()
    }

    @Test
    fun `weekday names are correct in English starting Saturday`() {
        assertThat(system.weekdayNames(DayOfWeek.SATURDAY))
            .containsExactly("Sat", "Sun", "Mon", "Tue", "Wed", "Thu", "Fri")
            .inOrder()
    }

    @Test
    fun `month names are in English`() {
        assertThat(system.monthNames(2024)[0]).isEqualTo("January")
        assertThat(system.monthNames(2024)[11]).isEqualTo("December")
    }

    @Test
    fun `bounds enforcement works correctly`() {
        val min = ShamsiDate(2024, 3, 20) // Gregorian
        val max = ShamsiDate(2024, 3, 25) // Gregorian

        assertThat(system.monthBounds(2024, min, max)).isEqualTo(3..3)
        assertThat(system.dayBounds(2024, 3, 31, min, max)).isEqualTo(20..25)

        assertThat(system.monthBounds(2023, min, max)).isEqualTo(1..12)
        assertThat(system.monthBounds(2024, null, null)).isEqualTo(1..12)
    }
}
