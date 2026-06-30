package io.github.alirezajavan.shamsipicker.model

import java.time.LocalTime

/**
 * A limit for [ShamsiTime] selection (e.g. minTime/maxTime).
 * Can be a fixed Shamsi time, a Gregorian time, or dynamic "Now".
 */
public sealed interface ShamsiTimeLimit {
    public fun toShamsiTime(): ShamsiTime

    public object Now : ShamsiTimeLimit {
        override fun toShamsiTime(): ShamsiTime = ShamsiTime.now()
    }

    public companion object {
        public fun of(time: LocalTime): ShamsiTimeLimit = Gregorian(time)
    }

    private data class Gregorian(
        val time: LocalTime,
    ) : ShamsiTimeLimit {
        override fun toShamsiTime(): ShamsiTime = ShamsiTime.fromLocalTime(time)
    }
}

/** Converts a [LocalTime] to a [ShamsiTimeLimit] for use in picker boundaries. */
public fun LocalTime.asLimit(): ShamsiTimeLimit = ShamsiTimeLimit.of(this)
