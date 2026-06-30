package io.github.alirezajavan.shamsipicker.model

import com.google.common.truth.Truth.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class ShamsiDateRangeTest {
    @Test
    fun `from and to are stored correctly`() {
        val from = ShamsiDate(1403, 1, 1)
        val to = ShamsiDate(1403, 3, 15)
        val range = ShamsiDateRange(from, to)
        assertThat(range.from).isEqualTo(from)
        assertThat(range.to).isEqualTo(to)
    }

    @Test
    fun `same-day range is valid`() {
        val date = ShamsiDate(1403, 6, 31)
        val range = ShamsiDateRange(date, date)
        assertThat(range.from).isEqualTo(range.to)
    }

    @Test
    fun `from and to are compared by year then month then day`() {
        val earlier = ShamsiDate(1402, 12, 29)
        val later = ShamsiDate(1403, 1, 1)
        assertThat(earlier).isLessThan(later)
        val range = ShamsiDateRange(earlier, later)
        assertThat(range.from).isLessThan(range.to)
    }

    @Test
    fun `cross-month range stores both endpoints`() {
        val from = ShamsiDate(1403, 6, 30)
        val to = ShamsiDate(1403, 7, 5)
        val range = ShamsiDateRange(from, to)
        assertThat(range.from.month).isEqualTo(6)
        assertThat(range.to.month).isEqualTo(7)
    }

    @Test
    fun `cross-year range stores both endpoints`() {
        val from = ShamsiDate(1402, 12, 1)
        val to = ShamsiDate(1403, 1, 1)
        val range = ShamsiDateRange(from, to)
        assertThat(range.from.year).isEqualTo(1402)
        assertThat(range.to.year).isEqualTo(1403)
    }

    @Test
    fun `data class equality holds`() {
        val a = ShamsiDateRange(ShamsiDate(1403, 1, 1), ShamsiDate(1403, 1, 10))
        val b = ShamsiDateRange(ShamsiDate(1403, 1, 1), ShamsiDate(1403, 1, 10))
        assertThat(a).isEqualTo(b)
    }

    @Test
    fun `copy produces a new instance with changed field`() {
        val original = ShamsiDateRange(ShamsiDate(1403, 1, 1), ShamsiDate(1403, 1, 31))
        val modified = original.copy(to = ShamsiDate(1403, 2, 15))
        assertThat(modified.from).isEqualTo(original.from)
        assertThat(modified.to).isEqualTo(ShamsiDate(1403, 2, 15))
    }
}
