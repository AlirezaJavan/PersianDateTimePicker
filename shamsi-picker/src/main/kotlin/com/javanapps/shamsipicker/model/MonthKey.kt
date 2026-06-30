package com.javanapps.shamsipicker.model

import kotlinx.serialization.Serializable

/**
 * Identifies a single Shamsi month (a [year] and [month] 1..12).
 */
@Serializable
public data class MonthKey(
    val year: Int,
    val month: Int,
) : Comparable<MonthKey> {
    init {
        require(month in 1..12) { "Shamsi month must be in 1..12 but was $month" }
    }

    /** An absolute month ordinal, enabling arithmetic and ordering across year boundaries. */
    public val ordinal: Int get() = year * 12 + (month - 1)

    override fun compareTo(other: MonthKey): Int = ordinal.compareTo(other.ordinal)

    /** Returns the month [count] months after this one (negative moves backwards). */
    public fun plusMonths(count: Int): MonthKey {
        val total = ordinal + count
        return MonthKey(year = Math.floorDiv(total, 12), month = Math.floorMod(total, 12) + 1)
    }

    public fun next(): MonthKey = plusMonths(1)

    public fun previous(): MonthKey = plusMonths(-1)
}
