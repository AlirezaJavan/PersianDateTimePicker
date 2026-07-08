package io.github.alirezajavan.shamsipicker.model

import com.google.common.truth.Truth.assertThat
import io.github.alirezajavan.shamsipicker.calendar.GregorianCalendarSystem
import io.github.alirezajavan.shamsipicker.calendar.ShamsiCalendarSystem
import org.junit.jupiter.api.Test

class ShamsiDateConversionTest {
    // 1403/01/01 (Shamsi) == 2024/03/20 (Gregorian).
    private val shamsiNewYear = ShamsiDate(1403, 1, 1, hour = 13, minute = 45)

    @Test
    fun `toSystem converts a canonical Jalali date into Gregorian fields`() {
        val gregorian = shamsiNewYear.toSystem(GregorianCalendarSystem)

        assertThat(gregorian.year).isEqualTo(2024)
        assertThat(gregorian.month).isEqualTo(3)
        assertThat(gregorian.day).isEqualTo(20)
        assertThat(gregorian.hour).isEqualTo(13)
        assertThat(gregorian.minute).isEqualTo(45)
    }

    @Test
    fun `fromSystem converts Gregorian-space fields back to canonical Jalali`() {
        val gregorian = ShamsiDate(2024, 3, 20, hour = 13, minute = 45)

        val jalali = gregorian.fromSystem(GregorianCalendarSystem)

        assertThat(jalali).isEqualTo(shamsiNewYear)
    }

    @Test
    fun `toSystem and fromSystem are no-ops for ShamsiCalendarSystem`() {
        assertThat(shamsiNewYear.toSystem(ShamsiCalendarSystem)).isEqualTo(shamsiNewYear)
        assertThat(shamsiNewYear.fromSystem(ShamsiCalendarSystem)).isEqualTo(shamsiNewYear)
    }

    @Test
    fun `round-trip through toSystem then fromSystem is identity`() {
        val roundTripped = shamsiNewYear.toSystem(GregorianCalendarSystem).fromSystem(GregorianCalendarSystem)

        assertThat(roundTripped).isEqualTo(shamsiNewYear)
    }

    @Test
    fun `ShamsiDateLimit supports LocalDateTime`() {
        val localDateTime = java.time.LocalDateTime.of(2024, 3, 20, 13, 45)
        val limit = localDateTime.asLimit()
        val shamsi = limit.toShamsiDate()

        assertThat(shamsi.year).isEqualTo(1403)
        assertThat(shamsi.month).isEqualTo(1)
        assertThat(shamsi.day).isEqualTo(1)
        assertThat(shamsi.hour).isEqualTo(13)
        assertThat(shamsi.minute).isEqualTo(45)
    }
}
