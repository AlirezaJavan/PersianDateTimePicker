package io.github.alirezajavan.shamsipicker.sample

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Event
import androidx.compose.material.icons.filled.MoreTime
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import io.github.alirezajavan.shamsipicker.calendar.CalendarType
import io.github.alirezajavan.shamsipicker.calendar.ShamsiCalendar
import io.github.alirezajavan.shamsipicker.format.DateFormatter
import io.github.alirezajavan.shamsipicker.model.CalendarEvent
import io.github.alirezajavan.shamsipicker.model.CalendarEventType
import io.github.alirezajavan.shamsipicker.model.ShamsiDate
import io.github.alirezajavan.shamsipicker.model.ShamsiDatePickerConfig
import io.github.alirezajavan.shamsipicker.model.ShamsiDatePickerStyle
import io.github.alirezajavan.shamsipicker.model.ShamsiDateRange
import io.github.alirezajavan.shamsipicker.model.ShamsiDateRangePickerConfig
import io.github.alirezajavan.shamsipicker.model.ShamsiDateTimePickerConfig
import io.github.alirezajavan.shamsipicker.model.ShamsiTime
import io.github.alirezajavan.shamsipicker.model.ShamsiTimePickerConfig
import io.github.alirezajavan.shamsipicker.model.ShamsiTimeRange
import io.github.alirezajavan.shamsipicker.model.ShamsiTimeRangePickerConfig
import io.github.alirezajavan.shamsipicker.ui.ShamsiDatePickerDialog
import io.github.alirezajavan.shamsipicker.ui.ShamsiDateRangePickerDialog
import io.github.alirezajavan.shamsipicker.ui.ShamsiDateTimePickerDialog
import io.github.alirezajavan.shamsipicker.ui.ShamsiTimePickerDialog
import io.github.alirezajavan.shamsipicker.ui.ShamsiTimeRangePickerDialog
import io.github.alirezajavan.shamsipicker.ui.theme.ShamsiPickerColors
import io.github.alirezajavan.shamsipicker.ui.theme.ShamsiPickerDefaults
import io.github.alirezajavan.shamsipicker.ui.theme.ShamsiPickerTypography
import java.time.DayOfWeek

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MaterialTheme {
                SampleScreen()
            }
        }
    }
}

