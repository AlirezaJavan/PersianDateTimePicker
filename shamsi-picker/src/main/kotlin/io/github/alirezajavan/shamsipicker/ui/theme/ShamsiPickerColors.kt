package io.github.alirezajavan.shamsipicker.ui.theme

import androidx.compose.material3.ButtonColors
import androidx.compose.ui.graphics.Color

/**
 * Colors used across the picker dialogs (wheel, calendar grid, and dialog chrome).
 *
 * Build one with [ShamsiPickerDefaults.colors], overriding only the fields you need —
 * everything else falls back to the current [androidx.compose.material3.MaterialTheme].
 */
public data class ShamsiPickerColors(
    val textColor: Color,
    val secondaryTextColor: Color,
    val disabledTextColor: Color,
    val accentColor: Color,
    val onAccentColor: Color,
    val titleColor: Color,
    val wheelHighlightColor: Color,
    val fadeColor: Color,
    val dialogContainerColor: Color,
    val switcherContainerColor: Color,
    val rangeStripColor: Color,
    val confirmButtonColors: ButtonColors,
    val cancelButtonColors: ButtonColors,
)
