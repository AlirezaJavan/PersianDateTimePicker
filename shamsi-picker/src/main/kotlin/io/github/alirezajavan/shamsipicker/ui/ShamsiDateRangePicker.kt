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
import androidx.compose.ui.graphics.takeOrElse
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import io.github.alirezajavan.shamsipicker.calendar.CalendarSystem
import io.github.alirezajavan.shamsipicker.format.NumberFormatter
import io.github.alirezajavan.shamsipicker.model.CalendarEvent
import io.github.alirezajavan.shamsipicker.model.CalendarEventType
import io.github.alirezajavan.shamsipicker.model.ShamsiDate
import io.github.alirezajavan.shamsipicker.model.ShamsiDatePickerStyle
import io.github.alirezajavan.shamsipicker.model.ShamsiDateRange
import io.github.alirezajavan.shamsipicker.model.ShamsiDateRangePickerConfig
import io.github.alirezajavan.shamsipicker.model.fromSystem
import io.github.alirezajavan.shamsipicker.model.toSystem
import io.github.alirezajavan.shamsipicker.ui.theme.ShamsiDateRangePickerStrings
import io.github.alirezajavan.shamsipicker.ui.theme.ShamsiPickerColors
import io.github.alirezajavan.shamsipicker.ui.theme.ShamsiPickerDefaults
import io.github.alirezajavan.shamsipicker.ui.theme.ShamsiPickerDimens
import io.github.alirezajavan.shamsipicker.ui.theme.ShamsiPickerTypography

/**
 * A date range picker dialog supporting both Shamsi and Gregorian calendars.
 *
 * Use [ShamsiDateRangePickerConfig] to set the initial range, optional date bounds,
 * display style, and calendar type. Use [colors], [typography], and [strings] to
 * restyle or re-word the dialog without forking it.
 */
