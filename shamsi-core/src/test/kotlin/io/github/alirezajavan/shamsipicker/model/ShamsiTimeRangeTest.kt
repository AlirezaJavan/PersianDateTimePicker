package io.github.alirezajavan.shamsipicker.model

import com.google.common.truth.Truth.assertThat
import org.junit.jupiter.api.Test

class ShamsiTimeRangeTest {
    @Test
    fun `from and to are stored correctly`() {
        val from = ShamsiTime(9, 0)
        val to = ShamsiTime(17, 30)
        val range = ShamsiTimeRange(from, to)
        assertThat(range.from).isEqualTo(from)
        assertThat(range.to).isEqualTo(to)
    }

    @Test
    fun `same-time range is valid`() {
        val time = ShamsiTime(12, 0)
        val range = ShamsiTimeRange(time, time)
        assertThat(range.from).isEqualTo(range.to)
    }

    @Test
    fun `from is less than to when correctly ordered`() {
        val from = ShamsiTime(8, 0)
        val to = ShamsiTime(20, 0)
        val range = ShamsiTimeRange(from, to)
        assertThat(range.from).isLessThan(range.to)
    }

    @Test
    fun `midnight range spans full day`() {
        val range = ShamsiTimeRange(ShamsiTime(0, 0), ShamsiTime(23, 59))
        assertThat(range.from.totalMinutes).isEqualTo(0)
        assertThat(range.to.totalMinutes).isEqualTo(23 * 60 + 59)
    }

    @Test
    fun `data class equality holds`() {
        val a = ShamsiTimeRange(ShamsiTime(9, 0), ShamsiTime(17, 0))
        val b = ShamsiTimeRange(ShamsiTime(9, 0), ShamsiTime(17, 0))
        assertThat(a).isEqualTo(b)
    }

    @Test
    fun `copy produces a new instance with changed field`() {
        val original = ShamsiTimeRange(ShamsiTime(9, 0), ShamsiTime(17, 0))
        val modified = original.copy(to = ShamsiTime(18, 30))
        assertThat(modified.from).isEqualTo(original.from)
        assertThat(modified.to).isEqualTo(ShamsiTime(18, 30))
    }
}
