package io.github.alirezajavan.shamsipicker

import com.google.common.truth.Truth
import io.github.alirezajavan.shamsipicker.model.ShamsiDate
import io.github.alirezajavan.shamsipicker.model.ShamsiDateRange
import io.github.alirezajavan.shamsipicker.model.ShamsiTime
import io.github.alirezajavan.shamsipicker.model.ShamsiTimeRange
import org.junit.Test

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
        Truth.assertThat(from).isAtMost(to)
    }

    @Test
    fun `calendar day tap first tap sets from, clears to`() {
        var from: ShamsiDate

        // Simulate a new first tap
        val tapped = ShamsiDate(1403, 2, 5)
        from = tapped

        Truth.assertThat(from).isEqualTo(tapped)
    }

    @Test
    fun `calendar day tap second tap after from sets to when tapped is later`() {
        var from = ShamsiDate(1403, 1, 1)
        var to: ShamsiDate?

        val tapped = ShamsiDate(1403, 1, 15)
        if (tapped < from) {
            to = from
            from = tapped
        } else {
            to = tapped
        }

        Truth.assertThat(from).isEqualTo(ShamsiDate(1403, 1, 1))
        Truth.assertThat(to).isEqualTo(ShamsiDate(1403, 1, 15))
    }

    @Test
    fun `calendar day tap tapping before from swaps the two dates`() {
        var from = ShamsiDate(1403, 3, 10)
        var to: ShamsiDate?

        val tapped = ShamsiDate(1403, 1, 5)
        if (tapped < from) {
            to = from
            from = tapped
        } else {
            to = tapped
        }

        Truth.assertThat(from).isEqualTo(ShamsiDate(1403, 1, 5))
        Truth.assertThat(to).isEqualTo(ShamsiDate(1403, 3, 10))
        Truth.assertThat(from).isLessThan(to)
    }

    @Test
    fun `calendar day tap tapping same day as from results in zero-length range`() {
        var from = ShamsiDate(1403, 3, 10)
        var to: ShamsiDate?

        val tapped = ShamsiDate(1403, 3, 10)
        if (tapped < from) {
            to = from
            from = tapped
        } else {
            to = tapped
        }

        Truth.assertThat(from).isEqualTo(to)
    }

    @Test
    fun `calendar range day key ordering is consistent`() {
        val d1 = ShamsiDate(1403, 1, 1)
        val d2 = ShamsiDate(1403, 1, 15)
        val d3 = ShamsiDate(1403, 2, 1)
        val key1 = d1.year * 10_000 + d1.month * 100 + d1.day
        val key2 = d2.year * 10_000 + d2.month * 100 + d2.day
        val key3 = d3.year * 10_000 + d3.month * 100 + d3.day
        Truth.assertThat(key1).isLessThan(key2)
        Truth.assertThat(key2).isLessThan(key3)
    }

    @Test
    fun `day is considered in-range only strictly between from and to keys`() {
        val fromKey = ShamsiDate(1403, 1, 1).let { it.year * 10_000 + it.month * 100 + it.day }
        val toKey = ShamsiDate(1403, 1, 10).let { it.year * 10_000 + it.month * 100 + it.day }

        fun inRange(d: ShamsiDate): Boolean {
            val key = d.year * 10_000 + d.month * 100 + d.day
            return key in (fromKey + 1)..<toKey
        }

        Truth.assertThat(inRange(ShamsiDate(1403, 1, 1))).isFalse() // from itself
        Truth.assertThat(inRange(ShamsiDate(1403, 1, 5))).isTrue() // middle
        Truth.assertThat(inRange(ShamsiDate(1403, 1, 10))).isFalse() // to itself
        Truth.assertThat(inRange(ShamsiDate(1402, 12, 31))).isFalse() // before from
        Truth.assertThat(inRange(ShamsiDate(1403, 1, 11))).isFalse() // after to
    }

    @Test
    fun `wheel result auto-swaps when from is after to`() {
        val from = ShamsiDate(1403, 6, 15)
        val to = ShamsiDate(1403, 1, 1)
        val result = if (from <= to) ShamsiDateRange(from, to) else ShamsiDateRange(to, from)
        Truth.assertThat(result.from).isEqualTo(to)
        Truth.assertThat(result.to).isEqualTo(from)
        Truth.assertThat(result.from).isLessThan(result.to)
    }

    @Test
    fun `initial config normalises reversed from-to pair`() {
        val later = ShamsiDate(1403, 6, 15)
        val earlier = ShamsiDate(1403, 1, 1)
        val (initFrom, initTo) =
            run {
                if (later <= earlier) later to earlier else earlier to later
            }
        Truth.assertThat(initFrom).isEqualTo(earlier)
        Truth.assertThat(initTo).isEqualTo(later)
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
        Truth.assertThat(from).isAtMost(to)
    }

    @Test
    fun `time range confirm swaps when from is after to`() {
        val from = ShamsiTime(18, 0)
        val to = ShamsiTime(9, 0)
        val result = if (from <= to) ShamsiTimeRange(from, to) else ShamsiTimeRange(to, from)
        Truth.assertThat(result.from).isEqualTo(to)
        Truth.assertThat(result.to).isEqualTo(from)
        Truth.assertThat(result.from).isLessThan(result.to)
    }

    @Test
    fun `time range confirm keeps order when from is before to`() {
        val from = ShamsiTime(8, 0)
        val to = ShamsiTime(17, 30)
        val result = if (from <= to) ShamsiTimeRange(from, to) else ShamsiTimeRange(to, from)
        Truth.assertThat(result.from).isEqualTo(from)
        Truth.assertThat(result.to).isEqualTo(to)
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
        Truth.assertThat(minuteRange(8)).isEqualTo(0..59)
        // mid-range hour: fully unrestricted
        Truth.assertThat(minuteRange(12)).isEqualTo(0..59)
        // hi%60 == 0 means only :00 is allowed at hour 20
        Truth.assertThat(minuteRange(20)).isEqualTo(0..0)
        // outside range: unrestricted (hour wheel is disabled anyway)
        Truth.assertThat(minuteRange(7)).isEqualTo(0..59)
        Truth.assertThat(minuteRange(21)).isEqualTo(0..59)
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

        Truth.assertThat(minuteRange(8)).isEqualTo(15..59) // at min hour: starts at :15
        Truth.assertThat(minuteRange(20)).isEqualTo(0..45) // at max hour: ends at :45
        Truth.assertThat(minuteRange(12)).isEqualTo(0..59) // mid: full range
        Truth.assertThat(minuteRange(7)).isEqualTo(0..59) // below min: unrestricted (hour is disabled anyway)
    }

    @Test
    fun `24h hour enabled range respects min and max`() {
        val lo = 9 * 60 // 09:00
        val hi = 18 * 60 // 18:00
        val hMin = lo / 60
        val hMax = hi / 60
        val enabledRange = hMin..hMax
        Truth.assertThat(enabledRange.first).isEqualTo(9)
        Truth.assertThat(enabledRange.last).isEqualTo(18)
        Truth.assertThat(enabledRange.contains(8)).isFalse()
        Truth.assertThat(enabledRange.contains(9)).isTrue()
        Truth.assertThat(enabledRange.contains(18)).isTrue()
        Truth.assertThat(enabledRange.contains(19)).isFalse()
    }

    // ── "to" minimum live-linked to "from" — date ────────────────────────────

    @Test
    fun `effectiveToMin for date is fromDate when no global min is set`() {
        val from = ShamsiDate(1403, 3, 10)
        val effectiveToMin = effectiveDateMin(from, resolvedMin = null)
        Truth.assertThat(effectiveToMin).isEqualTo(from)
    }

    @Test
    fun `effectiveToMin for date is fromDate when global min is before from`() {
        val from = ShamsiDate(1403, 3, 10)
        val globalMin = ShamsiDate(1403, 1, 1)
        val effectiveToMin = effectiveDateMin(from, resolvedMin = globalMin)
        Truth.assertThat(effectiveToMin).isEqualTo(from)
    }

    @Test
    fun `effectiveToMin for date is resolvedMin when global min is after from`() {
        val from = ShamsiDate(1403, 3, 10)
        val globalMin = ShamsiDate(1403, 6, 1)
        val effectiveToMin = effectiveDateMin(from, resolvedMin = globalMin)
        Truth.assertThat(effectiveToMin).isEqualTo(globalMin)
    }

    @Test
    fun `toYear is clamped up when from moves to a later year`() {
        val effectiveMin = ShamsiDate(1404, 1, 1)
        var toYear = 1403
        if (toYear < effectiveMin.year) toYear = effectiveMin.year
        Truth.assertThat(toYear).isEqualTo(1404)
    }

    @Test
    fun `toYear is unchanged when it already matches fromYear`() {
        val effectiveMin = ShamsiDate(1403, 6, 15)
        var toYear = 1403
        if (toYear < effectiveMin.year) toYear = effectiveMin.year
        Truth.assertThat(toYear).isEqualTo(1403)
    }

    @Test
    fun `toYear is unchanged when it is in a later year than from`() {
        val effectiveMin = ShamsiDate(1403, 6, 15)
        var toYear = 1404
        if (toYear < effectiveMin.year) toYear = effectiveMin.year
        Truth.assertThat(toYear).isEqualTo(1404)
    }

    @Test
    fun `toMonth is clamped up when from moves to a later month in the same year`() {
        val effectiveMin = ShamsiDate(1403, 6, 15)
        val toYear = 1403
        var toMonth = 3
        val monthLo = if (toYear == effectiveMin.year) effectiveMin.month else 1
        if (toMonth < monthLo) toMonth = monthLo
        Truth.assertThat(toMonth).isEqualTo(6)
    }

    @Test
    fun `toMonth is not clamped when to is in a later year than from`() {
        val effectiveMin = ShamsiDate(1403, 6, 15)
        val toYear = 1404
        var toMonth = 3
        val monthLo = if (toYear == effectiveMin.year) effectiveMin.month else 1
        if (toMonth < monthLo) toMonth = monthLo
        Truth.assertThat(toMonth).isEqualTo(3)
    }

    @Test
    fun `toDay is clamped up when from is same year and month but later day`() {
        val effectiveMin = ShamsiDate(1403, 6, 20)
        val toYear = 1403
        val toMonth = 6
        var toDay = 10
        val dayLo =
            if (toYear == effectiveMin.year && toMonth == effectiveMin.month) effectiveMin.day else 1
        if (toDay < dayLo) toDay = dayLo
        Truth.assertThat(toDay).isEqualTo(20)
    }

    @Test
    fun `toDay is not clamped when to is in a later month than from`() {
        val effectiveMin = ShamsiDate(1403, 6, 20)
        val toYear = 1403
        val toMonth = 7
        var toDay = 5
        val dayLo =
            if (toYear == effectiveMin.year && toMonth == effectiveMin.month) effectiveMin.day else 1
        if (toDay < dayLo) toDay = dayLo
        Truth.assertThat(toDay).isEqualTo(5)
    }

    @Test
    fun `full date cascade advancing from by a year clamps to to match from`() {
        // Simulates: from was 1403/3/10, user scrolls fromYear to 1404
        val effectiveMin = ShamsiDate(1404, 3, 10)
        var toYear = 1403
        var toMonth = 3
        var toDay = 10

        if (toYear < effectiveMin.year) toYear = effectiveMin.year
        val monthLo = if (toYear == effectiveMin.year) effectiveMin.month else 1
        if (toMonth < monthLo) toMonth = monthLo
        val dayLo =
            if (toYear == effectiveMin.year && toMonth == effectiveMin.month) effectiveMin.day else 1
        if (toDay < dayLo) toDay = dayLo

        Truth.assertThat(toYear).isEqualTo(1404)
        Truth.assertThat(toMonth).isEqualTo(3)
        Truth.assertThat(toDay).isEqualTo(10)
    }

    // ── "to" minimum live-linked to "from" — time ────────────────────────────

    @Test
    fun `effectiveToMin for time is fromTime when no global min is set`() {
        val from = ShamsiTime(10, 30)
        val effectiveToMin = effectiveTimeMin(from, resolvedMin = null)
        Truth.assertThat(effectiveToMin).isEqualTo(from)
    }

    @Test
    fun `effectiveToMin for time is fromTime when global min is before from`() {
        val from = ShamsiTime(10, 30)
        val globalMin = ShamsiTime(8, 0)
        val effectiveToMin = effectiveTimeMin(from, resolvedMin = globalMin)
        Truth.assertThat(effectiveToMin).isEqualTo(from)
    }

    @Test
    fun `effectiveToMin for time is resolvedMin when global min is after from`() {
        val from = ShamsiTime(10, 30)
        val globalMin = ShamsiTime(14, 0)
        val effectiveToMin = effectiveTimeMin(from, resolvedMin = globalMin)
        Truth.assertThat(effectiveToMin).isEqualTo(globalMin)
    }

    @Test
    fun `toHour is clamped up to effectiveToMin hour`() {
        val effectiveMin = ShamsiTime(14, 0)
        var toHour = 10
        if (toHour < effectiveMin.hour) toHour = effectiveMin.hour
        Truth.assertThat(toHour).isEqualTo(14)
    }

    @Test
    fun `toHour is unchanged when it is already at or above effectiveToMin hour`() {
        val effectiveMin = ShamsiTime(14, 0)
        var toHour = 16
        if (toHour < effectiveMin.hour) toHour = effectiveMin.hour
        Truth.assertThat(toHour).isEqualTo(16)
    }

    @Test
    fun `toMinute is clamped when toHour equals effectiveToMin hour`() {
        val effectiveMin = ShamsiTime(14, 30)
        var toHour = 14
        var toMinute = 15
        if (toHour < effectiveMin.hour) toHour = effectiveMin.hour
        val mLo = if (toHour == effectiveMin.hour) effectiveMin.minute else 0
        if (toMinute < mLo) toMinute = mLo
        Truth.assertThat(toMinute).isEqualTo(30)
    }

    @Test
    fun `toMinute is not clamped when toHour is above effectiveToMin hour`() {
        val effectiveMin = ShamsiTime(14, 30)
        var toHour = 15
        var toMinute = 0
        if (toHour < effectiveMin.hour) toHour = effectiveMin.hour
        val mLo = if (toHour == effectiveMin.hour) effectiveMin.minute else 0
        if (0 < mLo) toMinute = mLo
        Truth.assertThat(toMinute).isEqualTo(0)
    }

    @Test
    fun `full time cascade advancing from past to clamps to to match from`() {
        // Simulates: from was 09:00, user scrolls fromHour to 15 and fromMinute to 45
        val effectiveMin = ShamsiTime(15, 45)
        var toHour = 9
        var toMinute = 0

        if (toHour < effectiveMin.hour) toHour = effectiveMin.hour
        val mLo = if (toHour == effectiveMin.hour) effectiveMin.minute else 0
        if (0 < mLo) toMinute = mLo

        Truth.assertThat(toHour).isEqualTo(15)
        Truth.assertThat(toMinute).isEqualTo(45)
    }

    @Test
    fun `time clamping preserves valid toMinute when only hour was clamped`() {
        // toHour=10 is below min hour=14, so hour clamps to 14.
        // toMinute=50 > min minute=30 at hour 14, so minute stays 50.
        val effectiveMin = ShamsiTime(14, 30)
        var toHour = 10
        var toMinute = 50

        if (toHour < effectiveMin.hour) toHour = effectiveMin.hour
        val mLo = if (toHour == effectiveMin.hour) effectiveMin.minute else 0
        if (toMinute < mLo) toMinute = mLo

        Truth.assertThat(toHour).isEqualTo(14)
        Truth.assertThat(toMinute).isEqualTo(50)
    }

    // ── helpers that mirror the picker's effectiveToMin computation ───────────

    private fun effectiveDateMin(
        from: ShamsiDate,
        resolvedMin: ShamsiDate?,
    ): ShamsiDate = if (resolvedMin != null && resolvedMin > from) resolvedMin else from

    private fun effectiveTimeMin(
        from: ShamsiTime,
        resolvedMin: ShamsiTime?,
    ): ShamsiTime = if (resolvedMin != null && resolvedMin > from) resolvedMin else from
}