@Composable
public fun ShamsiDateRangePickerDialog(
    onConfirm: (ShamsiDateRange) -> Unit,
    onDismiss: () -> Unit,
    config: ShamsiDateRangePickerConfig = ShamsiDateRangePickerConfig(),
    colors: ShamsiPickerColors = ShamsiPickerDefaults.colors(),
    typography: ShamsiPickerTypography = ShamsiPickerDefaults.typography(),
    strings: ShamsiDateRangePickerStrings = ShamsiPickerDefaults.dateRangeStrings(calendarType = config.calendarType),
) {
    val calendarSystem = remember(config.calendarType) { config.calendarType.system }
    val (initFrom, initTo) =
        remember(config.initialFrom, config.initialTo, calendarSystem) {
            val f = config.initialFrom.toShamsiDate().toSystem(calendarSystem)
            val t = config.initialTo.toShamsiDate().toSystem(calendarSystem)
            if (f <= t) f to t else t to f
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
                val result = if (from <= to) ShamsiDateRange(from, to) else ShamsiDateRange(to, from)
                ShamsiDateRange(
                    result.from.fromSystem(calendarSystem),
                    result.to.fromSystem(calendarSystem),
                )
            }

            ShamsiDatePickerStyle.Calendar -> {
                val effectiveTo = toDate ?: fromDate
                val from = fromDate.copy(hour = initFrom.hour, minute = initFrom.minute)
                val to = effectiveTo.copy(hour = initTo.hour, minute = initTo.minute)
                ShamsiDateRange(
                    from.fromSystem(calendarSystem),
                    to.fromSystem(calendarSystem),
                )
            }
        }

    PickerDialogScaffold(
        title = strings.title,
        confirmText = strings.confirmText,
        cancelText = strings.cancelText,
        onCancel = onDismiss,
        onConfirm = { onConfirm(buildResult()) },
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
                    colors = colors,
                    typography = typography,
                    onFromYear = { fromYear = it },
                    onFromMonth = { fromMonth = it },
                    onFromDay = { fromDay = it },
                    onToYear = { toYear = it },
                    onToMonth = { toMonth = it },
                    onToDay = { toDay = it },
                    compact = config.compactWheel,
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
                    colors = colors,
                    typography = typography,
                    strings = strings,
                    compact = config.compactCalendar,
                    events = config.events,
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
    colors: ShamsiPickerColors,
    typography: ShamsiPickerTypography,
    onFromYear: (Int) -> Unit,
    onFromMonth: (Int) -> Unit,
    onFromDay: (Int) -> Unit,
    onToYear: (Int) -> Unit,
    onToMonth: (Int) -> Unit,
    onToDay: (Int) -> Unit,
    compact: Boolean = false,
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(ShamsiPickerDimens.RANGE_ROW_SPACING_DP.dp),
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
            colors = colors,
            typography = typography,
            onYear = onFromYear,
            onMonth = onFromMonth,
            onDay = onFromDay,
            compact = compact,
        )
        HorizontalDivider(modifier = Modifier.padding(vertical = ShamsiPickerDimens.RANGE_DIVIDER_PADDING_DP.dp))
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
            colors = colors,
            typography = typography,
            onYear = onToYear,
            onMonth = onToMonth,
            onDay = onToDay,
            compact = compact,
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
    colors: ShamsiPickerColors,
    typography: ShamsiPickerTypography,
    onYear: (Int) -> Unit,
    onMonth: (Int) -> Unit,
    onDay: (Int) -> Unit,
    compact: Boolean = false,
) {
    val months = calendarSystem.monthBounds(year, minDate, maxDate)
    val wheelVisibleCount =
        if (compact) ShamsiPickerDimens.COMPACT_WHEEL_VISIBLE_COUNT else ShamsiPickerDimens.RANGE_WHEEL_VISIBLE_COUNT
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
                enabledRange = (dayBounds.first - 1)..<dayBounds.last,
                visibleCount = wheelVisibleCount,
                dimAlpha = ShamsiPickerDimens.RANGE_WHEEL_DIM_ALPHA,
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
            visibleCount = wheelVisibleCount,
            dimAlpha = ShamsiPickerDimens.RANGE_WHEEL_DIM_ALPHA,
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
            visibleCount = wheelVisibleCount,
            dimAlpha = ShamsiPickerDimens.RANGE_WHEEL_DIM_ALPHA,
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
    colors: ShamsiPickerColors,
    typography: ShamsiPickerTypography,
    strings: ShamsiDateRangePickerStrings,
    onDayTap: (Int) -> Unit,
    onViewYear: (Int) -> Unit,
    onViewMonth: (Int) -> Unit,
    compact: Boolean = false,
    events: List<CalendarEvent> = emptyList(),
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
    val columnSpacing =
        if (compact) {
            ShamsiPickerDimens.COMPACT_CALENDAR_COLUMN_SPACING_DP
        } else {
            ShamsiPickerDimens.CALENDAR_COLUMN_SPACING_DP
        }

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(columnSpacing.dp),
    ) {
        NavRow(
            label = calendarSystem.monthNames(viewYear)[viewMonth - 1],
            contentDescriptionPrev = strings.prevMonthDescription,
            contentDescriptionNext = strings.nextMonthDescription,
            prevEnabled = afterMin(prevMonth.first, prevMonth.second),
            nextEnabled = beforeMax(nextMonth.first, nextMonth.second),
            colors = colors,
            typography = typography,
            compact = compact,
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
            contentDescriptionPrev = strings.prevYearDescription,
            contentDescriptionNext = strings.nextYearDescription,
            prevEnabled = minDate == null || viewYear - 1 >= minDate.year,
            nextEnabled = maxDate == null || viewYear + 1 <= maxDate.year,
            colors = colors,
            typography = typography,
            compact = compact,
            onPrev = { onViewYear(viewYear - 1) },
            onNext = { onViewYear(viewYear + 1) },
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

        val firstWeekday = calendarSystem.firstWeekdayOfMonth(viewYear, viewMonth, firstDayOfWeek)
        val cells = firstWeekday + maxDay
        val rows = (cells + 6) / 7
        val monthEvents = events.filter { it.date.year == viewYear && it.date.month == viewMonth }
        // No row spacing: the strip (DayCellSize) in each RangeDayRowHeight cell provides
        // natural visual separation.
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
                                    isWeekend = calendarSystem.isWeekend(viewYear, viewMonth, dayNumber),
                                    numberFormatter = numberFormatter,
                                    colors = colors,
                                    typography = typography,
                                    compact = compact,
                                    events = monthEvents.filter { it.date.day == dayNumber },
                                    weekendDescription = strings.weekendDescription,
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
                text = strings.selectToHint,
                style = typography.weekdayLabelStyle,
                color = colors.secondaryTextColor,
            )
        }
    }
}

