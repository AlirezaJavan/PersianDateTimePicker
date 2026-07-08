package io.github.alirezajavan.shamsipicker.model

import com.google.common.truth.Truth.assertThat
import org.junit.jupiter.api.Test

class CalendarEventTest {
    @Test
    fun `event matches a date on the same calendar day`() {
        val event = CalendarEvent(date = ShamsiDate(1403, 1, 1), label = "Nowruz")
        assertThat(event.isOnSameDayAs(ShamsiDate(1403, 1, 1))).isTrue()
    }

    @Test
    fun `event ignores hour and minute when matching`() {
        val event = CalendarEvent(date = ShamsiDate(1403, 1, 1, hour = 10, minute = 30), label = "Nowruz")
        assertThat(event.isOnSameDayAs(ShamsiDate(1403, 1, 1, hour = 0, minute = 0))).isTrue()
    }

    @Test
    fun `event does not match a different day`() {
        val event = CalendarEvent(date = ShamsiDate(1403, 1, 1), label = "Nowruz")
        assertThat(event.isOnSameDayAs(ShamsiDate(1403, 1, 2))).isFalse()
    }

    @Test
    fun `event does not match across a month boundary`() {
        val event = CalendarEvent(date = ShamsiDate(1403, 6, 31), label = "End of Shahrivar")
        assertThat(event.isOnSameDayAs(ShamsiDate(1403, 7, 1))).isFalse()
    }

    @Test
    fun `event does not match across a year boundary`() {
        val event = CalendarEvent(date = ShamsiDate(1402, 12, 29), label = "Year end")
        assertThat(event.isOnSameDayAs(ShamsiDate(1403, 1, 1))).isFalse()
    }

    @Test
    fun `type defaults to Event and can be set to Holiday`() {
        val default = CalendarEvent(date = ShamsiDate(1403, 1, 1), label = "App Reminder")
        assertThat(default.type).isEqualTo(CalendarEventType.Event)

        val holiday = default.copy(type = CalendarEventType.Holiday)
        assertThat(holiday.type).isEqualTo(CalendarEventType.Holiday)
    }

    @Test
    fun `colorArgb defaults to null and can be set`() {
        val default = CalendarEvent(date = ShamsiDate(1403, 1, 1), label = "Nowruz")
        assertThat(default.colorArgb).isNull()

        val colored = default.copy(colorArgb = 0xFFE53935.toInt())
        assertThat(colored.colorArgb).isEqualTo(0xFFE53935.toInt())
    }
}
