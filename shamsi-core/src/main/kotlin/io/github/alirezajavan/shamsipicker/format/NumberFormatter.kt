package io.github.alirezajavan.shamsipicker.format

import io.github.alirezajavan.shamsipicker.calendar.CalendarType

/**
 * Abstraction for formatting numbers into calendar-specific digits.
 */
public interface NumberFormatter {
    /** Formats the given [number] into a string with at least [minDigits]. */
    public fun format(
        number: Long,
        minDigits: Int = 1,
    ): String

    public companion object {
        /** Returns a [NumberFormatter] for the given [CalendarType]. */
        public fun get(type: CalendarType): NumberFormatter =
            when (type) {
                CalendarType.Shamsi -> PersianNumberFormatter
                CalendarType.Gregorian -> LatinNumberFormatter
            }
    }
}

internal object PersianNumberFormatter : NumberFormatter {
    override fun format(
        number: Long,
        minDigits: Int,
    ): String = PersianNumber.toPersianDigits(number.toString().padStart(minDigits, '0'))
}

internal object LatinNumberFormatter : NumberFormatter {
    override fun format(
        number: Long,
        minDigits: Int,
    ): String = number.toString().padStart(minDigits, '0')
}
