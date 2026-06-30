package io.github.alirezajavan.shamsipicker.model

import kotlinx.serialization.Serializable

/**
 * A date and time on the Shamsi (Jalali / Persian) calendar.
 */
@Serializable
public data class ShamsiDate(
    val year: Int,
    val month: Int,
    val day: Int,
    val hour: Int = 0,
    val minute: Int = 0,
) : Comparable<ShamsiDate> {
    /** The year/month this date belongs to, useful for monthly grouping and navigation. */
    public val monthKey: MonthKey get() = MonthKey(year, month)

    override fun compareTo(other: ShamsiDate): Int = COMPARATOR.compare(this, other)

    public companion object {
        private val COMPARATOR =
            compareBy<ShamsiDate>(
                { it.year },
                { it.month },
                { it.day },
                { it.hour },
                { it.minute },
            )
    }
}
