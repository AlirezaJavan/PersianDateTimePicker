package io.github.alirezajavan.shamsipicker.ui

import android.text.format.DateFormat
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
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
import io.github.alirezajavan.shamsipicker.model.ShamsiTimeLimit
import io.github.alirezajavan.shamsipicker.model.ShamsiTimePickerConfig
import io.github.alirezajavan.shamsipicker.ui.theme.ShamsiPickerColors
import io.github.alirezajavan.shamsipicker.ui.theme.ShamsiPickerDefaults
import io.github.alirezajavan.shamsipicker.ui.theme.ShamsiPickerDimens
import io.github.alirezajavan.shamsipicker.ui.theme.ShamsiPickerTypography
import io.github.alirezajavan.shamsipicker.ui.theme.ShamsiTimePickerStrings

internal const val MINUTES_PER_DAY: Int = 24 * 60

/**
 * A time picker dialog supporting both Shamsi (Persian digits) and Gregorian (Latin digits).
 *
 * Use [ShamsiTimePickerConfig] to set the initial time, optional time bounds,
 * and calendar type. Use [colors], [typography], and [strings] to restyle or
 * re-word the dialog without forking it.
 */
@Composable
public fun ShamsiTimePickerDialog(
    onConfirm: (ShamsiTime) -> Unit,
    onDismiss: () -> Unit,
    config: ShamsiTimePickerConfig = ShamsiTimePickerConfig(),
    colors: ShamsiPickerColors = ShamsiPickerDefaults.colors(),
    typography: ShamsiPickerTypography = ShamsiPickerDefaults.typography(),
    strings: ShamsiTimePickerStrings = ShamsiPickerDefaults.timeStrings(),
) {
    val initialTime = remember { config.initialTime.toShamsiTime() }
    val resolvedMin = remember(config.minTime) { config.minTime?.toShamsiTime() }
    val resolvedMax = remember(config.maxTime) { config.maxTime?.toShamsiTime() }

    val numberFormatter = remember(config.calendarType) { NumberFormatter.get(config.calendarType) }

    var hour by remember { mutableIntStateOf(initialTime.hour) }
    var minute by remember { mutableIntStateOf(initialTime.minute) }

    PickerDialogScaffold(
        title = strings.title,
        confirmText = strings.confirmText,
        cancelText = strings.cancelText,
        onCancel = onDismiss,
        onConfirm = { onConfirm(ShamsiTime(hour, minute)) },
        colors = colors,
        typography = typography,
    ) {
        TimePicker(
            hour = hour,
            minute = minute,
            initialHour = initialTime.hour,
            initialMinute = initialTime.minute,
            minTime = resolvedMin,
            maxTime = resolvedMax,
            numberFormatter = numberFormatter,
            colors = colors,
            typography = typography,
            amLabel = strings.amLabel,
            pmLabel = strings.pmLabel,
            onHourChange = { hour = it },
            onMinuteChange = { minute = it },
            visibleCount =
                if (config.compactWheel) {
                    ShamsiPickerDimens.COMPACT_WHEEL_VISIBLE_COUNT
                } else {
                    ShamsiPickerDimens.WHEEL_DEFAULT_VISIBLE_COUNT
                },
        )
    }
}

@Composable
internal fun TimePicker(
    hour: Int,
    minute: Int,
    initialHour: Int,
    initialMinute: Int,
    minTime: ShamsiTime?,
    maxTime: ShamsiTime?,
    numberFormatter: NumberFormatter,
    colors: ShamsiPickerColors,
    typography: ShamsiPickerTypography,
    amLabel: String,
    pmLabel: String,
    onHourChange: (Int) -> Unit,
    onMinuteChange: (Int) -> Unit,
    visibleCount: Int = ShamsiPickerDimens.WHEEL_DEFAULT_VISIBLE_COUNT,
    dimAlpha: Float = ShamsiPickerDimens.WHEEL_DIM_ALPHA,
) {
    val context = LocalContext.current
    val is24h = DateFormat.is24HourFormat(context)

    val lo = (minTime?.totalMinutes ?: 0).coerceIn(0, MINUTES_PER_DAY - 1)
    val hi = (maxTime?.totalMinutes ?: (MINUTES_PER_DAY - 1)).coerceIn(lo, MINUTES_PER_DAY - 1)
    val hMin = lo / 60
    val hMax = hi / 60

    val initialAmPm = initialHour / 12
    val initialHourIndex = initialHour % 12

    var hourIndex by remember { mutableIntStateOf(initialHourIndex) }
    var amPm by remember { mutableIntStateOf(initialAmPm) }

    val currentHour24 = if (is24h) hour else amPm * 12 + hourIndex

    val minuteRange =
        if (currentHour24 in hMin..hMax) {
            val mLo = if (currentHour24 == hMin) lo % 60 else 0
            val mHi = if (currentHour24 == hMax) hi % 60 else 59
            mLo..mHi
        } else {
            0..59
        }

    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Ltr) {
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
                    onSelectedIndexChange = { onHourChange(it) },
                    enabledRange = hMin..hMax,
                    visibleCount = visibleCount,
                    dimAlpha = dimAlpha,
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
                    onSelectedIndexChange = {
                        hourIndex = it
                        onHourChange(amPm * 12 + it)
                    },
                    enabledRange = hourEnabled,
                    visibleCount = visibleCount,
                    dimAlpha = dimAlpha,
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
                onSelectedIndexChange = { onMinuteChange(it) },
                enabledRange = minuteRange,
                visibleCount = visibleCount,
                dimAlpha = dimAlpha,
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
                        label = { if (it == 0) amLabel else pmLabel },
                        onSelectedIndexChange = {
                            amPm = it
                            onHourChange(it * 12 + hourIndex)
                        },
                        infinite = false,
                        enabledRange = amPmEnabled,
                        visibleCount = visibleCount,
                        dimAlpha = dimAlpha,
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
}
