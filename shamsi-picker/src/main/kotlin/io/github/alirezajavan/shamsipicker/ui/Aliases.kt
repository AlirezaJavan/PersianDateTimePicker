package io.github.alirezajavan.shamsipicker.ui

import androidx.compose.runtime.Composable
import io.github.alirezajavan.shamsipicker.model.ShamsiDate
import io.github.alirezajavan.shamsipicker.model.ShamsiDatePickerConfig
import io.github.alirezajavan.shamsipicker.model.ShamsiDateRange
import io.github.alirezajavan.shamsipicker.model.ShamsiDateRangePickerConfig
import io.github.alirezajavan.shamsipicker.model.ShamsiTime
import io.github.alirezajavan.shamsipicker.model.ShamsiTimePickerConfig
import io.github.alirezajavan.shamsipicker.model.ShamsiTimeRange
import io.github.alirezajavan.shamsipicker.model.ShamsiTimeRangePickerConfig
import io.github.alirezajavan.shamsipicker.ui.theme.ShamsiDatePickerStrings
import io.github.alirezajavan.shamsipicker.ui.theme.ShamsiDateRangePickerStrings
import io.github.alirezajavan.shamsipicker.ui.theme.ShamsiPickerColors
import io.github.alirezajavan.shamsipicker.ui.theme.ShamsiPickerDefaults
import io.github.alirezajavan.shamsipicker.ui.theme.ShamsiPickerTypography
import io.github.alirezajavan.shamsipicker.ui.theme.ShamsiTimePickerStrings
import io.github.alirezajavan.shamsipicker.ui.theme.ShamsiTimeRangePickerStrings

/** Alias for [ShamsiDatePickerDialog]. */
@Composable
public fun DatePickerDialog(
    onConfirm: (ShamsiDate) -> Unit,
    onDismiss: () -> Unit,
    config: ShamsiDatePickerConfig = ShamsiDatePickerConfig(),
    colors: ShamsiPickerColors = ShamsiPickerDefaults.colors(),
    typography: ShamsiPickerTypography = ShamsiPickerDefaults.typography(),
    strings: ShamsiDatePickerStrings = ShamsiPickerDefaults.dateStrings(),
) {
    ShamsiDatePickerDialog(onConfirm, onDismiss, config, colors, typography, strings)
}

/** Alias for [ShamsiTimePickerDialog]. */
@Composable
public fun TimePickerDialog(
    onConfirm: (ShamsiTime) -> Unit,
    onDismiss: () -> Unit,
    config: ShamsiTimePickerConfig = ShamsiTimePickerConfig(),
    colors: ShamsiPickerColors = ShamsiPickerDefaults.colors(),
    typography: ShamsiPickerTypography = ShamsiPickerDefaults.typography(),
    strings: ShamsiTimePickerStrings = ShamsiPickerDefaults.timeStrings(),
) {
    ShamsiTimePickerDialog(onConfirm, onDismiss, config, colors, typography, strings)
}

/** Alias for [ShamsiDateRangePickerDialog]. */
@Composable
public fun DateRangePickerDialog(
    onConfirm: (ShamsiDateRange) -> Unit,
    onDismiss: () -> Unit,
    config: ShamsiDateRangePickerConfig = ShamsiDateRangePickerConfig(),
    colors: ShamsiPickerColors = ShamsiPickerDefaults.colors(),
    typography: ShamsiPickerTypography = ShamsiPickerDefaults.typography(),
    strings: ShamsiDateRangePickerStrings = ShamsiPickerDefaults.dateRangeStrings(),
) {
    ShamsiDateRangePickerDialog(onConfirm, onDismiss, config, colors, typography, strings)
}

/** Alias for [ShamsiTimeRangePickerDialog]. */
@Composable
public fun TimeRangePickerDialog(
    onConfirm: (ShamsiTimeRange) -> Unit,
    onDismiss: () -> Unit,
    config: ShamsiTimeRangePickerConfig = ShamsiTimeRangePickerConfig(),
    colors: ShamsiPickerColors = ShamsiPickerDefaults.colors(),
    typography: ShamsiPickerTypography = ShamsiPickerDefaults.typography(),
    strings: ShamsiTimeRangePickerStrings = ShamsiPickerDefaults.timeRangeStrings(),
) {
    ShamsiTimeRangePickerDialog(onConfirm, onDismiss, config, colors, typography, strings)
}
