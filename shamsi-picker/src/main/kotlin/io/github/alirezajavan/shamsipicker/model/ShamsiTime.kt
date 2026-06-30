package io.github.alirezajavan.shamsipicker.model

import kotlinx.serialization.Serializable
import java.time.LocalTime

/**
 * A time on the clock (hour and minute).
 */
@Serializable
public data class ShamsiTime(
    public val hour: Int,
    public val minute: Int,
) : Comparable<ShamsiTime>,
    ShamsiTimeLimit {
    init {
        require(hour in 0..23) { "hour must be in 0..23 but was $hour" }
        require(minute in 0..59) { "minute must be in 0..59 but was $minute" }
    }

    /** Total minutes since the start of the day. */
    public val totalMinutes: Int get() = hour * 60 + minute

    public fun toLocalTime(): LocalTime = LocalTime.of(hour, minute)

    override fun toShamsiTime(): ShamsiTime = this

    override fun compareTo(other: ShamsiTime): Int = totalMinutes.compareTo(other.totalMinutes)

    public companion object {
        /** The current time in the system default time zone. */
        public val Now: ShamsiTime get() = now()

        /** The current time in the system default time zone. */
        public fun now(): ShamsiTime {
            val now = LocalTime.now()
            return ShamsiTime(now.hour, now.minute)
        }

        /** Converts a Gregorian [LocalTime] to [ShamsiTime]. */
        public fun fromLocalTime(time: LocalTime): ShamsiTime = ShamsiTime(time.hour, time.minute)
    }
}