/**
 * A calendar day cell that renders a colored "rope" strip connecting the selected range.
 *
 * The strip is drawn behind the circle and respects layout direction:
 * - LTR: "from" â†’ right half (strip flows toward "to" on the right);
 *         "to" â†’ left half (strip comes from "from" on the left)
 * - RTL: directions are mirrored â€” "from" â†’ left half, "to" â†’ right half
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
    isWeekend: Boolean,
    numberFormatter: NumberFormatter,
    colors: ShamsiPickerColors,
    typography: ShamsiPickerTypography,
    onClick: () -> Unit,
    compact: Boolean = false,
    events: List<CalendarEvent> = emptyList(),
    weekendDescription: String = "",
) {
    val holidayEvents = events.filter { it.type == CalendarEventType.Holiday }
    val markerEvents = events.filter { it.type == CalendarEventType.Event }
    val isHoliday = isWeekend || holidayEvents.isNotEmpty()

    val sameDay = isFrom && isTo
    val isRtl = LocalLayoutDirection.current == LayoutDirection.Rtl
    val rowHeight =
        if (compact) ShamsiPickerDimens.COMPACT_RANGE_DAY_ROW_HEIGHT_DP else ShamsiPickerDimens.RANGE_DAY_ROW_HEIGHT_DP
    val cellSize = if (compact) ShamsiPickerDimens.COMPACT_DAY_CELL_SIZE_DP else ShamsiPickerDimens.DAY_CELL_SIZE_DP
    val markerSize = if (compact) ShamsiPickerDimens.COMPACT_EVENT_MARKER_SIZE_DP else ShamsiPickerDimens.EVENT_MARKER_SIZE_DP
    val markerBottomOffset =
        if (compact) ShamsiPickerDimens.COMPACT_EVENT_MARKER_BOTTOM_OFFSET_DP else ShamsiPickerDimens.EVENT_MARKER_BOTTOM_OFFSET_DP

    val descriptionParts = events.map { it.label } + listOfNotNull(weekendDescription.takeIf { isWeekend && it.isNotEmpty() })
    val cellDescription = descriptionParts.joinToString(separator = ", ")

    val circleColor by animateColorAsState(
        if (isFrom || isTo) colors.accentColor else Color.Transparent,
        label = "rangeDayCircle",
    )

    Box(
        modifier =
            Modifier
                .height(rowHeight.dp)
                .fillMaxWidth()
                .drawBehind {
                    if (!sameDay) {
                        val stripH = cellSize.dp.toPx()
                        val stripTop = (size.height - stripH) / 2f
                        val half = size.width / 2f
                        // In LTR "from" is earlier (left side) so its strip extends right toward "to".
                        // In RTL the physical sides are mirrored.
                        val fromStripX = if (isRtl) 0f else half
                        val toStripX = if (isRtl) half else 0f
                        when {
                            isInRange -> {
                                drawRect(
                                    color = colors.rangeStripColor,
                                    topLeft = Offset(0f, stripTop),
                                    size = Size(size.width, stripH),
                                )
                            }

                            isFrom -> {
                                drawRect(
                                    color = colors.rangeStripColor,
                                    topLeft = Offset(fromStripX, stripTop),
                                    size = Size(half, stripH),
                                )
                            }

                            isTo -> {
                                drawRect(
                                    color = colors.rangeStripColor,
                                    topLeft = Offset(toStripX, stripTop),
                                    size = Size(half, stripH),
                                )
                            }
                        }
                    }
                }.clickable(enabled = enabled, onClick = onClick)
                .then(
                    if (cellDescription.isNotEmpty()) {
                        Modifier.semantics { contentDescription = cellDescription }
                    } else {
                        Modifier
                    },
                ),
        contentAlignment = Alignment.Center,
    ) {
        Box(
            modifier =
                Modifier
                    .size(cellSize.dp)
                    .clip(CircleShape)
                    .background(circleColor),
            contentAlignment = Alignment.Center,
        ) {
            val holidayColor =
                holidayEvents.firstOrNull()?.colorArgb?.let { Color(it) }
                    ?: colors.holidayTextColor.takeOrElse { colors.textColor }
            Text(
                text = numberFormatter.format(day.toLong()),
                style = typography.dayCellStyle,
                fontWeight = if (isFrom || isTo || isHoliday) FontWeight.Bold else FontWeight.Normal,
                color =
                    when {
                        isFrom || isTo -> colors.onAccentColor
                        !enabled -> colors.disabledTextColor
                        isHoliday -> holidayColor
                        isInRange -> colors.accentColor
                        else -> colors.textColor
                    },
            )
            val markerEvent = markerEvents.firstOrNull()
            if (markerEvent != null) {
                val markerColor =
                    markerEvent.colorArgb?.let { Color(it) }
                        ?: colors.eventMarkerColor.takeOrElse { colors.accentColor }
                Box(
                    modifier =
                        Modifier
                            .align(Alignment.BottomCenter)
                            .padding(bottom = markerBottomOffset.dp)
                            .size(markerSize.dp)
                            .clip(CircleShape)
                            .background(if (isFrom || isTo) colors.onAccentColor else markerColor),
                )
            }
        }
    }
}
