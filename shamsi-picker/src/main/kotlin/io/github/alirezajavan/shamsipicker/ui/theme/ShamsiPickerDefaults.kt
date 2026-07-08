package io.github.alirezajavan.shamsipicker.ui.theme

import android.content.res.Configuration
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import io.github.alirezajavan.shamsipicker.R
import io.github.alirezajavan.shamsipicker.calendar.CalendarType
import java.util.Locale

/**
 * Default [ShamsiPickerColors], [ShamsiPickerTypography], and per-dialog
 * strings bundles, derived from [MaterialTheme] and the library's localized
 * string resources.
 *
 * Mirrors the `XxxDefaults.colors()` / `.typography()` pattern used across
 * Material3: call the factory and override only the parameters you need.
 */
public object ShamsiPickerDefaults {
    /**
     * Resolves a built-in string resource for [calendarType]'s natural language
     * (Persian for Shamsi, English for Gregorian) regardless of the device's
     * system locale — mirrors how month names and digit formatting already
     * follow `calendarType` instead of the device locale.
     */
    @Composable
    private fun localizedString(
        calendarType: CalendarType,
        id: Int,
    ): String {
        val context = LocalContext.current
        return remember(context, calendarType, id) {
            val locale = if (calendarType == CalendarType.Gregorian) Locale.ENGLISH else Locale("fa")
            val config = Configuration(context.resources.configuration)
            config.setLocale(locale)
            context.createConfigurationContext(config).resources.getString(id)
        }
    }

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
        calendarType: CalendarType = CalendarType.Shamsi,
        title: String = localizedString(calendarType, R.string.shamsi_date_picker_title),
        confirmText: String = localizedString(calendarType, R.string.shamsi_date_picker_confirm),
        cancelText: String = localizedString(calendarType, R.string.shamsi_date_picker_cancel),
        styleWheelLabel: String = localizedString(calendarType, R.string.shamsi_date_picker_style_wheel),
        styleCalendarLabel: String = localizedString(calendarType, R.string.shamsi_date_picker_style_calendar),
        prevMonthDescription: String = localizedString(calendarType, R.string.shamsi_date_picker_prev_month),
        nextMonthDescription: String = localizedString(calendarType, R.string.shamsi_date_picker_next_month),
        prevYearDescription: String = localizedString(calendarType, R.string.shamsi_date_picker_prev_year),
        nextYearDescription: String = localizedString(calendarType, R.string.shamsi_date_picker_next_year),
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
        calendarType: CalendarType = CalendarType.Shamsi,
        title: String = localizedString(calendarType, R.string.shamsi_date_range_picker_title),
        confirmText: String = localizedString(calendarType, R.string.shamsi_date_picker_confirm),
        cancelText: String = localizedString(calendarType, R.string.shamsi_date_picker_cancel),
        styleWheelLabel: String = localizedString(calendarType, R.string.shamsi_date_picker_style_wheel),
        styleCalendarLabel: String = localizedString(calendarType, R.string.shamsi_date_picker_style_calendar),
        prevMonthDescription: String = localizedString(calendarType, R.string.shamsi_date_picker_prev_month),
        nextMonthDescription: String = localizedString(calendarType, R.string.shamsi_date_picker_next_month),
        prevYearDescription: String = localizedString(calendarType, R.string.shamsi_date_picker_prev_year),
        nextYearDescription: String = localizedString(calendarType, R.string.shamsi_date_picker_next_year),
        selectToHint: String = localizedString(calendarType, R.string.shamsi_date_range_picker_hint_select_to),
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
        calendarType: CalendarType = CalendarType.Shamsi,
        title: String = localizedString(calendarType, R.string.shamsi_time_picker_title),
        confirmText: String = localizedString(calendarType, R.string.shamsi_time_picker_confirm),
        cancelText: String = localizedString(calendarType, R.string.shamsi_time_picker_cancel),
        amLabel: String = localizedString(calendarType, R.string.shamsi_time_am),
        pmLabel: String = localizedString(calendarType, R.string.shamsi_time_pm),
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
        calendarType: CalendarType = CalendarType.Shamsi,
        title: String = localizedString(calendarType, R.string.shamsi_time_range_picker_title),
        confirmText: String = localizedString(calendarType, R.string.shamsi_time_picker_confirm),
        cancelText: String = localizedString(calendarType, R.string.shamsi_time_picker_cancel),
        amLabel: String = localizedString(calendarType, R.string.shamsi_time_am),
        pmLabel: String = localizedString(calendarType, R.string.shamsi_time_pm),
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
        calendarType: CalendarType = CalendarType.Shamsi,
        title: String = localizedString(calendarType, R.string.shamsi_date_time_picker_title),
        confirmText: String = localizedString(calendarType, R.string.shamsi_date_picker_confirm),
        cancelText: String = localizedString(calendarType, R.string.shamsi_date_picker_cancel),
        styleWheelLabel: String = localizedString(calendarType, R.string.shamsi_date_picker_style_wheel),
        styleCalendarLabel: String = localizedString(calendarType, R.string.shamsi_date_picker_style_calendar),
        prevMonthDescription: String = localizedString(calendarType, R.string.shamsi_date_picker_prev_month),
        nextMonthDescription: String = localizedString(calendarType, R.string.shamsi_date_picker_next_month),
        prevYearDescription: String = localizedString(calendarType, R.string.shamsi_date_picker_prev_year),
        nextYearDescription: String = localizedString(calendarType, R.string.shamsi_date_picker_next_year),
        amLabel: String = localizedString(calendarType, R.string.shamsi_time_am),
        pmLabel: String = localizedString(calendarType, R.string.shamsi_time_pm),
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
