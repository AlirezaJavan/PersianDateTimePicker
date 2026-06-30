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
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import io.github.alirezajavan.shamsipicker.R
import io.github.alirezajavan.shamsipicker.calendar.ShamsiCalendar
import io.github.alirezajavan.shamsipicker.format.PersianNumber
import io.github.alirezajavan.shamsipicker.model.ShamsiDate
import io.github.alirezajavan.shamsipicker.model.ShamsiDateLimit
import io.github.alirezajavan.shamsipicker.model.ShamsiDatePickerConfig
import io.github.alirezajavan.shamsipicker.model.ShamsiDatePickerStyle

/**
 * A fully Persian/Shamsi date picker dialog.
 *
 * Use [ShamsiDatePickerConfig] to set the initial date, optional date bounds, and display style.
 * All date fields in the config accept any [ShamsiDateLimit]:
 * - `ShamsiDate(1403, 1, 1)` — a fixed Shamsi date
 * - `ShamsiDate.Now` / `ShamsiDateLimit.Now` — today's date, resolved at open time
 * - `LocalDate.of(2024, 3, 20).asLimit()` — a Gregorian date converted to Shamsi
 * - `LocalDate.now().asLimit()` — today's Gregorian date, resolved at open time
 */
@Composable
public fun ShamsiDatePickerDialog(
    onConfirm: (ShamsiDate) -> Unit,
    onDismiss: () -> Unit,
    config: ShamsiDatePickerConfig = ShamsiDatePickerConfig(),
) {
    val initialDate = remember { config.initialDate.toShamsiDate() }
    val resolvedMin = remember(config.minDate) { config.minDate?.toShamsiDate() }
    val resolvedMax = remember(config.maxDate) { config.maxDate?.toShamsiDate() }

    var currentStyle by remember { mutableStateOf(config.style) }
    var year by remember {
        mutableIntStateOf(
            initialDate.year.coerceIn(
                ShamsiCalendar.YEAR_RANGE.first,
                ShamsiCalendar.YEAR_RANGE.last,
            ),
        )
    }
    var month by remember { mutableIntStateOf(initialDate.month) }
    var day by remember { mutableIntStateOf(initialDate.day) }

    val maxDay = ShamsiCalendar.monthLength(year, month)
    val days = ShamsiCalendar.dayBounds(year, month, maxDay, resolvedMin, resolvedMax)
    if (day > days.last) day = days.last
    if (day < days.first) day = days.first

    PickerDialogScaffold(
        title = stringResource(R.string.shamsi_date_picker_title),
        confirmText = stringResource(R.string.shamsi_date_picker_confirm),
        cancelText = stringResource(R.string.shamsi_date_picker_cancel),
        onCancel = onDismiss,
        onConfirm = { onConfirm(ShamsiDate(year, month, day, initialDate.hour, initialDate.minute)) },
        header = { StyleSwitcher(selected = currentStyle, onSelect = { currentStyle = it }) },
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
                    onYear = { year = it },
                    onMonth = { month = it },
                    onDay = { day = it },
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
                    onYear = {
                        year =
                            it.coerceIn(
                                ShamsiCalendar.YEAR_RANGE.first,
                                ShamsiCalendar.YEAR_RANGE.last,
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
private fun WheelDatePicker(
    year: Int,
    month: Int,
    day: Int,
    maxDay: Int,
    minDate: ShamsiDate?,
    maxDate: ShamsiDate?,
    onYear: (Int) -> Unit,
    onMonth: (Int) -> Unit,
    onDay: (Int) -> Unit,
) {
    val months = ShamsiCalendar.monthBounds(year, minDate, maxDate)
    val days = ShamsiCalendar.dayBounds(year, month, maxDay, minDate, maxDate)
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        key(maxDay) {
            WheelPicker(
                itemCount = maxDay,
                initialIndex = (day - 1).coerceIn(0, maxDay - 1),
                label = { PersianNumber.toPersianDigits((it + 1).toLong()) },
                onSelectedIndexChange = { onDay(it + 1) },
                enabledRange = (days.first - 1)..<days.last,
                modifier = Modifier.width(64.dp),
            )
        }
        WheelPicker(
            itemCount = 12,
            initialIndex = month - 1,
            label = { ShamsiCalendar.monthName(it + 1) },
            onSelectedIndexChange = { onMonth(it + 1) },
            enabledRange = (months.first - 1)..<months.last,
            modifier = Modifier.width(116.dp),
        )
        WheelPicker(
            itemCount = ShamsiCalendar.YEAR_RANGE.count(),
            initialIndex = year - ShamsiCalendar.YEAR_RANGE.first,
            label = { PersianNumber.toPersianDigits((ShamsiCalendar.YEAR_RANGE.first + it).toLong()) },
            onSelectedIndexChange = { onYear(ShamsiCalendar.YEAR_RANGE.first + it) },
            infinite = false,
            enabledRange = ShamsiCalendar.yearEnabledRange(ShamsiCalendar.YEAR_RANGE, minDate, maxDate),
            modifier = Modifier.width(86.dp),
        )
    }
}

@Composable
private fun CalendarDatePicker(
    year: Int,
    month: Int,
    day: Int,
    maxDay: Int,
    dayBounds: IntRange,
    minDate: ShamsiDate?,
    maxDate: ShamsiDate?,
    onYear: (Int) -> Unit,
    onMonth: (Int) -> Unit,
    onDay: (Int) -> Unit,
) {
    val afterMin = { y: Int, m: Int ->
        minDate == null ||
            ShamsiCalendar.dateKey(ShamsiDate(y, m, 1)) >=
            ShamsiCalendar.dateKey(ShamsiDate(minDate.year, minDate.month, 1))
    }
    val beforeMax = { y: Int, m: Int ->
        maxDate == null ||
            ShamsiCalendar.dateKey(ShamsiDate(y, m, 1)) <=
            ShamsiCalendar.dateKey(ShamsiDate(maxDate.year, maxDate.month, 1))
    }
    val prevMonth = if (month == 1) (year - 1) to 12 else year to (month - 1)
    val nextMonth = if (month == 12) (year + 1) to 1 else year to (month + 1)

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        NavRow(
            label = ShamsiCalendar.monthName(month),
            contentDescriptionPrev = stringResource(R.string.shamsi_date_picker_prev_month),
            contentDescriptionNext = stringResource(R.string.shamsi_date_picker_next_month),
            prevEnabled = afterMin(prevMonth.first, prevMonth.second),
            nextEnabled = beforeMax(nextMonth.first, nextMonth.second),
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
            label = PersianNumber.toPersianDigits(year.toLong()),
            contentDescriptionPrev = stringResource(R.string.shamsi_date_picker_prev_year),
            contentDescriptionNext = stringResource(R.string.shamsi_date_picker_next_year),
            prevEnabled = minDate == null || year - 1 >= minDate.year,
            nextEnabled = maxDate == null || year + 1 <= maxDate.year,
            onPrev = { onYear(year - 1) },
            onNext = { onYear(year + 1) },
        )

        Row(modifier = Modifier.fillMaxWidth()) {
            for (name in ShamsiCalendar.WEEKDAY_NAMES) {
                Text(
                    text = name.take(1),
                    modifier = Modifier.weight(1f),
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }

        val firstWeekday =
            ShamsiCalendar.WEEKDAY_NAMES.indexOf(ShamsiCalendar.weekdayName(ShamsiDate(year, month, 1)))
        val cells = firstWeekday + maxDay
        val rows = (cells + 6) / 7
        Column(verticalArrangement = Arrangement.spacedBy(4.dp), modifier = Modifier.fillMaxWidth()) {
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
    onClick: () -> Unit,
) {
    val background by animateColorAsState(
        if (selected) MaterialTheme.colorScheme.primary else Color.Transparent,
        label = "dayBackground",
    )
    Box(
        modifier =
            Modifier
                .padding(2.dp)
                .size(38.dp)
                .clip(CircleShape)
                .background(background)
                .clickable(enabled = enabled, onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = PersianNumber.toPersianDigits(day.toLong()),
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal,
            color =
                when {
                    selected -> MaterialTheme.colorScheme.onPrimary
                    !enabled -> MaterialTheme.colorScheme.onSurface.copy(alpha = 0.22f)
                    else -> MaterialTheme.colorScheme.onSurface
                },
        )
    }
}

@Composable
internal fun NavRow(
    label: String,
    contentDescriptionPrev: String,
    contentDescriptionNext: String,
    onPrev: () -> Unit,
    onNext: () -> Unit,
    prevEnabled: Boolean = true,
    nextEnabled: Boolean = true,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        IconButton(onClick = onPrev, enabled = prevEnabled) {
            Icon(Icons.AutoMirrored.Rounded.KeyboardArrowLeft, contentDescription = contentDescriptionPrev)
        }
        Text(
            text = label,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onSurface,
        )
        IconButton(onClick = onNext, enabled = nextEnabled) {
            Icon(Icons.AutoMirrored.Rounded.KeyboardArrowRight, contentDescription = contentDescriptionNext)
        }
    }
}

@Composable
internal fun StyleSwitcher(
    selected: ShamsiDatePickerStyle,
    onSelect: (ShamsiDatePickerStyle) -> Unit,
) {
    Surface(
        shape = RoundedCornerShape(12.dp),
        color = MaterialTheme.colorScheme.surfaceContainerHighest,
    ) {
        Row(modifier = Modifier.padding(3.dp)) {
            SegmentButton(
                text = stringResource(R.string.shamsi_date_picker_style_wheel),
                selected = selected == ShamsiDatePickerStyle.Wheel,
                onClick = { onSelect(ShamsiDatePickerStyle.Wheel) },
            )
            Spacer(Modifier.width(4.dp))
            SegmentButton(
                text = stringResource(R.string.shamsi_date_picker_style_calendar),
                selected = selected == ShamsiDatePickerStyle.Calendar,
                onClick = { onSelect(ShamsiDatePickerStyle.Calendar) },
            )
        }
    }
}

@Composable
private fun SegmentButton(
    text: String,
    selected: Boolean,
    onClick: () -> Unit,
) {
    val background by animateColorAsState(
        if (selected) MaterialTheme.colorScheme.primary else Color.Transparent,
        label = "segmentBackground",
    )
    Box(
        modifier =
            Modifier
                .clip(RoundedCornerShape(9.dp))
                .background(background)
                .clickable(onClick = onClick)
                .padding(horizontal = 20.dp, vertical = 8.dp),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelLarge,
            color =
                if (selected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}
