package io.github.alirezajavan.shamsipicker.model

/**
 * Configuration for [io.github.alirezajavan.shamsipicker.ui.ShamsiDateRangePickerDialog].
 *
 * All date parameters accept any [ShamsiDateLimit]:
 * - A [ShamsiDate] — a fixed Shamsi date, e.g. `ShamsiDate(1403, 1, 1)`
 * - [ShamsiDate.Now] or [ShamsiDateLimit.Now] — evaluates to the current date at open time
 * - `LocalDate.of(...).asLimit()` — a fixed Gregorian date converted to Shamsi
 * - `LocalDate.now().asLimit()` — the current Gregorian date converted to Shamsi
 */
public data class ShamsiDateRangePickerConfig(
    val initialFrom: ShamsiDateLimit = ShamsiDate.Now,
    val initialTo: ShamsiDateLimit = ShamsiDate.Now,
    val minDate: ShamsiDateLimit? = null,
    val maxDate: ShamsiDateLimit? = null,
    val style: ShamsiDatePickerStyle = ShamsiDatePickerStyle.Wheel,
)
