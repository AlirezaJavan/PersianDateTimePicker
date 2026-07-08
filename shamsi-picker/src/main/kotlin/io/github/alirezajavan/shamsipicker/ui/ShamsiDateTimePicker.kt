package io.github.alirezajavan.shamsipicker.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import io.github.alirezajavan.shamsipicker.format.NumberFormatter
import io.github.alirezajavan.shamsipicker.model.ShamsiDate
import io.github.alirezajavan.shamsipicker.model.ShamsiDatePickerStyle
import io.github.alirezajavan.shamsipicker.model.ShamsiDateTimePickerConfig
import io.github.alirezajavan.shamsipicker.model.fromSystem
import io.github.alirezajavan.shamsipicker.model.toSystem
import io.github.alirezajavan.shamsipicker.ui.theme.ShamsiDateTimePickerStrings
import io.github.alirezajavan.shamsipicker.ui.theme.ShamsiPickerColors
import io.github.alirezajavan.shamsipicker.ui.theme.ShamsiPickerDefaults
import io.github.alirezajavan.shamsipicker.ui.theme.ShamsiPickerDimens
import io.github.alirezajavan.shamsipicker.ui.theme.ShamsiPickerTypography

/**
 * A combined date and time picker dialog.
 *
 * Shows both date and time wheels stacked in a single view for quick selection.
 */
@Composable
public fun ShamsiDateTimePickerDialog(
    onConfirm: (ShamsiDate) -> Unit,
    onDismiss: () -> Unit,
    config: ShamsiDateTimePickerConfig = ShamsiDateTimePickerConfig(),
    colors: ShamsiPickerColors = ShamsiPickerDefaults.colors(),
    typography: ShamsiPickerTypography = ShamsiPickerDefaults.typography(),
    strings: ShamsiDateTimePickerStrings = ShamsiPickerDefaults.dateTimeStrings(),
) {
    val calendarSystem = remember(config.calendarType) { config.calendarType.system }
    val initialDate =
        remember(config.initialDateTime, calendarSystem) {
            config.initialDateTime.toShamsiDate().toSystem(calendarSystem)
        }
    val resolvedMin =
        remember(config.minDateTime, calendarSystem) {
            config.minDateTime?.toShamsiDate()?.toSystem(calendarSystem)
        }
    val resolvedMax =
        remember(config.maxDateTime, calendarSystem) {
            config.maxDateTime?.toShamsiDate()?.toSystem(calendarSystem)
        }

    val numberFormatter = remember(config.calendarType) { NumberFormatter.get(config.calendarType) }
    val firstDayOfWeek =
        remember(config.firstDayOfWeek, calendarSystem) {
            config.firstDayOfWeek ?: calendarSystem.defaultFirstDayOfWeek
        }
    val wheelVisibleCount =
        if (config.compactWheel) {
            ShamsiPickerDimens.COMPACT_WHEEL_VISIBLE_COUNT
        } else {
            ShamsiPickerDimens.RANGE_WHEEL_VISIBLE_COUNT
        }

    var currentStyle by remember { mutableStateOf(config.style) }

    var year by remember {
        mutableIntStateOf(
            initialDate.year.coerceIn(
                calendarSystem.yearRange.first,
                calendarSystem.yearRange.last,
            ),
        )
    }
    var month by remember { mutableIntStateOf(initialDate.month) }
    var day by remember { mutableIntStateOf(initialDate.day) }
    var hour by remember { mutableIntStateOf(initialDate.hour) }
    var minute by remember { mutableIntStateOf(initialDate.minute) }

    val maxDay = calendarSystem.monthLength(year, month)
    val days = calendarSystem.dayBounds(year, month, maxDay, resolvedMin, resolvedMax)
    if (day > days.last) day = days.last
    if (day < days.first) day = days.first

    PickerDialogScaffold(
        title = strings.title,
        confirmText = strings.confirmText,
        cancelText = strings.cancelText,
        onCancel = onDismiss,
        onConfirm = {
            val result = ShamsiDate(year, month, day, hour, minute)
            onConfirm(result.fromSystem(calendarSystem))
        },
        colors = colors,
        typography = typography,
        header = {
            StyleSwitcher(
                selected = currentStyle,
                onSelect = { currentStyle = it },
                colors = colors,
                typography = typography,
                wheelLabel = strings.styleWheelLabel,
                calendarLabel = strings.styleCalendarLabel,
            )
        },
    ) {
        Column {
            when (currentStyle) {
                ShamsiDatePickerStyle.Wheel -> {
                    WheelDatePicker(
                        year = year,
                        month = month,
                        day = day,
                        maxDay = maxDay,
                        minDate = resolvedMin,
                        maxDate = resolvedMax,
                        calendarSystem = calendarSystem,
                        numberFormatter = numberFormatter,
                        colors = colors,
                        typography = typography,
                        onYear = { year = it },
                        onMonth = { month = it },
                        onDay = { day = it },
                        visibleCount = wheelVisibleCount,
                        dimAlpha = ShamsiPickerDimens.RANGE_WHEEL_DIM_ALPHA,
                    )
                }

                ShamsiDatePickerStyle.Calendar -> {
                    CalendarDatePicker(
                        year = year,
                        month = month,
                        day = day,
                        maxDay = maxDay,
                        dayBounds = days,
                        minDate = resolvedMin,
                        maxDate = resolvedMax,
                        calendarSystem = calendarSystem,
                        numberFormatter = numberFormatter,
                        firstDayOfWeek = firstDayOfWeek,
                        colors = colors,
                        typography = typography,
                        strings = strings.toDatePickerStrings(),
                        compact = config.compactCalendar,
                        onYear = {
                            year =
                                it.coerceIn(
                                    calendarSystem.yearRange.first,
                                    calendarSystem.yearRange.last,
                                )
                        },
                        onMonth = { month = it },
                        onDay = { day = it },
                    )
                }
            }

            HorizontalDivider(
                modifier = Modifier.padding(vertical = ShamsiPickerDimens.RANGE_DIVIDER_PADDING_DP.dp),
                color = colors.wheelHighlightColor,
            )

            TimePicker(
                hour = hour,
                minute = minute,
                initialHour = initialDate.hour,
                initialMinute = initialDate.minute,
                minTime = resolvedMin?.toTime(),
                maxTime = resolvedMax?.toTime(),
                numberFormatter = numberFormatter,
                colors = colors,
                typography = typography,
                amLabel = strings.amLabel,
                pmLabel = strings.pmLabel,
                onHourChange = { hour = it },
                onMinuteChange = { minute = it },
                visibleCount = wheelVisibleCount,
                dimAlpha = ShamsiPickerDimens.RANGE_WHEEL_DIM_ALPHA,
            )
        }
    }
}

private fun ShamsiDateTimePickerStrings.toDatePickerStrings() =
    io.github.alirezajavan.shamsipicker.ui.theme.ShamsiDatePickerStrings(
        title = title,
        confirmText = confirmText,
        cancelText = cancelText,
        styleWheelLabel = styleWheelLabel,
        styleCalendarLabel = styleCalendarLabel,
        prevMonthDescription = prevMonthDescription,
        nextMonthDescription = nextMonthDescription,
        prevYearDescription = prevYearDescription,
        nextYearDescription = nextYearDescription,
    )
