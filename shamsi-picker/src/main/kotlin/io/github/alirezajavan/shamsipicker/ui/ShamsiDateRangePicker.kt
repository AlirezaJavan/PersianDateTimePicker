package io.github.alirezajavan.shamsipicker.ui

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
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
import io.github.alirezajavan.shamsipicker.model.ShamsiDate
import io.github.alirezajavan.shamsipicker.model.ShamsiDatePickerStyle
import io.github.alirezajavan.shamsipicker.model.ShamsiDateRange
import io.github.alirezajavan.shamsipicker.model.ShamsiDateRangePickerConfig

private fun ShamsiDate.dateKey(): Int = year * 10_000 + month * 100 + day

/**
 * A fully Persian/Shamsi date range picker dialog for selecting a "from" and "to" date.
 *
 * In [ShamsiDatePickerStyle.Calendar] mode the user first taps to set the start date, then taps
 * again to set the end date. The selected range is highlighted in the grid. Tapping once more
 * starts a new selection. In [ShamsiDatePickerStyle.Wheel] mode two compact wheel drums are
 * displayed — one for "از" (from) and one for "تا" (to).
 */
@Composable
public fun ShamsiDateRangePickerDialog(
    onConfirm: (ShamsiDateRange) -> Unit,
    onDismiss: () -> Unit,
    config: ShamsiDateRangePickerConfig = ShamsiDateRangePickerConfig(),
) {
    val (initFrom, initTo) =
        remember {
            val f = config.initialFrom.toShamsiDate()
            val t = config.initialTo.toShamsiDate()
            if (f <= t) f to t else t to f
        }
    val resolvedMin = remember(config.minDate) { config.minDate?.toShamsiDate() }
    val resolvedMax = remember(config.maxDate) { config.maxDate?.toShamsiDate() }

    var currentStyle by remember { mutableStateOf(config.style) }

    // Wheel mode state
    var fromYear by remember { mutableIntStateOf(initFrom.year.coerceIn(ShamsiYearRange)) }
    var fromMonth by remember { mutableIntStateOf(initFrom.month) }
    var fromDay by remember { mutableIntStateOf(initFrom.day) }
    var toYear by remember { mutableIntStateOf(initTo.year.coerceIn(ShamsiYearRange)) }
    var toMonth by remember { mutableIntStateOf(initTo.month) }
    var toDay by remember { mutableIntStateOf(initTo.day) }

    // Clamp days to valid range whenever month/year changes (same pattern as ShamsiDatePickerDialog)
    val fromMaxDay = ShamsiCalendar.monthLength(fromYear, fromMonth)
    val fromDayBounds = dayBounds(fromYear, fromMonth, fromMaxDay, resolvedMin, resolvedMax)
    if (fromDay > fromDayBounds.last) fromDay = fromDayBounds.last
    if (fromDay < fromDayBounds.first) fromDay = fromDayBounds.first

    val toMaxDay = ShamsiCalendar.monthLength(toYear, toMonth)
    val toDayBounds = dayBounds(toYear, toMonth, toMaxDay, resolvedMin, resolvedMax)
    if (toDay > toDayBounds.last) toDay = toDayBounds.last
    if (toDay < toDayBounds.first) toDay = toDayBounds.first

    // Calendar mode state
    // fromDate is always set; toDate==null means the user is currently picking "to"
    var fromDate by remember { mutableStateOf(initFrom) }
    var toDate by remember { mutableStateOf<ShamsiDate?>(initTo) }
    var viewYear by remember { mutableIntStateOf(initFrom.year) }
    var viewMonth by remember { mutableIntStateOf(initFrom.month) }

    fun buildResult(): ShamsiDateRange =
        when (currentStyle) {
            ShamsiDatePickerStyle.Wheel -> {
                val from = ShamsiDate(fromYear, fromMonth, fromDay, initFrom.hour, initFrom.minute)
                val to = ShamsiDate(toYear, toMonth, toDay, initTo.hour, initTo.minute)
                if (from <= to) ShamsiDateRange(from, to) else ShamsiDateRange(to, from)
            }

            ShamsiDatePickerStyle.Calendar -> {
                val effectiveTo = toDate ?: fromDate
                val from = fromDate.copy(hour = initFrom.hour, minute = initFrom.minute)
                val to = effectiveTo.copy(hour = initTo.hour, minute = initTo.minute)
                ShamsiDateRange(from, to)
            }
        }

    PickerDialogScaffold(
        title = stringResource(R.string.shamsi_date_range_picker_title),
        confirmText = stringResource(R.string.shamsi_date_picker_confirm),
        cancelText = stringResource(R.string.shamsi_date_picker_cancel),
        onCancel = onDismiss,
        onConfirm = { onConfirm(buildResult()) },
        header = { StyleSwitcher(selected = currentStyle, onSelect = { currentStyle = it }) },
    ) {
        when (currentStyle) {
            ShamsiDatePickerStyle.Wheel -> {
                WheelDateRangePicker(
                    fromYear = fromYear,
                    fromMonth = fromMonth,
                    fromDay = fromDay,
                    fromMaxDay = fromMaxDay,
                    fromDayBounds = fromDayBounds,
                    toYear = toYear,
                    toMonth = toMonth,
                    toDay = toDay,
                    toMaxDay = toMaxDay,
                    toDayBounds = toDayBounds,
                    minDate = resolvedMin,
                    maxDate = resolvedMax,
                    onFromYear = { fromYear = it },
                    onFromMonth = { fromMonth = it },
                    onFromDay = { fromDay = it },
                    onToYear = { toYear = it },
                    onToMonth = { toMonth = it },
                    onToDay = { toDay = it },
                )
            }

            ShamsiDatePickerStyle.Calendar -> {
                CalendarDateRangePicker(
                    fromDate = fromDate,
                    toDate = toDate,
                    viewYear = viewYear,
                    viewMonth = viewMonth,
                    minDate = resolvedMin,
                    maxDate = resolvedMax,
                    onDayTap = { day ->
                        val tapped = ShamsiDate(viewYear, viewMonth, day)
                        if (toDate != null) {
                            // Both were already set — start a new selection
                            fromDate = tapped
                            toDate = null
                        } else {
                            // Setting the end date
                            if (tapped < fromDate) {
                                toDate = fromDate
                                fromDate = tapped
                            } else {
                                toDate = tapped
                            }
                        }
                    },
                    onViewYear = { viewYear = it.coerceIn(ShamsiYearRange.first, ShamsiYearRange.last) },
                    onViewMonth = { viewMonth = it },
                )
            }
        }
    }
}

