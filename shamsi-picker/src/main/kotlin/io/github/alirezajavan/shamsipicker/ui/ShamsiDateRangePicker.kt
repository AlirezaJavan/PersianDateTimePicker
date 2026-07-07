package io.github.alirezajavan.shamsipicker.ui

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
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
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import io.github.alirezajavan.shamsipicker.R
import io.github.alirezajavan.shamsipicker.calendar.CalendarSystem
import io.github.alirezajavan.shamsipicker.format.NumberFormatter
import io.github.alirezajavan.shamsipicker.model.ShamsiDate
import io.github.alirezajavan.shamsipicker.model.ShamsiDatePickerStyle
import io.github.alirezajavan.shamsipicker.model.ShamsiDateRange
import io.github.alirezajavan.shamsipicker.model.ShamsiDateRangePickerConfig

private const val RANGE_WHEEL_DIM_ALPHA = 0.4f

/**
 * A date range picker dialog supporting both Shamsi and Gregorian calendars.
 *
 * Use [ShamsiDateRangePickerConfig] to set the initial range, optional date bounds,
 * display style, and calendar type.
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

    val calendarSystem = remember(config.calendarType) { config.calendarType.system }
    val numberFormatter = remember(config.calendarType) { NumberFormatter.get(config.calendarType) }
    val firstDayOfWeek =
        remember(config.firstDayOfWeek, calendarSystem) {
            config.firstDayOfWeek ?: calendarSystem.defaultFirstDayOfWeek
        }

    var currentStyle by remember { mutableStateOf(config.style) }

    // Wheel mode state
    var fromYear by remember { mutableIntStateOf(initFrom.year.coerceIn(calendarSystem.yearRange)) }
    var fromMonth by remember { mutableIntStateOf(initFrom.month) }
    var fromDay by remember { mutableIntStateOf(initFrom.day) }
    var toYear by remember { mutableIntStateOf(initTo.year.coerceIn(calendarSystem.yearRange)) }
    var toMonth by remember { mutableIntStateOf(initTo.month) }
    var toDay by remember { mutableIntStateOf(initTo.day) }

    // Clamp "from" to global bounds
    val fromMaxDay = calendarSystem.monthLength(fromYear, fromMonth)
    val fromDayBounds = calendarSystem.dayBounds(fromYear, fromMonth, fromMaxDay, resolvedMin, resolvedMax)
    if (fromDay > fromDayBounds.last) fromDay = fromDayBounds.last
    if (fromDay < fromDayBounds.first) fromDay = fromDayBounds.first

    // "to" must always be >= "from": derive an effective minimum for the "to" wheels
    val fromAsDate = ShamsiDate(fromYear, fromMonth, fromDay)
    val effectiveToMin: ShamsiDate =
        if (resolvedMin != null && resolvedMin > fromAsDate) resolvedMin else fromAsDate

    val effectiveToMinYear = effectiveToMin.year.coerceIn(calendarSystem.yearRange)
    if (toYear < effectiveToMinYear) toYear = effectiveToMinYear

    val toMonthBounds = calendarSystem.monthBounds(toYear, effectiveToMin, resolvedMax)
    if (toMonth < toMonthBounds.first) toMonth = toMonthBounds.first
    if (toMonth > toMonthBounds.last) toMonth = toMonthBounds.last

    val toMaxDay = calendarSystem.monthLength(toYear, toMonth)
    val toDayBounds = calendarSystem.dayBounds(toYear, toMonth, toMaxDay, effectiveToMin, resolvedMax)
    if (toDay > toDayBounds.last) toDay = toDayBounds.last
    if (toDay < toDayBounds.first) toDay = toDayBounds.first

    // Calendar mode state: toDate==null means the user is picking "to" next
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
                    toMinDate = effectiveToMin,
                    calendarSystem = calendarSystem,
                    numberFormatter = numberFormatter,
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
                    calendarSystem = calendarSystem,
                    numberFormatter = numberFormatter,
                    firstDayOfWeek = firstDayOfWeek,
                    onDayTap = { day ->
                        val tapped = ShamsiDate(viewYear, viewMonth, day)
                        if (toDate != null) {
                            fromDate = tapped
                            toDate = null
                        } else {
                            if (tapped < fromDate) {
                                toDate = fromDate
                                fromDate = tapped
                            } else {
                                toDate = tapped
                            }
                        }
                    },
                    onViewYear = {
                        viewYear =
                            it.coerceIn(
                                calendarSystem.yearRange.first,
                                calendarSystem.yearRange.last,
                            )
                    },
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
    toMinDate: ShamsiDate,
    calendarSystem: CalendarSystem,
    numberFormatter: NumberFormatter,
    onFromYear: (Int) -> Unit,
    onFromMonth: (Int) -> Unit,
    onFromDay: (Int) -> Unit,
    onToYear: (Int) -> Unit,
    onToMonth: (Int) -> Unit,
    onToDay: (Int) -> Unit,
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(2.dp),
    ) {
        CompactWheelDateRow(
            year = fromYear,
            month = fromMonth,
            day = fromDay,
            maxDay = fromMaxDay,
            dayBounds = fromDayBounds,
            minDate = minDate,
            maxDate = maxDate,
            calendarSystem = calendarSystem,
            numberFormatter = numberFormatter,
            onYear = onFromYear,
            onMonth = onFromMonth,
            onDay = onFromDay,
        )
        HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
        CompactWheelDateRow(
            year = toYear,
            month = toMonth,
            day = toDay,
            maxDay = toMaxDay,
            dayBounds = toDayBounds,
            minDate = toMinDate,
            maxDate = maxDate,
            calendarSystem = calendarSystem,
            numberFormatter = numberFormatter,
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
    calendarSystem: CalendarSystem,
    numberFormatter: NumberFormatter,
    onYear: (Int) -> Unit,
    onMonth: (Int) -> Unit,
    onDay: (Int) -> Unit,
) {
    val months = calendarSystem.monthBounds(year, minDate, maxDate)
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        key(maxDay) {
            WheelPicker(
                itemCount = maxDay,
                initialIndex = (day - 1).coerceIn(0, maxDay - 1),
                label = { numberFormatter.format((it + 1).toLong()) },
                onSelectedIndexChange = { onDay(it + 1) },
                enabledRange = (dayBounds.first - 1)..<dayBounds.last,
                visibleCount = 3,
                dimAlpha = RANGE_WHEEL_DIM_ALPHA,
                modifier = Modifier.width(64.dp),
            )
        }
        WheelPicker(
            itemCount = 12,
            initialIndex = month - 1,
            label = { calendarSystem.monthNames(year)[it] },
            onSelectedIndexChange = { onMonth(it + 1) },
            enabledRange = (months.first - 1)..<months.last,
            visibleCount = 3,
            dimAlpha = RANGE_WHEEL_DIM_ALPHA,
            modifier = Modifier.width(116.dp),
        )
        WheelPicker(
            itemCount = calendarSystem.yearRange.count(),
            initialIndex = year - calendarSystem.yearRange.first,
            label = { numberFormatter.format((calendarSystem.yearRange.first + it).toLong()) },
            onSelectedIndexChange = { onYear(calendarSystem.yearRange.first + it) },
            infinite = false,
            enabledRange = calendarSystem.yearEnabledRange(minDate, maxDate),
            visibleCount = 3,
            dimAlpha = RANGE_WHEEL_DIM_ALPHA,
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
    calendarSystem: CalendarSystem,
    numberFormatter: NumberFormatter,
    firstDayOfWeek: java.time.DayOfWeek,
    onDayTap: (Int) -> Unit,
    onViewYear: (Int) -> Unit,
    onViewMonth: (Int) -> Unit,
) {
    val maxDay = calendarSystem.monthLength(viewYear, viewMonth)
    val days = calendarSystem.dayBounds(viewYear, viewMonth, maxDay, minDate, maxDate)

    val afterMin = { y: Int, m: Int ->
        minDate == null ||
            (y * 100 + m) >= (minDate.year * 100 + minDate.month)
    }
    val beforeMax = { y: Int, m: Int ->
        maxDate == null ||
            (y * 100 + m) <= (maxDate.year * 100 + maxDate.month)
    }
    val prevMonth = if (viewMonth == 1) (viewYear - 1) to 12 else viewYear to (viewMonth - 1)
    val nextMonth = if (viewMonth == 12) (viewYear + 1) to 1 else viewYear to (viewMonth + 1)

    val fromKey = fromDate.year * 10000 + fromDate.month * 100 + fromDate.day
    val toKey = toDate?.let { it.year * 10000 + it.month * 100 + it.day }

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        NavRow(
            label = calendarSystem.monthNames(viewYear)[viewMonth - 1],
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
            label = numberFormatter.format(viewYear.toLong()),
            contentDescriptionPrev = stringResource(R.string.shamsi_date_picker_prev_year),
            contentDescriptionNext = stringResource(R.string.shamsi_date_picker_next_year),
            prevEnabled = minDate == null || viewYear - 1 >= minDate.year,
            nextEnabled = maxDate == null || viewYear + 1 <= maxDate.year,
            onPrev = { onViewYear(viewYear - 1) },
            onNext = { onViewYear(viewYear + 1) },
        )

        Row(modifier = Modifier.fillMaxWidth()) {
            for (name in calendarSystem.weekdayNames(firstDayOfWeek)) {
                Text(
                    text = name.take(1),
                    modifier = Modifier.weight(1f),
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }

        val firstWeekday = calendarSystem.firstWeekdayOfMonth(viewYear, viewMonth, firstDayOfWeek)
        val cells = firstWeekday + maxDay
        val rows = (cells + 6) / 7
        // No row spacing: the strip (38dp) in each 44dp cell provides natural visual separation.
        Column(modifier = Modifier.fillMaxWidth()) {
            for (row in 0 until rows) {
                Row(modifier = Modifier.fillMaxWidth()) {
                    for (col in 0 until 7) {
                        val dayNumber = row * 7 + col - firstWeekday + 1
                        Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.Center) {
                            if (dayNumber in 1..maxDay) {
                                val cellKey = viewYear * 10000 + viewMonth * 100 + dayNumber
                                val isFrom = cellKey == fromKey
                                val isTo = toKey != null && cellKey == toKey
                                val isInRange = toKey != null && cellKey > fromKey && cellKey < toKey
                                RangeDayCell(
                                    day = dayNumber,
                                    isFrom = isFrom,
                                    isTo = isTo,
                                    isInRange = isInRange,
                                    enabled = dayNumber in days,
                                    numberFormatter = numberFormatter,
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

/**
 * A calendar day cell that renders a colored "rope" strip connecting the selected range.
 *
 * The strip is drawn behind the circle and respects layout direction:
 * - LTR: "from" → right half (strip flows toward "to" on the right);
 *         "to" → left half (strip comes from "from" on the left)
 * - RTL: directions are mirrored — "from" → left half, "to" → right half
 * - In-range days: full-width fill
 * - Same-day range: only the circle, no strip
 */
