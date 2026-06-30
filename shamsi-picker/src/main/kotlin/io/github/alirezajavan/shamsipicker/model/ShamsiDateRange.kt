package io.github.alirezajavan.shamsipicker.model

import kotlinx.serialization.Serializable

/** A date range on the Shamsi (Jalali / Persian) calendar. */
@Serializable
public data class ShamsiDateRange(
    public val from: ShamsiDate,
    public val to: ShamsiDate,
)
