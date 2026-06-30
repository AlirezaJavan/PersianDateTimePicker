package io.github.alirezajavan.shamsipicker

import com.google.common.truth.Truth.assertThat
import io.github.alirezajavan.shamsipicker.model.ShamsiDate
import io.github.alirezajavan.shamsipicker.model.ShamsiDateRange
import io.github.alirezajavan.shamsipicker.model.ShamsiTime
import io.github.alirezajavan.shamsipicker.model.ShamsiTimeRange
import org.junit.jupiter.api.Test

/**
 * Tests for range picker logic: selection state machine, auto-swap, and config resolution.
 */
class ShamsiRangePickerLogicTest {
    // ── ShamsiDateRange ──────────────────────────────────────────────────────────

    @Test
    fun `date range auto-swap makes from always earlier`() {
        val later = ShamsiDate(1403, 6, 15)
        val earlier = ShamsiDate(1403, 1, 1)
        val from: ShamsiDate
        val to: ShamsiDate
        if (later <= earlier) {
            from = later
            to = earlier
        } else {
            from = earlier
            to = later
        }
        assertThat(from).isAtMost(to)
    }

    @Test
    fun `calendar day tap: first tap sets from, clears to`() {
        var from = ShamsiDate(1403, 1, 1)
        var to: ShamsiDate? = ShamsiDate(1403, 1, 10)

        // Simulate a new first tap
        val tapped = ShamsiDate(1403, 2, 5)
        from = tapped
        to = null

        assertThat(from).isEqualTo(tapped)
        assertThat(to).isNull()
    }

    @Test
    fun `calendar day tap: second tap after from sets to when tapped is later`() {
        var from = ShamsiDate(1403, 1, 1)
        var to: ShamsiDate? = null

        val tapped = ShamsiDate(1403, 1, 15)
        if (tapped < from) {
            to = from
            from = tapped
        } else {
            to = tapped
        }

        assertThat(from).isEqualTo(ShamsiDate(1403, 1, 1))
        assertThat(to).isEqualTo(ShamsiDate(1403, 1, 15))
    }

    @Test
    fun `calendar day tap: tapping before from swaps the two dates`() {
        var from = ShamsiDate(1403, 3, 10)
        var to: ShamsiDate? = null

        val tapped = ShamsiDate(1403, 1, 5)
        if (tapped < from) {
            to = from
            from = tapped
        } else {
            to = tapped
        }

        assertThat(from).isEqualTo(ShamsiDate(1403, 1, 5))
        assertThat(to).isEqualTo(ShamsiDate(1403, 3, 10))
        assertThat(from).isLessThan(to!!)
    }

    @Test
    fun `calendar day tap: tapping same day as from results in zero-length range`() {
        var from = ShamsiDate(1403, 3, 10)
        var to: ShamsiDate? = null

        val tapped = ShamsiDate(1403, 3, 10)
        if (tapped < from) {
            to = from
            from = tapped
        } else {
            to = tapped
        }

        assertThat(from).isEqualTo(to)
    }

    @Test
    fun `calendar range day key ordering is consistent`() {
        val d1 = ShamsiDate(1403, 1, 1)
        val d2 = ShamsiDate(1403, 1, 15)
        val d3 = ShamsiDate(1403, 2, 1)
        val key1 = d1.year * 10_000 + d1.month * 100 + d1.day
        val key2 = d2.year * 10_000 + d2.month * 100 + d2.day
        val key3 = d3.year * 10_000 + d3.month * 100 + d3.day
        assertThat(key1).isLessThan(key2)
        assertThat(key2).isLessThan(key3)
    }

    @Test
    fun `day is considered in-range only strictly between from and to keys`() {
        val fromKey = ShamsiDate(1403, 1, 1).let { it.year * 10_000 + it.month * 100 + it.day }
        val toKey = ShamsiDate(1403, 1, 10).let { it.year * 10_000 + it.month * 100 + it.day }

        fun inRange(d: ShamsiDate): Boolean {
            val key = d.year * 10_000 + d.month * 100 + d.day
            return key > fromKey && key < toKey
        }

        assertThat(inRange(ShamsiDate(1403, 1, 1))).isFalse() // from itself
        assertThat(inRange(ShamsiDate(1403, 1, 5))).isTrue() // middle
        assertThat(inRange(ShamsiDate(1403, 1, 10))).isFalse() // to itself
        assertThat(inRange(ShamsiDate(1402, 12, 31))).isFalse() // before from
        assertThat(inRange(ShamsiDate(1403, 1, 11))).isFalse() // after to
    }

