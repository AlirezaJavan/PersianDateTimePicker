package io.github.alirezajavan.shamsipicker.ui.theme

/**
 * Layout constants shared by the wheel and calendar-grid picker UI.
 *
 * Kept internal: spacing/sizing is an implementation detail, not part of the
 * public theming surface (see [ShamsiPickerColors] / [ShamsiPickerTypography]
 * for the customization API).
 *
 * All values are raw magnitudes (dp/alpha/count) rather than `Dp`/`Color` instances:
 * `Int.dp` is an inline extension function, not a compile-time constant, so a `Dp`
 * property could never be declared `const val`. Callers append `.dp` at the use site.
 */
internal object ShamsiPickerDimens {
    // Wheel geometry
    const val WHEEL_ITEM_HEIGHT_DP: Int = 44
    const val WHEEL_DEFAULT_VISIBLE_COUNT: Int = 5
    const val WHEEL_INFINITE_LOOP_COUNT: Int = 2_000
    const val WHEEL_HIGHLIGHT_HORIZONTAL_INSET_DP: Int = 4
    const val WHEEL_HIGHLIGHT_CORNER_RADIUS_DP: Int = 12
    const val WHEEL_HIGHLIGHT_ALPHA: Float = 0.06f
    const val WHEEL_DIM_ALPHA: Float = 0.1f
    const val WHEEL_MIN_SCALE: Float = 0.72f
    const val WHEEL_MAX_ROTATION_DEGREES: Float = 26f
    const val WHEEL_CAMERA_DISTANCE_MULTIPLIER: Float = 10f

    // Range-wheel variants (compact 3-row wheels used by the from/to rows)
    const val RANGE_WHEEL_VISIBLE_COUNT: Int = 3
    const val RANGE_WHEEL_DIM_ALPHA: Float = 0.4f

    // Dialog chrome
    const val DIALOG_OUTER_PADDING_DP: Int = 24
    const val DIALOG_WIDTH_DP: Int = 340
    const val DIALOG_HEIGHT_INSET_DP: Int = 64
    const val DIALOG_CORNER_RADIUS_DP: Int = 28
    const val DIALOG_TONAL_ELEVATION_DP: Int = 0
    const val DIALOG_CONTENT_PADDING_DP: Int = 20
    const val DIALOG_HEADER_SPACING_DP: Int = 16
    const val DIALOG_BODY_SPACING_DP: Int = 16
    const val DIALOG_BUTTON_SPACING_DP: Int = 12
    const val DIALOG_BUTTON_CORNER_RADIUS_DP: Int = 14

    // Wheel column widths
    const val COMPACT_WHEEL_WIDTH_DP: Int = 64
    const val MONTH_WHEEL_WIDTH_DP: Int = 116
    const val YEAR_WHEEL_WIDTH_DP: Int = 86
    const val WIDE_WHEEL_WIDTH_DP: Int = 72
    const val SEPARATOR_PADDING_DP: Int = 4

    // Date wheel / calendar layout
    const val DATE_WHEEL_ROW_SPACING_DP: Int = 8
    const val CALENDAR_COLUMN_SPACING_DP: Int = 10
    const val CALENDAR_GRID_ROW_SPACING_DP: Int = 4
    const val DAY_CELL_PADDING_DP: Int = 2
    const val DAY_CELL_SIZE_DP: Int = 38
    const val DISABLED_CONTENT_ALPHA: Float = 0.22f

    // Compact calendar-grid variant (used when the calendar shares space with another
    // control, e.g. the time wheel in ShamsiDateTimePickerDialog)
    const val COMPACT_CALENDAR_COLUMN_SPACING_DP: Int = 4
    const val COMPACT_CALENDAR_GRID_ROW_SPACING_DP: Int = 1
    const val COMPACT_DAY_CELL_PADDING_DP: Int = 1
    const val COMPACT_DAY_CELL_SIZE_DP: Int = 26
    const val COMPACT_NAV_BUTTON_SIZE_DP: Int = 32

    // Style switcher
    const val SWITCHER_CORNER_RADIUS_DP: Int = 12
    const val SWITCHER_PADDING_DP: Int = 3
    const val SWITCHER_SEGMENT_GAP_DP: Int = 4
    const val SEGMENT_CORNER_RADIUS_DP: Int = 9
    const val SEGMENT_PADDING_HORIZONTAL_DP: Int = 20
    const val SEGMENT_PADDING_VERTICAL_DP: Int = 8

    // Range picker
    const val RANGE_ROW_SPACING_DP: Int = 2
    const val RANGE_DIVIDER_PADDING_DP: Int = 8
    const val RANGE_DAY_ROW_HEIGHT_DP: Int = WHEEL_ITEM_HEIGHT_DP
    const val RANGE_STRIP_ALPHA: Float = 0.15f
}
