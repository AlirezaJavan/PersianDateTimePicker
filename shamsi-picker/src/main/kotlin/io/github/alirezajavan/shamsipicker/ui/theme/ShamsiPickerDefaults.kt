package io.github.alirezajavan.shamsipicker.ui.theme

import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import io.github.alirezajavan.shamsipicker.R

/**
 * Default [ShamsiPickerColors], [ShamsiPickerTypography], and per-dialog
 * strings bundles, derived from [MaterialTheme] and the library's localized
 * string resources.
 *
 * Mirrors the `XxxDefaults.colors()` / `.typography()` pattern used across
 * Material3: call the factory and override only the parameters you need.
 */
public object ShamsiPickerDefaults {
    @Composable
    public fun colors(
        textColor: Color = MaterialTheme.colorScheme.onSurface,
        secondaryTextColor: Color = MaterialTheme.colorScheme.onSurfaceVariant,
        disabledTextColor: Color =
            MaterialTheme.colorScheme.onSurface.copy(alpha = ShamsiPickerDimens.DISABLED_CONTENT_ALPHA),
        accentColor: Color = MaterialTheme.colorScheme.primary,
        onAccentColor: Color = MaterialTheme.colorScheme.onPrimary,
        titleColor: Color = MaterialTheme.colorScheme.onSurface,
        wheelHighlightColor: Color =
            MaterialTheme.colorScheme.onSurface.copy(alpha = ShamsiPickerDimens.WHEEL_HIGHLIGHT_ALPHA),
        fadeColor: Color = MaterialTheme.colorScheme.surfaceContainerHigh,
        dialogContainerColor: Color = MaterialTheme.colorScheme.surfaceContainerHigh,
        switcherContainerColor: Color = MaterialTheme.colorScheme.surfaceContainerHighest,
        rangeStripColor: Color = MaterialTheme.colorScheme.primary.copy(alpha = ShamsiPickerDimens.RANGE_STRIP_ALPHA),
        confirmButtonColors: ButtonColors = ButtonDefaults.buttonColors(),
        cancelButtonColors: ButtonColors = ButtonDefaults.outlinedButtonColors(),
    ): ShamsiPickerColors =
        ShamsiPickerColors(
            textColor = textColor,
            secondaryTextColor = secondaryTextColor,
            disabledTextColor = disabledTextColor,
            accentColor = accentColor,
            onAccentColor = onAccentColor,
            titleColor = titleColor,
            wheelHighlightColor = wheelHighlightColor,
            fadeColor = fadeColor,
            dialogContainerColor = dialogContainerColor,
            switcherContainerColor = switcherContainerColor,
            rangeStripColor = rangeStripColor,
            confirmButtonColors = confirmButtonColors,
            cancelButtonColors = cancelButtonColors,
        )

    @Composable
    public fun typography(
        titleStyle: TextStyle = MaterialTheme.typography.titleLarge,
        wheelItemStyle: TextStyle = MaterialTheme.typography.titleLarge,
        compactWheelItemStyle: TextStyle = MaterialTheme.typography.titleMedium,
        dayCellStyle: TextStyle = MaterialTheme.typography.bodyLarge,
        weekdayLabelStyle: TextStyle = MaterialTheme.typography.labelMedium,
        navHeaderStyle: TextStyle = MaterialTheme.typography.titleMedium,
        segmentLabelStyle: TextStyle = MaterialTheme.typography.labelLarge,
        separatorStyle: TextStyle = MaterialTheme.typography.headlineSmall,
        buttonTextStyle: TextStyle = MaterialTheme.typography.labelLarge,
    ): ShamsiPickerTypography =
        ShamsiPickerTypography(
            titleStyle = titleStyle,
            wheelItemStyle = wheelItemStyle,
            compactWheelItemStyle = compactWheelItemStyle,
            dayCellStyle = dayCellStyle,
            weekdayLabelStyle = weekdayLabelStyle,
            navHeaderStyle = navHeaderStyle,
            segmentLabelStyle = segmentLabelStyle,
            separatorStyle = separatorStyle,
            buttonTextStyle = buttonTextStyle,
        )

