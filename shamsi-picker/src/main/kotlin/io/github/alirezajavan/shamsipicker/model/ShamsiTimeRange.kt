package io.github.alirezajavan.shamsipicker.model

import kotlinx.serialization.Serializable

/** A time range on the clock. */
@Serializable
public data class ShamsiTimeRange(
    public val from: ShamsiTime,
    public val to: ShamsiTime,
)
