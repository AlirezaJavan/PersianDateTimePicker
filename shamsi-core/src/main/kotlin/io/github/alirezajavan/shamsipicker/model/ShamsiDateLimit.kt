package io.github.alirezajavan.shamsipicker.model

import io.github.alirezajavan.shamsipicker.calendar.ShamsiCalendar
import java.time.LocalDate
import java.time.LocalDateTime

/**
 * A limit for [ShamsiDate] selection (e.g. minDate/maxDate).
 * Can be a fixed Shamsi date, a Gregorian date/datetime, or dynamic "Now".
 */
public sealed interface ShamsiDateLimit {
    public fun toShamsiDate(): ShamsiDate

    public object Now : ShamsiDateLimit {
        override fun toShamsiDate(): ShamsiDate = ShamsiCalendar.now()
    }

    public companion object {
        public fun of(date: LocalDate): ShamsiDateLimit = Gregorian(date)

        public fun of(dateTime: LocalDateTime): ShamsiDateLimit = GregorianDateTime(dateTime)
    }

    private data class Gregorian(
        val date: LocalDate,
    ) : ShamsiDateLimit {
        override fun toShamsiDate(): ShamsiDate = ShamsiCalendar.fromGregorian(date)
    }

    private data class GregorianDateTime(
        val dateTime: LocalDateTime,
    ) : ShamsiDateLimit {
        override fun toShamsiDate(): ShamsiDate {
            val date = ShamsiCalendar.fromGregorian(dateTime.toLocalDate())
            return date.copy(hour = dateTime.hour, minute = dateTime.minute)
        }
    }
}

/** Converts a [LocalDate] to a [ShamsiDateLimit] for use in picker boundaries. */
public fun LocalDate.asLimit(): ShamsiDateLimit = ShamsiDateLimit.of(this)

/** Converts a [LocalDateTime] to a [ShamsiDateLimit] for use in picker boundaries. */
public fun LocalDateTime.asLimit(): ShamsiDateLimit = ShamsiDateLimit.of(this)
