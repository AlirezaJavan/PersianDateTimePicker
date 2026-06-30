package io.github.alirezajavan.shamsipicker.model

import io.github.alirezajavan.shamsipicker.calendar.ShamsiCalendar
import java.time.LocalDate

/**
 * A limit for [ShamsiDate] selection (e.g. minDate/maxDate).
 * Can be a fixed Shamsi date, a Gregorian date, or dynamic "Now".
 */
public sealed interface ShamsiDateLimit {
    public fun toShamsiDate(): ShamsiDate

    public object Now : ShamsiDateLimit {
        override fun toShamsiDate(): ShamsiDate = ShamsiCalendar.now()
    }

    public companion object {
        public fun of(date: LocalDate): ShamsiDateLimit = Gregorian(date)
    }

    private data class Gregorian(
        val date: LocalDate,
    ) : ShamsiDateLimit {
        override fun toShamsiDate(): ShamsiDate = ShamsiCalendar.fromGregorian(date)
    }
}

/** Converts a [LocalDate] to a [ShamsiDateLimit] for use in picker boundaries. */
public fun LocalDate.asLimit(): ShamsiDateLimit = ShamsiDateLimit.of(this)
