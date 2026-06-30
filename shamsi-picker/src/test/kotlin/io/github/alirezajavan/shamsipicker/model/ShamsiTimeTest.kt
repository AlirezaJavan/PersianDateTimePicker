package io.github.alirezajavan.shamsipicker.model

import com.google.common.truth.Truth.assertThat
import org.junit.jupiter.api.Test
import java.time.LocalTime

class ShamsiTimeTest {
    @Test
    fun `total minutes calculation is correct`() {
        assertThat(ShamsiTime(0, 0).totalMinutes).isEqualTo(0)
        assertThat(ShamsiTime(1, 30).totalMinutes).isEqualTo(90)
        assertThat(ShamsiTime(23, 59).totalMinutes).isEqualTo(24 * 60 - 1)
    }

    @Test
    fun `comparison works correctly`() {
        assertThat(ShamsiTime(8, 0)).isLessThan(ShamsiTime(9, 0))
        assertThat(ShamsiTime(12, 30)).isAtLeast(ShamsiTime(12, 30))
        assertThat(ShamsiTime(20, 0)).isGreaterThan(ShamsiTime(10, 0))
    }

    @Test
    fun `conversion to and from LocalTime works`() {
        val local = LocalTime.of(14, 45)
        val shamsi = ShamsiTime.fromLocalTime(local)

        assertThat(shamsi.hour).isEqualTo(14)
        assertThat(shamsi.minute).isEqualTo(45)
        assertThat(shamsi.toLocalTime()).isEqualTo(local)
    }
}