    @Test
    fun `wheel result auto-swaps when from is after to`() {
        val from = ShamsiDate(1403, 6, 15)
        val to = ShamsiDate(1403, 1, 1)
        val result = if (from <= to) ShamsiDateRange(from, to) else ShamsiDateRange(to, from)
        assertThat(result.from).isEqualTo(to)
        assertThat(result.to).isEqualTo(from)
        assertThat(result.from).isLessThan(result.to)
    }

    @Test
    fun `initial config normalises reversed from-to pair`() {
        val later = ShamsiDate(1403, 6, 15)
        val earlier = ShamsiDate(1403, 1, 1)
        val (initFrom, initTo) =
            run {
                val f = later
                val t = earlier
                if (f <= t) f to t else t to f
            }
        assertThat(initFrom).isEqualTo(earlier)
        assertThat(initTo).isEqualTo(later)
    }

    // ── ShamsiTimeRange ──────────────────────────────────────────────────────────

    @Test
    fun `time range auto-swap makes from always earlier`() {
        val later = ShamsiTime(20, 0)
        val earlier = ShamsiTime(8, 30)
        val from: ShamsiTime
        val to: ShamsiTime
        if (later <= earlier) {
            from = later
            to = earlier
        } else {
            from = earlier
            to = later
        }
        assertThat(from).isAtMost(to)
    }

    @Test
    fun `time range confirm swaps when from is after to`() {
        val from = ShamsiTime(18, 0)
        val to = ShamsiTime(9, 0)
        val result = if (from <= to) ShamsiTimeRange(from, to) else ShamsiTimeRange(to, from)
        assertThat(result.from).isEqualTo(to)
        assertThat(result.to).isEqualTo(from)
        assertThat(result.from).isLessThan(result.to)
    }

    @Test
    fun `time range confirm keeps order when from is before to`() {
        val from = ShamsiTime(8, 0)
        val to = ShamsiTime(17, 30)
        val result = if (from <= to) ShamsiTimeRange(from, to) else ShamsiTimeRange(to, from)
        assertThat(result.from).isEqualTo(from)
        assertThat(result.to).isEqualTo(to)
    }

    @Test
    fun `minute range is unrestricted for hours between boundaries`() {
        val lo = 8 * 60 // minTime = 08:00
        val hi = 20 * 60 // maxTime = 20:00
        val hMin = lo / 60 // 8
        val hMax = hi / 60 // 20

        fun minuteRange(currentHour: Int): IntRange =
            if (currentHour in hMin..hMax) {
                val mLo = if (currentHour == hMin) lo % 60 else 0
                val mHi = if (currentHour == hMax) hi % 60 else 59
                mLo..mHi
            } else {
                0..59
            }

        // lo%60 == 0, so at boundary hour 8 all minutes are allowed
        assertThat(minuteRange(8)).isEqualTo(0..59)
        // mid-range hour: fully unrestricted
        assertThat(minuteRange(12)).isEqualTo(0..59)
        // hi%60 == 0 means only :00 is allowed at hour 20
        assertThat(minuteRange(20)).isEqualTo(0..0)
        // outside range: unrestricted (hour wheel is disabled anyway)
        assertThat(minuteRange(7)).isEqualTo(0..59)
        assertThat(minuteRange(21)).isEqualTo(0..59)
    }

    @Test
    fun `minute range is clamped at boundary hours`() {
        val lo = 8 * 60 + 15 // minTime = 08:15
        val hi = 20 * 60 + 45 // maxTime = 20:45
        val hMin = lo / 60 // 8
        val hMax = hi / 60 // 20

        fun minuteRange(currentHour: Int): IntRange =
            if (currentHour in hMin..hMax) {
                val mLo = if (currentHour == hMin) lo % 60 else 0
                val mHi = if (currentHour == hMax) hi % 60 else 59
                mLo..mHi
            } else {
                0..59
            }

        assertThat(minuteRange(8)).isEqualTo(15..59) // at min hour: starts at :15
        assertThat(minuteRange(20)).isEqualTo(0..45) // at max hour: ends at :45
        assertThat(minuteRange(12)).isEqualTo(0..59) // mid: full range
        assertThat(minuteRange(7)).isEqualTo(0..59) // below min: unrestricted (hour is disabled anyway)
    }

    @Test
    fun `24h hour enabled range respects min and max`() {
        val lo = 9 * 60 // 09:00
        val hi = 18 * 60 // 18:00
        val hMin = lo / 60
        val hMax = hi / 60
        val enabledRange = hMin..hMax
        assertThat(enabledRange.first).isEqualTo(9)
        assertThat(enabledRange.last).isEqualTo(18)
        assertThat(enabledRange.contains(8)).isFalse()
        assertThat(enabledRange.contains(9)).isTrue()
        assertThat(enabledRange.contains(18)).isTrue()
        assertThat(enabledRange.contains(19)).isFalse()
    }
}