@Composable
private fun RangeDayCell(
    day: Int,
    isFrom: Boolean,
    isTo: Boolean,
    isInRange: Boolean,
    enabled: Boolean,
    numberFormatter: NumberFormatter,
    onClick: () -> Unit,
) {
    val primary = MaterialTheme.colorScheme.primary
    val onPrimary = MaterialTheme.colorScheme.onPrimary
    val onSurface = MaterialTheme.colorScheme.onSurface
    val rangeColor = primary.copy(alpha = 0.15f)
    val sameDay = isFrom && isTo
    val isRtl = LocalLayoutDirection.current == LayoutDirection.Rtl

    val circleColor by animateColorAsState(
        if (isFrom || isTo) primary else Color.Transparent,
        label = "rangeDayCircle",
    )

    Box(
        modifier =
            Modifier
                .height(44.dp)
                .fillMaxWidth()
                .drawBehind {
                    if (!sameDay) {
                        val stripH = 38.dp.toPx()
                        val stripTop = (size.height - stripH) / 2f
                        val half = size.width / 2f
                        // In LTR "from" is earlier (left side) so its strip extends right toward "to".
                        // In RTL the physical sides are mirrored.
                        val fromStripX = if (isRtl) 0f else half
                        val toStripX = if (isRtl) half else 0f
                        when {
                            isInRange -> {
                                drawRect(
                                    color = rangeColor,
                                    topLeft = Offset(0f, stripTop),
                                    size = Size(size.width, stripH),
                                )
                            }

                            isFrom -> {
                                drawRect(
                                    color = rangeColor,
                                    topLeft = Offset(fromStripX, stripTop),
                                    size = Size(half, stripH),
                                )
                            }

                            isTo -> {
                                drawRect(
                                    color = rangeColor,
                                    topLeft = Offset(toStripX, stripTop),
                                    size = Size(half, stripH),
                                )
                            }
                        }
                    }
                }.clickable(enabled = enabled, onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        Box(
            modifier =
                Modifier
                    .size(38.dp)
                    .clip(CircleShape)
                    .background(circleColor),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = numberFormatter.format(day.toLong()),
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = if (isFrom || isTo) FontWeight.Bold else FontWeight.Normal,
                color =
                    when {
                        isFrom || isTo -> onPrimary
                        !enabled -> onSurface.copy(alpha = 0.22f)
                        isInRange -> primary
                        else -> onSurface
                    },
            )
        }
    }
}
