package io.github.alirezajavan.shamsipicker.ui.theme

import androidx.compose.ui.text.TextStyle

/**
 * Text styles used across the picker dialogs.
 *
 * Each field is a plain [TextStyle], so a custom font is applied the same way as
 * anywhere else in Compose — set [TextStyle.fontFamily] on the style you pass in.
 *
 * Build one with [ShamsiPickerDefaults.typography], overriding only the fields you need.
 */
public data class ShamsiPickerTypography(
    val titleStyle: TextStyle,
    val wheelItemStyle: TextStyle,
    val compactWheelItemStyle: TextStyle,
    val dayCellStyle: TextStyle,
    val weekdayLabelStyle: TextStyle,
    val navHeaderStyle: TextStyle,
    val segmentLabelStyle: TextStyle,
    val separatorStyle: TextStyle,
    val buttonTextStyle: TextStyle,
)
