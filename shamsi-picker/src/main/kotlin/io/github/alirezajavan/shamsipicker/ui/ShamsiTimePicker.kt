package io.github.alirezajavan.shamsipicker.ui

import android.text.format.DateFormat
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
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
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import io.github.alirezajavan.shamsipicker.R

private const val MINUTES_PER_DAY: Int = 24 * 60

/**
 * An iOS-style Shamsi time picker dialog.
 */
@Composable
public fun ShamsiTimePickerDialog(
    initialHour: Int,
    initialMinute: Int,
    onConfirm: (hour: Int, minute: Int) -> Unit,
    onDismiss: () -> Unit,
    minTotalMinutes: Int? = null,
    maxTotalMinutes: Int? = null,
) {
    val context = LocalContext.current
    val is24h = DateFormat.is24HourFormat(context)

    val lo = (minTotalMinutes ?: 0).coerceIn(0, MINUTES_PER_DAY - 1)
    val hi = (maxTotalMinutes ?: (MINUTES_PER_DAY - 1)).coerceIn(lo, MINUTES_PER_DAY - 1)
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

    PickerDialogScaffold(
        title = stringResource(R.string.shamsi_time_picker_title),
        confirmText = stringResource(R.string.shamsi_time_picker_confirm),
        cancelText = stringResource(R.string.shamsi_time_picker_cancel),
        onCancel = onDismiss,
        onConfirm = { onConfirm(currentHour24, minute) },
    ) {
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
                        label = { PersianNumber.toPersianDigits(it.toString().padStart(2, '0')) },
                        onSelectedIndexChange = { hour24 = it },
                        enabledRange = hMin..hMax,
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
                        onSelectedIndexChange = { hourIndex = it },
                        enabledRange = hourEnabled,
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
                    onSelectedIndexChange = { minute = it },
                    enabledRange = minuteRange,
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
                            onSelectedIndexChange = { amPm = it },
                            infinite = false,
                            enabledRange = amPmEnabled,
                            textStyle = MaterialTheme.typography.titleMedium,
                        )
                    }
                }
            }
        }
    }
}
