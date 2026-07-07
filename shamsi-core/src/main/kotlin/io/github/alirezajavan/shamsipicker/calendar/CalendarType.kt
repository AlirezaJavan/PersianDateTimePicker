package io.github.alirezajavan.shamsipicker.calendar

/**
 * Supported calendar types for the pickers.
 */
public enum class CalendarType {
    /** The Shamsi (Jalali / Persian) calendar. */
    Shamsi,

    /** The Gregorian calendar. */
    Gregorian,

    ;

    /** Returns the [CalendarSystem] implementation for this type. */
    public val system: CalendarSystem
        get() =
            when (this) {
                Shamsi -> ShamsiCalendarSystem
                Gregorian -> GregorianCalendarSystem
            }
}
