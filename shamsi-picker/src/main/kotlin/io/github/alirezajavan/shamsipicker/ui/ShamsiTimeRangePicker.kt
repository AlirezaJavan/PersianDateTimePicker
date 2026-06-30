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
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import io.github.alirezajavan.shamsipicker.R
import io.github.alirezajavan.shamsipicker.model.ShamsiTime
import io.github.alirezajavan.shamsipicker.model.ShamsiTimeRange
import io.github.alirezajavan.shamsipicker.model.ShamsiTimeRangePickerConfig

private const val RANGE_MINUTES_PER_DAY: Int = 24 * 60

/**
 * An iOS-style Shamsi time range picker dialog for selecting a "from" and "to" time.
 *
 * Two wheel pickers are shown stacked vertically — one labeled "از" (from) and one "تا" (to).
 * If the user confirms with "from" > "to", the values are automatically swapped.
 */
@Composable
public fun ShamsiTimeRangePickerDialog(
    onConfirm: (ShamsiTimeRange) -> Unit,
    onDismiss: () -> Unit,
    config: ShamsiTimeRangePickerConfig = ShamsiTimeRangePickerConfig(),
) {
    val initFrom = remember { config.initialFrom.toShamsiTime() }
    val initTo = remember { config.initialTo.toShamsiTime() }
    val resolvedMin = remember(config.minTime) { config.minTime?.toShamsiTime() }
    val resolvedMax = remember(config.maxTime) { config.maxTime?.toShamsiTime() }

    val context = LocalContext.current
    val is24h = DateFormat.is24HourFormat(context)

    var fromHour by remember { mutableIntStateOf(initFrom.hour) }
    var fromMinute by remember { mutableIntStateOf(initFrom.minute) }
    var toHour by remember { mutableIntStateOf(initTo.hour) }
    var toMinute by remember { mutableIntStateOf(initTo.minute) }

    PickerDialogScaffold(
        title = stringResource(R.string.shamsi_time_range_picker_title),
        confirmText = stringResource(R.string.shamsi_time_picker_confirm),
        cancelText = stringResource(R.string.shamsi_time_picker_cancel),
        onCancel = onDismiss,
        onConfirm = {
            val from = ShamsiTime(fromHour, fromMinute)
            val to = ShamsiTime(toHour, toMinute)
            if (from <= to) onConfirm(ShamsiTimeRange(from, to)) else onConfirm(ShamsiTimeRange(to, from))
        },
    ) {
        CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Ltr) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(4.dp),
            ) {
                TimeRangeLabel(stringResource(R.string.shamsi_time_range_picker_from))
                TimeWheelRow(
                    initialHour = initFrom.hour,
                    initialMinute = initFrom.minute,
                    is24h = is24h,
                    minTime = resolvedMin,
                    maxTime = resolvedMax,
                    onHourChange = { fromHour = it },
                    onMinuteChange = { fromMinute = it },
                )
                HorizontalDivider(modifier = Modifier.padding(vertical = 6.dp))
                TimeRangeLabel(stringResource(R.string.shamsi_time_range_picker_to))
                TimeWheelRow(
                    initialHour = initTo.hour,
                    initialMinute = initTo.minute,
                    is24h = is24h,
                    minTime = resolvedMin,
                    maxTime = resolvedMax,
                    onHourChange = { toHour = it },
                    onMinuteChange = { toMinute = it },
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
    onHourChange: (Int) -> Unit,
    onMinuteChange: (Int) -> Unit,
) {
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
                label = { PersianNumber.toPersianDigits(it.toString().padStart(2, '0')) },
                onSelectedIndexChange = { idx ->
                    hour24 = idx
                    onHourChange(idx)
                },
                enabledRange = hMin..hMax,
                visibleCount = 3,
                modifier = Modifier.width(72.dp),
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
                    PersianNumber.toPersianDigits(display.toString().padStart(2, '0'))
                },
                onSelectedIndexChange = { idx ->
                    hourIndex = idx
                    onHourChange(amPm * 12 + idx)
                },
                enabledRange = hourEnabled,
                visibleCount = 3,
                modifier = Modifier.width(64.dp),
            )
        }

        Text(
            text = ":",
            style = MaterialTheme.typography.headlineSmall,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.padding(horizontal = 4.dp),
        )

        WheelPicker(
            itemCount = 60,
            initialIndex = initialMinute,
            label = { PersianNumber.toPersianDigits(it.toString().padStart(2, '0')) },
            onSelectedIndexChange = { min ->
                minute = min
                onMinuteChange(min)
            },
            enabledRange = minuteRange,
            visibleCount = 3,
            modifier = Modifier.width(64.dp),
        )

        if (!is24h) {
            val am = stringResource(R.string.shamsi_time_am)
            val pm = stringResource(R.string.shamsi_time_pm)
            val amPmEnabled =
                when {
                    hMin <= 11 && hMax >= 12 -> 0..1
                    hMin <= 11 -> 0..0
                    else -> 1..1
                }
            Box(modifier = Modifier.width(72.dp)) {
                WheelPicker(
                    itemCount = 2,
                    initialIndex = initialAmPm,
                    label = { if (it == 0) am else pm },
                    onSelectedIndexChange = { idx ->
                        amPm = idx
                        onHourChange(idx * 12 + hourIndex)
                    },
                    infinite = false,
                    enabledRange = amPmEnabled,
                    visibleCount = 3,
                    textStyle = MaterialTheme.typography.titleMedium,
                )
            }
        }
    }
}

@Composable
private fun TimeRangeLabel(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.labelMedium,
        color = MaterialTheme.colorScheme.primary,
        modifier = Modifier.fillMaxWidth(),
        textAlign = TextAlign.Start,
    )
}
