package io.github.alirezajavan.shamsipicker.ui

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.rounded.KeyboardArrowRight
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import io.github.alirezajavan.shamsipicker.calendar.CalendarSystem
import io.github.alirezajavan.shamsipicker.format.NumberFormatter
import io.github.alirezajavan.shamsipicker.model.ShamsiDate
import io.github.alirezajavan.shamsipicker.model.ShamsiDatePickerConfig
import io.github.alirezajavan.shamsipicker.model.ShamsiDatePickerStyle
import io.github.alirezajavan.shamsipicker.model.fromSystem
import io.github.alirezajavan.shamsipicker.model.toSystem
import io.github.alirezajavan.shamsipicker.ui.theme.ShamsiDatePickerStrings
import io.github.alirezajavan.shamsipicker.ui.theme.ShamsiPickerColors
import io.github.alirezajavan.shamsipicker.ui.theme.ShamsiPickerDefaults
import io.github.alirezajavan.shamsipicker.ui.theme.ShamsiPickerDimens
import io.github.alirezajavan.shamsipicker.ui.theme.ShamsiPickerTypography

/**
 * A date picker dialog supporting both Shamsi and Gregorian calendars.
 *
 * Use [ShamsiDatePickerConfig] to set the initial date, optional date bounds,
 * display style, and calendar type. Use [colors], [typography], and [strings] to
 * restyle or re-word the dialog without forking it.
 */
@Composable
public fun ShamsiDatePickerDialog(
    onConfirm: (ShamsiDate) -> Unit,
    onDismiss: () -> Unit,
    config: ShamsiDatePickerConfig = ShamsiDatePickerConfig(),
    colors: ShamsiPickerColors = ShamsiPickerDefaults.colors(),
    typography: ShamsiPickerTypography = ShamsiPickerDefaults.typography(),
    strings: ShamsiDatePickerStrings = ShamsiPickerDefaults.dateStrings(),
) {
    val calendarSystem = remember(config.calendarType) { config.calendarType.system }
    val initialDate =
        remember(config.initialDate, calendarSystem) {
            config.initialDate.toShamsiDate().toSystem(calendarSystem)
        }
    val resolvedMin =
        remember(config.minDate, calendarSystem) {
            config.minDate?.toShamsiDate()?.toSystem(calendarSystem)
        }
    val resolvedMax =
        remember(config.maxDate, calendarSystem) {
            config.maxDate?.toShamsiDate()?.toSystem(calendarSystem)
        }

    val numberFormatter = remember(config.calendarType) { NumberFormatter.get(config.calendarType) }
    val firstDayOfWeek =
        remember(config.firstDayOfWeek, calendarSystem) {
            config.firstDayOfWeek ?: calendarSystem.defaultFirstDayOfWeek
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
            val result = ShamsiDate(year, month, day, initialDate.hour, initialDate.minute)
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
                    visibleCount =
                        if (config.compactWheel) {
                            ShamsiPickerDimens.COMPACT_WHEEL_VISIBLE_COUNT
                        } else {
                            ShamsiPickerDimens.WHEEL_DEFAULT_VISIBLE_COUNT
                        },
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
                    strings = strings,
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
    }
}