    @Composable
    public fun dateStrings(
        title: String = stringResource(R.string.shamsi_date_picker_title),
        confirmText: String = stringResource(R.string.shamsi_date_picker_confirm),
        cancelText: String = stringResource(R.string.shamsi_date_picker_cancel),
        styleWheelLabel: String = stringResource(R.string.shamsi_date_picker_style_wheel),
        styleCalendarLabel: String = stringResource(R.string.shamsi_date_picker_style_calendar),
        prevMonthDescription: String = stringResource(R.string.shamsi_date_picker_prev_month),
        nextMonthDescription: String = stringResource(R.string.shamsi_date_picker_next_month),
        prevYearDescription: String = stringResource(R.string.shamsi_date_picker_prev_year),
        nextYearDescription: String = stringResource(R.string.shamsi_date_picker_next_year),
    ): ShamsiDatePickerStrings =
        ShamsiDatePickerStrings(
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

    @Composable
    public fun dateRangeStrings(
        title: String = stringResource(R.string.shamsi_date_range_picker_title),
        confirmText: String = stringResource(R.string.shamsi_date_picker_confirm),
        cancelText: String = stringResource(R.string.shamsi_date_picker_cancel),
        styleWheelLabel: String = stringResource(R.string.shamsi_date_picker_style_wheel),
        styleCalendarLabel: String = stringResource(R.string.shamsi_date_picker_style_calendar),
        prevMonthDescription: String = stringResource(R.string.shamsi_date_picker_prev_month),
        nextMonthDescription: String = stringResource(R.string.shamsi_date_picker_next_month),
        prevYearDescription: String = stringResource(R.string.shamsi_date_picker_prev_year),
        nextYearDescription: String = stringResource(R.string.shamsi_date_picker_next_year),
        selectToHint: String = stringResource(R.string.shamsi_date_range_picker_hint_select_to),
    ): ShamsiDateRangePickerStrings =
        ShamsiDateRangePickerStrings(
            title = title,
            confirmText = confirmText,
            cancelText = cancelText,
            styleWheelLabel = styleWheelLabel,
            styleCalendarLabel = styleCalendarLabel,
            prevMonthDescription = prevMonthDescription,
            nextMonthDescription = nextMonthDescription,
            prevYearDescription = prevYearDescription,
            nextYearDescription = nextYearDescription,
            selectToHint = selectToHint,
        )

    @Composable
    public fun timeStrings(
        title: String = stringResource(R.string.shamsi_time_picker_title),
        confirmText: String = stringResource(R.string.shamsi_time_picker_confirm),
        cancelText: String = stringResource(R.string.shamsi_time_picker_cancel),
        amLabel: String = stringResource(R.string.shamsi_time_am),
        pmLabel: String = stringResource(R.string.shamsi_time_pm),
    ): ShamsiTimePickerStrings =
        ShamsiTimePickerStrings(
            title = title,
            confirmText = confirmText,
            cancelText = cancelText,
            amLabel = amLabel,
            pmLabel = pmLabel,
        )

    @Composable
    public fun timeRangeStrings(
        title: String = stringResource(R.string.shamsi_time_range_picker_title),
        confirmText: String = stringResource(R.string.shamsi_time_picker_confirm),
        cancelText: String = stringResource(R.string.shamsi_time_picker_cancel),
        amLabel: String = stringResource(R.string.shamsi_time_am),
        pmLabel: String = stringResource(R.string.shamsi_time_pm),
    ): ShamsiTimeRangePickerStrings =
        ShamsiTimeRangePickerStrings(
            title = title,
            confirmText = confirmText,
            cancelText = cancelText,
            amLabel = amLabel,
            pmLabel = pmLabel,
        )

    @Composable
    public fun dateTimeStrings(
        title: String = stringResource(R.string.shamsi_date_time_picker_title),
        confirmText: String = stringResource(R.string.shamsi_date_picker_confirm),
        cancelText: String = stringResource(R.string.shamsi_date_picker_cancel),
        styleWheelLabel: String = stringResource(R.string.shamsi_date_picker_style_wheel),
        styleCalendarLabel: String = stringResource(R.string.shamsi_date_picker_style_calendar),
        prevMonthDescription: String = stringResource(R.string.shamsi_date_picker_prev_month),
        nextMonthDescription: String = stringResource(R.string.shamsi_date_picker_next_month),
        prevYearDescription: String = stringResource(R.string.shamsi_date_picker_prev_year),
        nextYearDescription: String = stringResource(R.string.shamsi_date_picker_next_year),
        amLabel: String = stringResource(R.string.shamsi_time_am),
        pmLabel: String = stringResource(R.string.shamsi_time_pm),
    ): ShamsiDateTimePickerStrings =
        ShamsiDateTimePickerStrings(
            title = title,
            confirmText = confirmText,
            cancelText = cancelText,
            styleWheelLabel = styleWheelLabel,
            styleCalendarLabel = styleCalendarLabel,
            prevMonthDescription = prevMonthDescription,
            nextMonthDescription = nextMonthDescription,
            prevYearDescription = prevYearDescription,
            nextYearDescription = nextYearDescription,
            amLabel = amLabel,
            pmLabel = pmLabel,
        )
}