enum class SampleTab(
    val title: String,
    val icon: ImageVector,
) {
    Single("Single", Icons.Default.CalendarToday),
    Range("Range", Icons.Default.DateRange),
    Settings("Config", Icons.Default.Settings),
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SampleScreen() {
    var currentTab by remember { mutableStateOf(SampleTab.Single) }

    var calendarType by remember { mutableStateOf(CalendarType.Shamsi) }
    var useCompactMode by remember { mutableStateOf(false) }
    var firstDayOfWeek by remember { mutableStateOf<DayOfWeek?>(null) }

    var selectedDate by remember { mutableStateOf(ShamsiCalendar.now()) }
    var selectedDateRange by remember { mutableStateOf<ShamsiDateRange?>(null) }
    var selectedTimeRange by remember { mutableStateOf<ShamsiTimeRange?>(null) }

    var showDatePicker by remember { mutableStateOf(false) }
    var showLimitedDatePicker by remember { mutableStateOf(false) }
    var showTimePicker by remember { mutableStateOf(false) }
    var showDateTimePicker by remember { mutableStateOf(false) }
    var showDateRangePickerStyle by remember { mutableStateOf<ShamsiDatePickerStyle?>(null) }
    var showTimeRangePicker by remember { mutableStateOf(false) }
    var showThemedDatePicker by remember { mutableStateOf(false) }
    var showHolidaysPicker by remember { mutableStateOf(false) }

    val holidayEvents =
        remember(calendarType, selectedDate.year) {
            val year = selectedDate.year
            if (calendarType == CalendarType.Shamsi) {
                listOf(
                    CalendarEvent(ShamsiDate(year, 1, 1), "Nowruz", type = CalendarEventType.Holiday),
                    CalendarEvent(
                        ShamsiDate(year, 1, 13),
                        "Sizdah Be-dar",
                        type = CalendarEventType.Holiday,
                        colorArgb = 0xFF43A047.toInt(),
                    ),
                    CalendarEvent(
                        ShamsiDate(year, 3, 14),
                        "App Reminder",
                        type = CalendarEventType.Event,
                        colorArgb = 0xFF1E88E5.toInt(),
                    ),
                )
            } else {
                listOf(
                    CalendarEvent(ShamsiDate(year, 1, 1), "New Year's Day", type = CalendarEventType.Holiday),
                    CalendarEvent(
                        ShamsiDate(year, 12, 25),
                        "Christmas",
                        type = CalendarEventType.Holiday,
                        colorArgb = 0xFF43A047.toInt(),
                    ),
                    CalendarEvent(
                        ShamsiDate(year, 7, 4),
                        "App Reminder",
                        type = CalendarEventType.Event,
                        colorArgb = 0xFF1E88E5.toInt(),
                    ),
                )
            }
        }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = "ShamsiPicker ${currentTab.title}",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                    )
                },
                colors =
                    TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.surface,
                        titleContentColor = MaterialTheme.colorScheme.onSurface,
                    ),
            )
        },
        bottomBar = {
            NavigationBar {
                SampleTab.entries.forEach { tab ->
                    NavigationBarItem(
                        selected = currentTab == tab,
                        onClick = { currentTab = tab },
                        icon = { Icon(tab.icon, contentDescription = null) },
                        label = { Text(tab.title) },
                    )
                }
            }
        },
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding =
                PaddingValues(
                    start = 16.dp,
                    end = 16.dp,
                    top = innerPadding.calculateTopPadding() + 8.dp,
                    bottom = innerPadding.calculateBottomPadding() + 16.dp,
                ),
            verticalArrangement = Arrangement.spacedBy(24.dp),
        ) {
            when (currentTab) {
                SampleTab.Single -> {
                    item {
                        FeatureSection(title = "Single Pickers") {
                            SelectionTile(
                                title = "Date Picker",
                                value = DateFormatter.long(selectedDate, calendarType),
                                icon = Icons.Default.CalendarToday,
                                onClick = { showDatePicker = true },
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            SelectionTile(
                                title = "Time Picker",
                                value = DateFormatter.time(selectedDate, calendarType),
                                icon = Icons.Default.Schedule,
                                onClick = { showTimePicker = true },
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            SelectionTile(
                                title = "Date + Time Picker",
                                value = DateFormatter.longWithTime(selectedDate, calendarType),
                                icon = Icons.Default.CalendarMonth,
                                onClick = { showDateTimePicker = true },
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            SelectionTile(
                                title = "Limited Date Picker",
                                value = "Today to +30 days",
                                icon = Icons.Default.Timer,
                                onClick = { showLimitedDatePicker = true },
                            )
                        }
                    }
                    item {
                        FeatureSection(title = "Visual Customization") {
                            SelectionTile(
                                title = "Themed Picker",
                                value = "Custom Colors & Typography",
                                icon = Icons.Default.Palette,
                                onClick = { showThemedDatePicker = true },
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            SelectionTile(
                                title = "Events & Holidays",
                                value = "Calendar style with markers",
                                icon = Icons.Default.Timer,
                                onClick = { showHolidaysPicker = true },
                            )
                        }
                    }
                }

                SampleTab.Range -> {
                    item {
                        FeatureSection(title = "Date Ranges") {
                            val dateRangeText =
                                selectedDateRange?.let {
                                    "${DateFormatter.short(it.from, calendarType)}  →  ${DateFormatter.short(it.to, calendarType)}"
                                } ?: "Select Range"
                            SelectionTile(
                                title = "Date Range (Wheel)",
                                value = dateRangeText,
                                icon = Icons.Default.DateRange,
                                onClick = { showDateRangePickerStyle = ShamsiDatePickerStyle.Wheel },
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            SelectionTile(
                                title = "Date Range (Calendar)",
                                value = dateRangeText,
                                icon = Icons.Default.Event,
                                onClick = { showDateRangePickerStyle = ShamsiDatePickerStyle.Calendar },
                            )
                        }
                    }
                    item {
                        FeatureSection(title = "Time Ranges") {
                            val timeRangeText =
                                selectedTimeRange?.let {
                                    fun fmt(t: ShamsiTime) = DateFormatter.time(ShamsiDate(1403, 1, 1, t.hour, t.minute), calendarType)
                                    "${fmt(it.from)}  →  ${fmt(it.to)}"
                                } ?: "Select Range"
                            SelectionTile(
                                title = "Time Range Picker",
                                value = timeRangeText,
                                icon = Icons.Default.MoreTime,
                                onClick = { showTimeRangePicker = true },
                            )
                        }
                    }
                }

                SampleTab.Settings -> {
                    item {
                        SettingsCard(
                            calendarType = calendarType,
                            onCalendarTypeChange = { calendarType = it },
                            useCompactMode = useCompactMode,
                            onCompactModeChange = { useCompactMode = it },
                            firstDayOfWeek = firstDayOfWeek,
                            onFirstDayOfWeekChange = { firstDayOfWeek = it },
                        )
                    }
                }
            }
        }
    }

    // --- Dialogs (common to all tabs) ---

    if (showDatePicker) {
        ShamsiDatePickerDialog(
            onConfirm = {
                selectedDate = it
                showDatePicker = false
            },
            onDismiss = { showDatePicker = false },
            config =
                ShamsiDatePickerConfig(
                    initialDate = selectedDate,
                    style = ShamsiDatePickerStyle.Calendar,
                    calendarType = calendarType,
                    firstDayOfWeek = firstDayOfWeek,
                    compactCalendar = useCompactMode,
                    compactWheel = useCompactMode,
                ),
        )
    }

    if (showLimitedDatePicker) {
        val now = ShamsiCalendar.now()
        ShamsiDatePickerDialog(
            onConfirm = {
                selectedDate = it
                showLimitedDatePicker = false
            },
            onDismiss = { showLimitedDatePicker = false },
            config =
                ShamsiDatePickerConfig(
                    initialDate = now,
                    minDate = now,
                    maxDate = ShamsiCalendar.fromGregorian(ShamsiCalendar.toGregorian(now).plusDays(30)),
                    style = ShamsiDatePickerStyle.Calendar,
                    calendarType = calendarType,
                    firstDayOfWeek = firstDayOfWeek,
                    compactCalendar = useCompactMode,
                    compactWheel = useCompactMode,
                ),
        )
    }

    if (showTimePicker) {
        ShamsiTimePickerDialog(
            onConfirm = { time ->
                selectedDate = selectedDate.copy(hour = time.hour, minute = time.minute)
                showTimePicker = false
            },
            onDismiss = { showTimePicker = false },
            config =
                ShamsiTimePickerConfig(
                    initialTime = selectedDate.toTime(),
                    calendarType = calendarType,
                    compactWheel = useCompactMode,
                ),
        )
    }

    if (showDateTimePicker) {
        ShamsiDateTimePickerDialog(
            onConfirm = {
                selectedDate = it
                showDateTimePicker = false
            },
            onDismiss = { showDateTimePicker = false },
            config =
                ShamsiDateTimePickerConfig(
                    initialDateTime = selectedDate,
                    calendarType = calendarType,
                    firstDayOfWeek = firstDayOfWeek,
                    compactCalendar = useCompactMode,
                    compactWheel = useCompactMode,
                ),
        )
    }

    if (showHolidaysPicker) {
        ShamsiDatePickerDialog(
            onConfirm = {
                selectedDate = it
                showHolidaysPicker = false
            },
            onDismiss = { showHolidaysPicker = false },
            config =
                ShamsiDatePickerConfig(
                    initialDate = selectedDate,
                    style = ShamsiDatePickerStyle.Calendar,
                    calendarType = calendarType,
                    firstDayOfWeek = firstDayOfWeek,
                    compactCalendar = useCompactMode,
                    compactWheel = useCompactMode,
                    events = holidayEvents,
                ),
        )
    }

    showDateRangePickerStyle?.let { style ->
        ShamsiDateRangePickerDialog(
            onConfirm = { range ->
                selectedDateRange = range
                showDateRangePickerStyle = null
            },
            onDismiss = { showDateRangePickerStyle = null },
            config =
                ShamsiDateRangePickerConfig(
                    initialFrom = selectedDateRange?.from ?: ShamsiCalendar.now(),
                    initialTo = selectedDateRange?.to ?: ShamsiCalendar.now(),
                    style = style,
                    calendarType = calendarType,
                    firstDayOfWeek = firstDayOfWeek,
                    compactCalendar = useCompactMode,
                    compactWheel = useCompactMode,
                    events = holidayEvents,
                ),
        )
    }

    if (showTimeRangePicker) {
        ShamsiTimeRangePickerDialog(
            onConfirm = { range ->
                selectedTimeRange = range
                showTimeRangePicker = false
            },
            onDismiss = { showTimeRangePicker = false },
            config =
                ShamsiTimeRangePickerConfig(
                    initialFrom = selectedTimeRange?.from ?: ShamsiCalendar.now().toTime(),
                    initialTo = selectedTimeRange?.to ?: ShamsiCalendar.now().toTime(),
                    calendarType = calendarType,
                    compactWheel = useCompactMode,
                ),
        )
    }

    if (showThemedDatePicker) {
        ShamsiDatePickerDialog(
            onConfirm = {
                selectedDate = it
                showThemedDatePicker = false
            },
            onDismiss = { showThemedDatePicker = false },
            config =
                ShamsiDatePickerConfig(
                    initialDate = selectedDate,
                    style = ShamsiDatePickerStyle.Calendar,
                    calendarType = calendarType,
                    firstDayOfWeek = firstDayOfWeek,
                ),
            colors = brandColors(),
            typography = brandTypography(),
            strings =
                ShamsiPickerDefaults.dateStrings(
                    title = "Pick a day",
                    confirmText = "Done",
                    cancelText = "Nevermind",
                ),
        )
    }
}

@Composable
fun FeatureSection(
    title: String,
    content: @Composable () -> Unit,
) {
    Column {
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(start = 4.dp, bottom = 12.dp),
        )
        Card(
            colors =
                CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
                ),
            shape = RoundedCornerShape(24.dp),
        ) {
            Column(modifier = Modifier.padding(12.dp)) {
                content()
            }
        }
    }
}

@Composable
fun SelectionTile(
    title: String,
    value: String,
    icon: ImageVector,
    onClick: () -> Unit,
) {
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(16.dp),
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 2.dp,
    ) {
        Row(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = MaterialTheme.colorScheme.primaryContainer,
                modifier = Modifier.size(40.dp),
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    modifier = Modifier.padding(8.dp),
                    tint = MaterialTheme.colorScheme.onPrimaryContainer,
                )
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.outline,
                )
                Text(
                    text = value,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Medium,
                )
            }
            Icon(
                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.outline,
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsCard(
    calendarType: CalendarType,
    onCalendarTypeChange: (CalendarType) -> Unit,
    useCompactMode: Boolean,
    onCompactModeChange: (Boolean) -> Unit,
    firstDayOfWeek: DayOfWeek?,
    onFirstDayOfWeekChange: (DayOfWeek?) -> Unit,
) {
    Card(
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.2f)),
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Settings, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    "Global Config",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                )
            }
            Spacer(modifier = Modifier.height(16.dp))

            Text(
                "Calendar System",
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Spacer(modifier = Modifier.height(8.dp))
            SingleChoiceSegmentedButtonRow(modifier = Modifier.fillMaxWidth()) {
                SegmentedButton(
                    selected = calendarType == CalendarType.Shamsi,
                    onClick = { onCalendarTypeChange(CalendarType.Shamsi) },
                    shape = SegmentedButtonDefaults.itemShape(index = 0, count = 2),
                ) {
                    Text("Shamsi")
                }
                SegmentedButton(
                    selected = calendarType == CalendarType.Gregorian,
                    onClick = { onCalendarTypeChange(CalendarType.Gregorian) },
                    shape = SegmentedButtonDefaults.itemShape(index = 1, count = 2),
                ) {
                    Text("Gregorian")
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            Text(
                "First Day of Week",
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Spacer(modifier = Modifier.height(8.dp))
            SingleChoiceSegmentedButtonRow(modifier = Modifier.fillMaxWidth()) {
                SegmentedButton(
                    selected = firstDayOfWeek == null,
                    onClick = { onFirstDayOfWeekChange(null) },
                    shape = SegmentedButtonDefaults.itemShape(index = 0, count = 3),
                ) {
                    Text("Auto")
                }
                SegmentedButton(
                    selected = firstDayOfWeek == DayOfWeek.SATURDAY,
                    onClick = { onFirstDayOfWeekChange(DayOfWeek.SATURDAY) },
                    shape = SegmentedButtonDefaults.itemShape(index = 1, count = 3),
                ) {
                    Text("Sat")
                }
                SegmentedButton(
                    selected = firstDayOfWeek == DayOfWeek.SUNDAY,
                    onClick = { onFirstDayOfWeekChange(DayOfWeek.SUNDAY) },
                    shape = SegmentedButtonDefaults.itemShape(index = 2, count = 3),
                ) {
                    Text("Sun")
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            Row(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .clickable { onCompactModeChange(!useCompactMode) },
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        "Compact Mode",
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Medium,
                    )
                    Text(
                        "Smaller grid and single-row wheels",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.outline,
                    )
                }
                Switch(checked = useCompactMode, onCheckedChange = onCompactModeChange)
            }
        }
    }
}

@Composable
private fun brandColors(): ShamsiPickerColors =
    ShamsiPickerDefaults.colors(
        accentColor = Color(0xFF6750A4),
        onAccentColor = Color.White,
        dialogContainerColor = Color(0xFFFFF8F0),
        titleColor = Color(0xFF6750A4),
        confirmButtonColors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6750A4)),
    )

@Composable
private fun brandTypography(): ShamsiPickerTypography {
    val brandFont = FontFamily.Serif
    val defaults = ShamsiPickerDefaults.typography()
    return defaults.copy(
        titleStyle = defaults.titleStyle.copy(fontFamily = brandFont),
        wheelItemStyle = defaults.wheelItemStyle.copy(fontFamily = brandFont),
        dayCellStyle = defaults.dayCellStyle.copy(fontFamily = brandFont),
        navHeaderStyle = defaults.navHeaderStyle.copy(fontFamily = brandFont),
        buttonTextStyle = defaults.buttonTextStyle.copy(fontFamily = brandFont),
    )
}