@Composable
internal fun WheelDatePicker(
    year: Int,
    month: Int,
    day: Int,
    maxDay: Int,
    minDate: ShamsiDate?,
    maxDate: ShamsiDate?,
    calendarSystem: CalendarSystem,
    numberFormatter: NumberFormatter,
    colors: ShamsiPickerColors,
    typography: ShamsiPickerTypography,
    onYear: (Int) -> Unit,
    onMonth: (Int) -> Unit,
    onDay: (Int) -> Unit,
    visibleCount: Int = ShamsiPickerDimens.WHEEL_DEFAULT_VISIBLE_COUNT,
    dimAlpha: Float = ShamsiPickerDimens.WHEEL_DIM_ALPHA,
) {
    val months = calendarSystem.monthBounds(year, minDate, maxDate)
    val days = calendarSystem.dayBounds(year, month, maxDay, minDate, maxDate)
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(ShamsiPickerDimens.DATE_WHEEL_ROW_SPACING_DP.dp, Alignment.CenterHorizontally),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        key(maxDay) {
            WheelPicker(
                itemCount = maxDay,
                initialIndex = (day - 1).coerceIn(0, maxDay - 1),
                label = { numberFormatter.format((it + 1).toLong()) },
                onSelectedIndexChange = { onDay(it + 1) },
                enabledRange = (days.first - 1)..<days.last,
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
        WheelPicker(
            itemCount = 12,
            initialIndex = month - 1,
            label = { calendarSystem.monthNames(year)[it] },
            onSelectedIndexChange = { onMonth(it + 1) },
            enabledRange = (months.first - 1)..<months.last,
            visibleCount = visibleCount,
            dimAlpha = dimAlpha,
            textStyle = typography.wheelItemStyle,
            selectedColor = colors.textColor,
            unselectedColor = colors.secondaryTextColor,
            disabledColor = colors.disabledTextColor,
            fadeColor = colors.fadeColor,
            modifier = Modifier.width(ShamsiPickerDimens.MONTH_WHEEL_WIDTH_DP.dp),
        )
        WheelPicker(
            itemCount = calendarSystem.yearRange.count(),
            initialIndex = year - calendarSystem.yearRange.first,
            label = { numberFormatter.format((calendarSystem.yearRange.first + it).toLong()) },
            onSelectedIndexChange = { onYear(calendarSystem.yearRange.first + it) },
            infinite = false,
            enabledRange = calendarSystem.yearEnabledRange(minDate, maxDate),
            visibleCount = visibleCount,
            dimAlpha = dimAlpha,
            textStyle = typography.wheelItemStyle,
            selectedColor = colors.textColor,
            unselectedColor = colors.secondaryTextColor,
            disabledColor = colors.disabledTextColor,
            fadeColor = colors.fadeColor,
            modifier = Modifier.width(ShamsiPickerDimens.YEAR_WHEEL_WIDTH_DP.dp),
        )
    }
}

@Composable
internal fun CalendarDatePicker(
    year: Int,
    month: Int,
    day: Int,
    maxDay: Int,
    dayBounds: IntRange,
    minDate: ShamsiDate?,
    maxDate: ShamsiDate?,
    calendarSystem: CalendarSystem,
    numberFormatter: NumberFormatter,
    firstDayOfWeek: java.time.DayOfWeek,
    colors: ShamsiPickerColors,
    typography: ShamsiPickerTypography,
    strings: ShamsiDatePickerStrings,
    onYear: (Int) -> Unit,
    onMonth: (Int) -> Unit,
    onDay: (Int) -> Unit,
    compact: Boolean = false,
) {
    val afterMin = { y: Int, m: Int ->
        minDate == null ||
            (y * 100 + m) >= (minDate.year * 100 + minDate.month)
    }
    val beforeMax = { y: Int, m: Int ->
        maxDate == null ||
            (y * 100 + m) <= (maxDate.year * 100 + maxDate.month)
    }
    val prevMonth = if (month == 1) (year - 1) to 12 else year to (month - 1)
    val nextMonth = if (month == 12) (year + 1) to 1 else year to (month + 1)
    val columnSpacing =
        if (compact) {
            ShamsiPickerDimens.COMPACT_CALENDAR_COLUMN_SPACING_DP
        } else {
            ShamsiPickerDimens.CALENDAR_COLUMN_SPACING_DP
        }
    val gridRowSpacing =
        if (compact) {
            ShamsiPickerDimens.COMPACT_CALENDAR_GRID_ROW_SPACING_DP
        } else {
            ShamsiPickerDimens.CALENDAR_GRID_ROW_SPACING_DP
        }

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(columnSpacing.dp),
    ) {
        NavRow(
            label = calendarSystem.monthNames(year)[month - 1],
            contentDescriptionPrev = strings.prevMonthDescription,
            contentDescriptionNext = strings.nextMonthDescription,
            prevEnabled = afterMin(prevMonth.first, prevMonth.second),
            nextEnabled = beforeMax(nextMonth.first, nextMonth.second),
            colors = colors,
            typography = typography,
            compact = compact,
            onPrev = {
                onMonth(prevMonth.second)
                if (prevMonth.first != year) onYear(prevMonth.first)
            },
            onNext = {
                onMonth(nextMonth.second)
                if (nextMonth.first != year) onYear(nextMonth.first)
            },
        )
        NavRow(
            label = numberFormatter.format(year.toLong()),
            contentDescriptionPrev = strings.prevYearDescription,
            contentDescriptionNext = strings.nextYearDescription,
            prevEnabled = minDate == null || year - 1 >= minDate.year,
            nextEnabled = maxDate == null || year + 1 <= maxDate.year,
            colors = colors,
            typography = typography,
            compact = compact,
            onPrev = { onYear(year - 1) },
            onNext = { onYear(year + 1) },
        )

        Row(modifier = Modifier.fillMaxWidth()) {
            for (name in calendarSystem.weekdayNames(firstDayOfWeek)) {
                Text(
                    text = name.take(1),
                    modifier = Modifier.weight(1f),
                    textAlign = TextAlign.Center,
                    style = typography.weekdayLabelStyle,
                    color = colors.secondaryTextColor,
                )
            }
        }

        val firstWeekday = calendarSystem.firstWeekdayOfMonth(year, month, firstDayOfWeek)
        val cells = firstWeekday + maxDay
        val rows = (cells + 6) / 7
        Column(
            verticalArrangement = Arrangement.spacedBy(gridRowSpacing.dp),
            modifier = Modifier.fillMaxWidth(),
        ) {
            for (row in 0 until rows) {
                Row(modifier = Modifier.fillMaxWidth()) {
                    for (col in 0 until 7) {
                        val cellIndex = row * 7 + col
                        val dayNumber = cellIndex - firstWeekday + 1
                        Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.Center) {
                            if (dayNumber in 1..maxDay) {
                                DayCell(
                                    day = dayNumber,
                                    selected = dayNumber == day,
                                    enabled = dayNumber in dayBounds,
                                    numberFormatter = numberFormatter,
                                    colors = colors,
                                    typography = typography,
                                    compact = compact,
                                    onClick = { onDay(dayNumber) },
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun DayCell(
    day: Int,
    selected: Boolean,
    enabled: Boolean,
    numberFormatter: NumberFormatter,
    colors: ShamsiPickerColors,
    typography: ShamsiPickerTypography,
    onClick: () -> Unit,
    compact: Boolean = false,
) {
    val background by animateColorAsState(
        if (selected) colors.accentColor else Color.Transparent,
        label = "dayBackground",
    )
    val cellPadding = if (compact) ShamsiPickerDimens.COMPACT_DAY_CELL_PADDING_DP else ShamsiPickerDimens.DAY_CELL_PADDING_DP
    val cellSize = if (compact) ShamsiPickerDimens.COMPACT_DAY_CELL_SIZE_DP else ShamsiPickerDimens.DAY_CELL_SIZE_DP
    Box(
        modifier =
            Modifier
                .padding(cellPadding.dp)
                .size(cellSize.dp)
                .clip(CircleShape)
                .background(background)
                .clickable(enabled = enabled, onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = numberFormatter.format(day.toLong()),
            style = typography.dayCellStyle,
            fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal,
            color =
                when {
                    selected -> colors.onAccentColor
                    !enabled -> colors.disabledTextColor
                    else -> colors.textColor
                },
        )
    }
}

@Composable
internal fun NavRow(
    label: String,
    contentDescriptionPrev: String,
    contentDescriptionNext: String,
    colors: ShamsiPickerColors,
    typography: ShamsiPickerTypography,
    onPrev: () -> Unit,
    onNext: () -> Unit,
    prevEnabled: Boolean = true,
    nextEnabled: Boolean = true,
    compact: Boolean = false,
) {
    val buttonModifier =
        if (compact) Modifier.size(ShamsiPickerDimens.COMPACT_NAV_BUTTON_SIZE_DP.dp) else Modifier
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        IconButton(onClick = onPrev, enabled = prevEnabled, modifier = buttonModifier) {
            Icon(Icons.AutoMirrored.Rounded.KeyboardArrowLeft, contentDescription = contentDescriptionPrev)
        }
        Text(
            text = label,
            style = typography.navHeaderStyle,
            fontWeight = FontWeight.SemiBold,
            color = colors.textColor,
        )
        IconButton(onClick = onNext, enabled = nextEnabled, modifier = buttonModifier) {
            Icon(Icons.AutoMirrored.Rounded.KeyboardArrowRight, contentDescription = contentDescriptionNext)
        }
    }
}

@Composable
internal fun StyleSwitcher(
    selected: ShamsiDatePickerStyle,
    onSelect: (ShamsiDatePickerStyle) -> Unit,
    colors: ShamsiPickerColors,
    typography: ShamsiPickerTypography,
    wheelLabel: String,
    calendarLabel: String,
) {
    Surface(
        shape = RoundedCornerShape(ShamsiPickerDimens.SWITCHER_CORNER_RADIUS_DP.dp),
        color = colors.switcherContainerColor,
    ) {
        Row(modifier = Modifier.padding(ShamsiPickerDimens.SWITCHER_PADDING_DP.dp)) {
            SegmentButton(
                text = wheelLabel,
                selected = selected == ShamsiDatePickerStyle.Wheel,
                colors = colors,
                typography = typography,
                onClick = { onSelect(ShamsiDatePickerStyle.Wheel) },
            )
            Spacer(Modifier.width(ShamsiPickerDimens.SWITCHER_SEGMENT_GAP_DP.dp))
            SegmentButton(
                text = calendarLabel,
                selected = selected == ShamsiDatePickerStyle.Calendar,
                colors = colors,
                typography = typography,
                onClick = { onSelect(ShamsiDatePickerStyle.Calendar) },
            )
        }
    }
}

@Composable
internal fun SegmentButton(
    text: String,
    selected: Boolean,
    colors: ShamsiPickerColors,
    typography: ShamsiPickerTypography,
    onClick: () -> Unit,
) {
    val background by animateColorAsState(
        if (selected) colors.accentColor else Color.Transparent,
        label = "segmentBackground",
    )
    Box(
        modifier =
            Modifier
                .clip(RoundedCornerShape(ShamsiPickerDimens.SEGMENT_CORNER_RADIUS_DP.dp))
                .background(background)
                .clickable(onClick = onClick)
                .padding(
                    horizontal = ShamsiPickerDimens.SEGMENT_PADDING_HORIZONTAL_DP.dp,
                    vertical = ShamsiPickerDimens.SEGMENT_PADDING_VERTICAL_DP.dp,
                ),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = text,
            style = typography.segmentLabelStyle,
            color = if (selected) colors.onAccentColor else colors.secondaryTextColor,
        )
    }
}