@Composable
private fun WheelDateRangePicker(
    fromYear: Int,
    fromMonth: Int,
    fromDay: Int,
    fromMaxDay: Int,
    fromDayBounds: IntRange,
    toYear: Int,
    toMonth: Int,
    toDay: Int,
    toMaxDay: Int,
    toDayBounds: IntRange,
    minDate: ShamsiDate?,
    maxDate: ShamsiDate?,
    onFromYear: (Int) -> Unit,
    onFromMonth: (Int) -> Unit,
    onFromDay: (Int) -> Unit,
    onToYear: (Int) -> Unit,
    onToMonth: (Int) -> Unit,
    onToDay: (Int) -> Unit,
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        RangeRowLabel(stringResource(R.string.shamsi_date_range_picker_from))
        CompactWheelDateRow(
            year = fromYear,
            month = fromMonth,
            day = fromDay,
            maxDay = fromMaxDay,
            dayBounds = fromDayBounds,
            minDate = minDate,
            maxDate = maxDate,
            onYear = onFromYear,
            onMonth = onFromMonth,
            onDay = onFromDay,
        )
        HorizontalDivider(modifier = Modifier.padding(vertical = 6.dp))
        RangeRowLabel(stringResource(R.string.shamsi_date_range_picker_to))
        CompactWheelDateRow(
            year = toYear,
            month = toMonth,
            day = toDay,
            maxDay = toMaxDay,
            dayBounds = toDayBounds,
            minDate = minDate,
            maxDate = maxDate,
            onYear = onToYear,
            onMonth = onToMonth,
            onDay = onToDay,
        )
    }
}

@Composable
private fun CompactWheelDateRow(
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
    val months = monthBounds(year, minDate, maxDate)
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
                enabledRange = (dayBounds.first - 1)..<dayBounds.last,
                visibleCount = 3,
                modifier = Modifier.width(64.dp),
            )
        }
        WheelPicker(
            itemCount = 12,
            initialIndex = month - 1,
            label = { ShamsiCalendar.monthName(it + 1) },
            onSelectedIndexChange = { onMonth(it + 1) },
            enabledRange = (months.first - 1)..<months.last,
            visibleCount = 3,
            modifier = Modifier.width(116.dp),
        )
        WheelPicker(
            itemCount = ShamsiYearRange.count(),
            initialIndex = year - ShamsiYearRange.first,
            label = { PersianNumber.toPersianDigits((ShamsiYearRange.first + it).toLong()) },
            onSelectedIndexChange = { onYear(ShamsiYearRange.first + it) },
            infinite = false,
            enabledRange = yearEnabledRange(ShamsiYearRange, minDate, maxDate),
            visibleCount = 3,
            modifier = Modifier.width(86.dp),
        )
    }
}

