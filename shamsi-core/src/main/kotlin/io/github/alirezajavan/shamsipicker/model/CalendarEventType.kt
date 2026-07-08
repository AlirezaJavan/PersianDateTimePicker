package io.github.alirezajavan.shamsipicker.model

import kotlinx.serialization.Serializable

/**
 * Distinguishes how a [CalendarEvent] is presented on the calendar grid.
 *
 * - [Holiday] renders like a non-working day (same treatment as a weekend):
 *   the day number itself is drawn in the theme's holiday color.
 * - [Event] renders as a small colored marker under the day number, without
 *   implying the day is off — for reminders, anniversaries, or other
 *   non-holiday occasions.
 */
@Serializable
public enum class CalendarEventType {
    Holiday,
    Event,
}
