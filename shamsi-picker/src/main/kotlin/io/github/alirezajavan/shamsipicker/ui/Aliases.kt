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

/** Alias for [ShamsiDatePickerDialog]. */
@Composable
public fun DatePickerDialog(
    onConfirm: (ShamsiDate) -> Unit,
    onDismiss: () -> Unit,
    config: ShamsiDatePickerConfig = ShamsiDatePickerConfig(),
) {
    ShamsiDatePickerDialog(onConfirm, onDismiss, config)
}

/** Alias for [ShamsiTimePickerDialog]. */
@Composable
public fun TimePickerDialog(
    onConfirm: (ShamsiTime) -> Unit,
    onDismiss: () -> Unit,
    config: ShamsiTimePickerConfig = ShamsiTimePickerConfig(),
) {
    ShamsiTimePickerDialog(onConfirm, onDismiss, config)
}

/** Alias for [ShamsiDateRangePickerDialog]. */
@Composable
public fun DateRangePickerDialog(
    onConfirm: (ShamsiDateRange) -> Unit,
    onDismiss: () -> Unit,
    config: ShamsiDateRangePickerConfig = ShamsiDateRangePickerConfig(),
) {
    ShamsiDateRangePickerDialog(onConfirm, onDismiss, config)
}

/** Alias for [ShamsiTimeRangePickerDialog]. */
@Composable
public fun TimeRangePickerDialog(
    onConfirm: (ShamsiTimeRange) -> Unit,
    onDismiss: () -> Unit,
    config: ShamsiTimeRangePickerConfig = ShamsiTimeRangePickerConfig(),
) {
    ShamsiTimeRangePickerDialog(onConfirm, onDismiss, config)
}
