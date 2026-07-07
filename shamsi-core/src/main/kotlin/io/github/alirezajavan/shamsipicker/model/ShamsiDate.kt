package io.github.alirezajavan.shamsipicker.model

import io.github.alirezajavan.shamsipicker.calendar.CalendarSystem
import io.github.alirezajavan.shamsipicker.calendar.ShamsiCalendar
import io.github.alirezajavan.shamsipicker.calendar.ShamsiCalendarSystem
import kotlinx.serialization.Serializable

/**
 * A date and time on the Shamsi (Jalali / Persian) calendar.
 */
@Serializable
public data class ShamsiDate(
    public val year: Int,
    public val month: Int,
    public val day: Int,
    public val hour: Int = 0,
    public val minute: Int = 0,
) : Comparable<ShamsiDate>,
    ShamsiDateLimit {
    override fun toShamsiDate(): ShamsiDate = this

    /** The year/month this date belongs to, useful for monthly grouping and navigation. */
    public val monthKey: MonthKey get() = MonthKey(year, month)

    /** Extracts the time part as a [ShamsiTime]. */
    public fun toTime(): ShamsiTime = ShamsiTime(hour, minute)

    override fun compareTo(other: ShamsiDate): Int = COMPARATOR.compare(this, other)

    public companion object {
        /** The current Shamsi date in the system default time zone. */
        public val Now: ShamsiDate get() = ShamsiCalendar.now()

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

/**
 * Converts this Jalali date to a date representation in the given [system].
 *
 * This is used internally by the pickers to handle Gregorian mode while reusing
 * [ShamsiDate] as a data container.
 */
public fun ShamsiDate.toSystem(system: CalendarSystem): ShamsiDate {
    if (system is ShamsiCalendarSystem) return this
    val epochDay = ShamsiCalendarSystem.toEpochDay(year, month, day)
    val (y, m, d) = system.fromEpochDay(epochDay)
    return copy(year = y, month = m, day = d)
}

/**
 * Converts this date (interpreted in the given [system]) back to a Jalali date.
 */
public fun ShamsiDate.fromSystem(system: CalendarSystem): ShamsiDate {
    if (system is ShamsiCalendarSystem) return this
    val epochDay = system.toEpochDay(year, month, day)
    val (y, m, d) = ShamsiCalendarSystem.fromEpochDay(epochDay)
    return copy(year = y, month = m, day = d)
}