@Composable
private fun CalendarDateRangePicker(
    fromDate: ShamsiDate,
    toDate: ShamsiDate?,
    viewYear: Int,
    viewMonth: Int,
    minDate: ShamsiDate?,
    maxDate: ShamsiDate?,
    onDayTap: (Int) -> Unit,
    onViewYear: (Int) -> Unit,
    onViewMonth: (Int) -> Unit,
) {
    val maxDay = ShamsiCalendar.monthLength(viewYear, viewMonth)
    val days = dayBounds(viewYear, viewMonth, maxDay, minDate, maxDate)

    val afterMin = { y: Int, m: Int ->
        minDate == null ||
            ShamsiDate(y, m, 1).dateKey() >= ShamsiDate(minDate.year, minDate.month, 1).dateKey()
    }
    val beforeMax = { y: Int, m: Int ->
        maxDate == null ||
            ShamsiDate(y, m, 1).dateKey() <= ShamsiDate(maxDate.year, maxDate.month, 1).dateKey()
    }
    val prevMonth = if (viewMonth == 1) (viewYear - 1) to 12 else viewYear to (viewMonth - 1)
    val nextMonth = if (viewMonth == 12) (viewYear + 1) to 1 else viewYear to (viewMonth + 1)

    val fromKey = fromDate.dateKey()
    val toKey = toDate?.dateKey()

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        NavRow(
            label = ShamsiCalendar.monthName(viewMonth),
            contentDescriptionPrev = stringResource(R.string.shamsi_date_picker_prev_month),
            contentDescriptionNext = stringResource(R.string.shamsi_date_picker_next_month),
            prevEnabled = afterMin(prevMonth.first, prevMonth.second),
            nextEnabled = beforeMax(nextMonth.first, nextMonth.second),
            onPrev = {
                onViewMonth(prevMonth.second)
                if (prevMonth.first != viewYear) onViewYear(prevMonth.first)
            },
            onNext = {
                onViewMonth(nextMonth.second)
                if (nextMonth.first != viewYear) onViewYear(nextMonth.first)
            },
        )
        NavRow(
            label = PersianNumber.toPersianDigits(viewYear.toLong()),
            contentDescriptionPrev = stringResource(R.string.shamsi_date_picker_prev_year),
            contentDescriptionNext = stringResource(R.string.shamsi_date_picker_next_year),
            prevEnabled = minDate == null || viewYear - 1 >= minDate.year,
            nextEnabled = maxDate == null || viewYear + 1 <= maxDate.year,
            onPrev = { onViewYear(viewYear - 1) },
            onNext = { onViewYear(viewYear + 1) },
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
            ShamsiCalendar.WEEKDAY_NAMES.indexOf(ShamsiCalendar.weekdayName(ShamsiDate(viewYear, viewMonth, 1)))
        val cells = firstWeekday + maxDay
        val rows = (cells + 6) / 7
        Column(verticalArrangement = Arrangement.spacedBy(4.dp), modifier = Modifier.fillMaxWidth()) {
            for (row in 0 until rows) {
                Row(modifier = Modifier.fillMaxWidth()) {
                    for (col in 0 until 7) {
                        val dayNumber = row * 7 + col - firstWeekday + 1
                        Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.Center) {
                            if (dayNumber in 1..maxDay) {
                                val cellKey = ShamsiDate(viewYear, viewMonth, dayNumber).dateKey()
                                RangeDayCell(
                                    day = dayNumber,
                                    isFrom = cellKey == fromKey,
                                    isTo = toKey != null && cellKey == toKey,
                                    isInRange = toKey != null && cellKey > fromKey && cellKey < toKey,
                                    enabled = dayNumber in days,
                                    onClick = { onDayTap(dayNumber) },
                                )
                            }
                        }
                    }
                }
            }
        }

        if (toDate == null) {
            Text(
                text = stringResource(R.string.shamsi_date_range_picker_hint_select_to),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

@Composable
private fun RangeDayCell(
    day: Int,
    isFrom: Boolean,
    isTo: Boolean,
    isInRange: Boolean,
    enabled: Boolean,
    onClick: () -> Unit,
) {
    val circleColor by animateColorAsState(
        if (isFrom || isTo) MaterialTheme.colorScheme.primary else Color.Transparent,
        label = "rangeDayCircle",
    )
    val rangeBackground = if (isInRange) MaterialTheme.colorScheme.primary.copy(alpha = 0.15f) else Color.Transparent
    Box(
        modifier =
            Modifier
                .padding(2.dp)
                .size(38.dp)
                .background(rangeBackground)
                .clip(CircleShape)
                .background(circleColor)
                .clickable(enabled = enabled, onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = PersianNumber.toPersianDigits(day.toLong()),
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = if (isFrom || isTo) FontWeight.Bold else FontWeight.Normal,
            color =
                when {
                    isFrom || isTo -> MaterialTheme.colorScheme.onPrimary
                    !enabled -> MaterialTheme.colorScheme.onSurface.copy(alpha = 0.22f)
                    isInRange -> MaterialTheme.colorScheme.primary
                    else -> MaterialTheme.colorScheme.onSurface
                },
        )
    }
}

@Composable
private fun RangeRowLabel(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.labelMedium,
        color = MaterialTheme.colorScheme.primary,
        modifier = Modifier.fillMaxWidth(),
        textAlign = TextAlign.End,
    )
}
