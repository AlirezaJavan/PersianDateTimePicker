package io.github.alirezajavan.shamsipicker.ui

import android.text.format.DateFormat
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import io.github.alirezajavan.shamsipicker.format.NumberFormatter
import io.github.alirezajavan.shamsipicker.model.ShamsiTime
import io.github.alirezajavan.shamsipicker.model.ShamsiTimeRange
import io.github.alirezajavan.shamsipicker.model.ShamsiTimeRangePickerConfig
import io.github.alirezajavan.shamsipicker.ui.theme.ShamsiPickerColors
import io.github.alirezajavan.shamsipicker.ui.theme.ShamsiPickerDefaults
import io.github.alirezajavan.shamsipicker.ui.theme.ShamsiPickerDimens
import io.github.alirezajavan.shamsipicker.ui.theme.ShamsiPickerTypography
import io.github.alirezajavan.shamsipicker.ui.theme.ShamsiTimeRangePickerStrings

private const val RANGE_MINUTES_PER_DAY: Int = 24 * 60

/**
 * A time range picker dialog supporting both Shamsi (Persian digits) and Gregorian (Latin digits).
 *
 * Two wheel pickers are shown stacked vertically — one labeled "از" (from) and one "تا" (to).
 * If the user confirms with "from" > "to", the values are automatically swapped. Use [colors],
 * [typography], and [strings] to restyle or re-word the dialog without forking it.
 */
@Composable
public fun ShamsiTimeRangePickerDialog(
    onConfirm: (ShamsiTimeRange) -> Unit,
    onDismiss: () -> Unit,
    config: ShamsiTimeRangePickerConfig = ShamsiTimeRangePickerConfig(),
    colors: ShamsiPickerColors = ShamsiPickerDefaults.colors(),
    typography: ShamsiPickerTypography = ShamsiPickerDefaults.typography(),
    strings: ShamsiTimeRangePickerStrings = ShamsiPickerDefaults.timeRangeStrings(calendarType = config.calendarType),
) {
    val initFrom = remember { config.initialFrom.toShamsiTime() }
    val initTo = remember { config.initialTo.toShamsiTime() }
    val resolvedMin = remember(config.minTime) { config.minTime?.toShamsiTime() }
    val resolvedMax = remember(config.maxTime) { config.maxTime?.toShamsiTime() }

    val numberFormatter = remember(config.calendarType) { NumberFormatter.get(config.calendarType) }

    val context = LocalContext.current
    val is24h = DateFormat.is24HourFormat(context)

    var fromHour by remember { mutableIntStateOf(initFrom.hour) }
    var fromMinute by remember { mutableIntStateOf(initFrom.minute) }
    var toHour by remember { mutableIntStateOf(initTo.hour) }
    var toMinute by remember { mutableIntStateOf(initTo.minute) }

    // "to" must always be >= "from": derive an effective minimum for the "to" wheel
    val fromAsTime = ShamsiTime(fromHour, fromMinute)
    val effectiveToMin: ShamsiTime =
        if (resolvedMin != null && resolvedMin > fromAsTime) resolvedMin else fromAsTime

    val toHMin = effectiveToMin.hour
    val toHMax = resolvedMax?.hour ?: 23
    if (toHour < toHMin) toHour = toHMin
    if (toHour > toHMax) toHour = toHMax
    val toMlo = if (toHour == toHMin) effectiveToMin.minute else 0
    val toMhi = if (resolvedMax != null && toHour == toHMax) resolvedMax.minute else 59
    if (toMinute < toMlo) toMinute = toMlo
    if (toMinute > toMhi) toMinute = toMhi

    PickerDialogScaffold(
        title = strings.title,
        confirmText = strings.confirmText,
        cancelText = strings.cancelText,
        onCancel = onDismiss,
        onConfirm = {
            val from = ShamsiTime(fromHour, fromMinute)
            val to = ShamsiTime(toHour, toMinute)
            if (from <= to) onConfirm(ShamsiTimeRange(from, to)) else onConfirm(ShamsiTimeRange(to, from))
        },
        colors = colors,
        typography = typography,
    ) {
        // Labels are in the device's natural RTL context so Persian text sits on the right.
        // Only the wheel rows are forced to LTR (to keep H:M column order consistent).
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(ShamsiPickerDimens.RANGE_ROW_SPACING_DP.dp),
        ) {
            CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Ltr) {
                TimeWheelRow(
                    initialHour = initFrom.hour,
                    initialMinute = initFrom.minute,
                    is24h = is24h,
                    minTime = resolvedMin,
                    maxTime = resolvedMax,
                    numberFormatter = numberFormatter,
                    colors = colors,
                    typography = typography,
                    strings = strings,
                    onHourChange = { fromHour = it },
                    onMinuteChange = { fromMinute = it },
                    compact = config.compactWheel,
                )
            }
            HorizontalDivider(modifier = Modifier.padding(vertical = ShamsiPickerDimens.RANGE_DIVIDER_PADDING_DP.dp))
            CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Ltr) {
                TimeWheelRow(
                    initialHour = initTo.hour,
                    initialMinute = initTo.minute,
                    is24h = is24h,
                    minTime = effectiveToMin,
                    maxTime = resolvedMax,
                    numberFormatter = numberFormatter,
                    colors = colors,
                    typography = typography,
                    strings = strings,
                    onHourChange = { toHour = it },
                    onMinuteChange = { toMinute = it },
                    compact = config.compactWheel,
                )
            }
        }
    }
}

