package io.github.alirezajavan.shamsipicker.model

/**
 * Configuration for [io.github.alirezajavan.shamsipicker.ui.ShamsiTimePickerDialog].
 *
 * All time parameters accept any [ShamsiTimeLimit]:
 * - A [ShamsiTime] — a fixed time, e.g. `ShamsiTime(8, 30)`
 * - [ShamsiTime.Now] or [ShamsiTimeLimit.Now] — evaluates to the current time at open time
 * - `LocalTime.of(...).asLimit()` — a fixed Gregorian time
 * - `LocalTime.now().asLimit()` — the current system time
 */
public data class ShamsiTimePickerConfig(
    val initialTime: ShamsiTimeLimit = ShamsiTime.Now,
    val minTime: ShamsiTimeLimit? = null,
    val maxTime: ShamsiTimeLimit? = null,
)
