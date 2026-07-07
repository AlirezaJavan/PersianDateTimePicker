package io.github.alirezajavan.shamsipicker.format

import com.google.common.truth.Truth.assertThat
import io.github.alirezajavan.shamsipicker.calendar.CalendarType
import io.github.alirezajavan.shamsipicker.model.ShamsiDate
import org.junit.jupiter.api.Test

class DateFormatterTest {
    // 1403/01/01 (Shamsi) == 2024/03/20 (Gregorian). ShamsiDate always carries the
    // canonical Jalali fields; Gregorian formatting must convert before rendering.
    private val shamsiNewYear = ShamsiDate(1403, 1, 1)

    @Test
    fun `short formats the Gregorian-equivalent date, not the raw Jalali fields`() {
        val text = DateFormatter.short(shamsiNewYear, CalendarType.Gregorian)

        assertThat(text).isEqualTo("2024/03/20")
    }

    @Test
    fun `long formats the Gregorian-equivalent date, not the raw Jalali fields`() {
        val text = DateFormatter.long(shamsiNewYear, CalendarType.Gregorian)

        assertThat(text).isEqualTo("Wed, March 20, 2024")
    }

    @Test
    fun `short keeps existing Shamsi behavior unchanged`() {
        val text = DateFormatter.short(shamsiNewYear, CalendarType.Shamsi)

        assertThat(text).isEqualTo("۱۴۰۳/۰۱/۰۱")
    }
}