@Composable
private fun TimeWheelRow(
    initialHour: Int,
    initialMinute: Int,
    is24h: Boolean,
    minTime: ShamsiTime?,
    maxTime: ShamsiTime?,
    numberFormatter: NumberFormatter,
    colors: ShamsiPickerColors,
    typography: ShamsiPickerTypography,
    strings: ShamsiTimeRangePickerStrings,
    onHourChange: (Int) -> Unit,
    onMinuteChange: (Int) -> Unit,
    compact: Boolean = false,
) {
    val wheelVisibleCount =
        if (compact) ShamsiPickerDimens.COMPACT_WHEEL_VISIBLE_COUNT else ShamsiPickerDimens.RANGE_WHEEL_VISIBLE_COUNT
    val lo = (minTime?.totalMinutes ?: 0).coerceIn(0, RANGE_MINUTES_PER_DAY - 1)
    val hi = (maxTime?.totalMinutes ?: (RANGE_MINUTES_PER_DAY - 1)).coerceIn(lo, RANGE_MINUTES_PER_DAY - 1)
    val hMin = lo / 60
    val hMax = hi / 60

    val initialAmPm = initialHour / 12
    val initialHourIndex = initialHour % 12

    var hour24 by remember { mutableIntStateOf(initialHour) }
    var hourIndex by remember { mutableIntStateOf(initialHourIndex) }
    var amPm by remember { mutableIntStateOf(initialAmPm) }
    var minute by remember { mutableIntStateOf(initialMinute) }

    val currentHour24 = if (is24h) hour24 else amPm * 12 + hourIndex

    val minuteRange =
        if (currentHour24 in hMin..hMax) {
            val mLo = if (currentHour24 == hMin) lo % 60 else 0
            val mHi = if (currentHour24 == hMax) hi % 60 else 59
            mLo..mHi
        } else {
            0..59
        }

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        if (is24h) {
            WheelPicker(
                itemCount = 24,
                initialIndex = initialHour,
                label = { numberFormatter.format(it.toLong(), minDigits = 2) },
                onSelectedIndexChange = { idx ->
                    hour24 = idx
                    onHourChange(idx)
                },
                enabledRange = hMin..hMax,
                visibleCount = wheelVisibleCount,
                dimAlpha = ShamsiPickerDimens.RANGE_WHEEL_DIM_ALPHA,
                textStyle = typography.wheelItemStyle,
                selectedColor = colors.textColor,
                unselectedColor = colors.secondaryTextColor,
                disabledColor = colors.disabledTextColor,
                fadeColor = colors.fadeColor,
                modifier = Modifier.width(ShamsiPickerDimens.WIDE_WHEEL_WIDTH_DP.dp),
            )
        } else {
            val a = (hMin - amPm * 12).coerceAtLeast(0)
            val b = (hMax - amPm * 12).coerceAtMost(11)
            val hourEnabled = if (a <= b) a..b else IntRange.EMPTY
            WheelPicker(
                itemCount = 12,
                initialIndex = initialHourIndex,
                label = {
                    val display = if (it == 0) 12 else it
                    numberFormatter.format(display.toLong(), minDigits = 2)
                },
                onSelectedIndexChange = { idx ->
                    hourIndex = idx
                    onHourChange(amPm * 12 + idx)
                },
                enabledRange = hourEnabled,
                visibleCount = wheelVisibleCount,
                dimAlpha = ShamsiPickerDimens.RANGE_WHEEL_DIM_ALPHA,
                textStyle = typography.wheelItemStyle,
                selectedColor = colors.textColor,
                unselectedColor = colors.secondaryTextColor,
                disabledColor = colors.disabledTextColor,
                fadeColor = colors.fadeColor,
                modifier = Modifier.width(ShamsiPickerDimens.COMPACT_WHEEL_WIDTH_DP.dp),
            )
        }

        Text(
            text = ":",
            style = typography.separatorStyle,
            color = colors.textColor,
            modifier = Modifier.padding(horizontal = ShamsiPickerDimens.SEPARATOR_PADDING_DP.dp),
        )

        WheelPicker(
            itemCount = 60,
            initialIndex = initialMinute,
            label = { numberFormatter.format(it.toLong(), minDigits = 2) },
            onSelectedIndexChange = { min ->
                minute = min
                onMinuteChange(min)
            },
            enabledRange = minuteRange,
            visibleCount = wheelVisibleCount,
            dimAlpha = ShamsiPickerDimens.RANGE_WHEEL_DIM_ALPHA,
            textStyle = typography.wheelItemStyle,
            selectedColor = colors.textColor,
            unselectedColor = colors.secondaryTextColor,
            disabledColor = colors.disabledTextColor,
            fadeColor = colors.fadeColor,
            modifier = Modifier.width(ShamsiPickerDimens.COMPACT_WHEEL_WIDTH_DP.dp),
        )

        if (!is24h) {
            val amPmEnabled =
                when {
                    hMin <= 11 && hMax >= 12 -> 0..1
                    hMin <= 11 -> 0..0
                    else -> 1..1
                }
            Box(modifier = Modifier.width(ShamsiPickerDimens.WIDE_WHEEL_WIDTH_DP.dp)) {
                WheelPicker(
                    itemCount = 2,
                    initialIndex = initialAmPm,
                    label = { if (it == 0) strings.amLabel else strings.pmLabel },
                    onSelectedIndexChange = { idx ->
                        amPm = idx
                        onHourChange(idx * 12 + hourIndex)
                    },
                    infinite = false,
                    enabledRange = amPmEnabled,
                    visibleCount = wheelVisibleCount,
                    dimAlpha = ShamsiPickerDimens.RANGE_WHEEL_DIM_ALPHA,
                    textStyle = typography.compactWheelItemStyle,
                    selectedColor = colors.textColor,
                    unselectedColor = colors.secondaryTextColor,
                    disabledColor = colors.disabledTextColor,
                    fadeColor = colors.fadeColor,
                )
            }
        }
    }
}
