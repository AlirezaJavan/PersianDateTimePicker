package io.github.alirezajavan.shamsipicker.sample

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import io.github.alirezajavan.shamsipicker.calendar.CalendarType
import io.github.alirezajavan.shamsipicker.calendar.GregorianCalendarSystem
import io.github.alirezajavan.shamsipicker.calendar.ShamsiCalendar
import io.github.alirezajavan.shamsipicker.calendar.ShamsiCalendarSystem
import io.github.alirezajavan.shamsipicker.format.DateFormatter
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

@Composable
fun SampleScreen() {
    var calendarType by remember { mutableStateOf(CalendarType.Shamsi) }

    var selectedDate by remember { mutableStateOf(ShamsiCalendar.now()) }
    var selectedDateRange by remember { mutableStateOf<ShamsiDateRange?>(null) }
    var selectedTimeRange by remember { mutableStateOf<ShamsiTimeRange?>(null) }

    var showDatePicker by remember { mutableStateOf(false) }
    var showTimePicker by remember { mutableStateOf(false) }
    var showDateTimePicker by remember { mutableStateOf(false) }
    // null = hidden; non-null = open with that style
    var showDateRangePickerStyle by remember { mutableStateOf<ShamsiDatePickerStyle?>(null) }
    var showTimeRangePicker by remember { mutableStateOf(false) }
    var useCustomTheme by remember { mutableStateOf(false) }
    var showThemedDatePicker by remember { mutableStateOf(false) }

    Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
        Column(
            modifier =
                Modifier
                    .padding(innerPadding)
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            Text(
                text = "Shamsi Date & Time Picker",
                style = MaterialTheme.typography.headlineMedium,
            )

            Spacer(modifier = Modifier.height(16.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                androidx.compose.material3.RadioButton(
                    selected = calendarType == CalendarType.Shamsi,
                    onClick = { calendarType = CalendarType.Shamsi },
                )
                Text("Shamsi")
                Spacer(modifier = Modifier.width(16.dp))
                androidx.compose.material3.RadioButton(
                    selected = calendarType == CalendarType.Gregorian,
                    onClick = { calendarType = CalendarType.Gregorian },
                )
                Text("Gregorian")
            }

            Spacer(modifier = Modifier.height(32.dp))

            // ── Single pickers ────────────────────────────────────────────────
            Text("Single Pickers", style = MaterialTheme.typography.titleMedium)
            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "Date: ${DateFormatter.long(selectedDate, calendarType)}",
                style = MaterialTheme.typography.bodyLarge,
            )
            Text(
                text = "Time: ${DateFormatter.time(selectedDate, calendarType)}",
                style = MaterialTheme.typography.bodyLarge,
            )
            Text(
                text = "Combined: ${DateFormatter.longWithTime(selectedDate, calendarType)}",
                style = MaterialTheme.typography.bodyLarge,
            )

            Spacer(modifier = Modifier.height(16.dp))

            Button(onClick = { showDatePicker = true }) { Text("Open Date Picker") }
            Spacer(modifier = Modifier.height(8.dp))
            Button(onClick = { showTimePicker = true }) { Text("Open Time Picker") }
            Spacer(modifier = Modifier.height(8.dp))
            Button(onClick = { showDateTimePicker = true }) { Text("Open Date + Time Picker") }

            Spacer(modifier = Modifier.height(32.dp))
            HorizontalDivider()
            Spacer(modifier = Modifier.height(32.dp))

            // ── Range pickers ─────────────────────────────────────────────────
            Text("Range Pickers", style = MaterialTheme.typography.titleMedium)
            Spacer(modifier = Modifier.height(12.dp))

            val dateRangeText =
                selectedDateRange?.let {
                    "${DateFormatter.short(it.from, calendarType)}  →  ${DateFormatter.short(it.to, calendarType)}"
                } ?: "—"
            Text(
                text = "Date Range: $dateRangeText",
                style = MaterialTheme.typography.bodyLarge,
            )

            val timeRangeText =
                selectedTimeRange?.let {
                    fun fmt(t: ShamsiTime) = DateFormatter.time(ShamsiDate(1403, 1, 1, t.hour, t.minute), calendarType)
                    "${fmt(it.from)}  →  ${fmt(it.to)}"
                } ?: "—"
            Text(
                text = "Time Range: $timeRangeText",
                style = MaterialTheme.typography.bodyLarge,
            )

            Spacer(modifier = Modifier.height(16.dp))

            Button(onClick = { showDateRangePickerStyle = ShamsiDatePickerStyle.Wheel }) {
                Text("Open Date Range Picker (Wheel)")
            }
            Spacer(modifier = Modifier.height(8.dp))
            Button(onClick = { showDateRangePickerStyle = ShamsiDatePickerStyle.Calendar }) {
                Text("Open Date Range Picker (Calendar)")
            }
            Spacer(modifier = Modifier.height(8.dp))
            Button(onClick = { showTimeRangePicker = true }) { Text("Open Time Range Picker") }

            Spacer(modifier = Modifier.height(32.dp))
            HorizontalDivider()
            Spacer(modifier = Modifier.height(32.dp))

            // ── Theming ───────────────────────────────────────────────────────
            Text("Theming", style = MaterialTheme.typography.titleMedium)
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text =
                    "Same picker, restyled via ShamsiPickerColors/Typography and re-worded via " +
                        "ShamsiDatePickerStrings — English text even while the calendar is Shamsi.",
                style = MaterialTheme.typography.bodyMedium,
            )
            Spacer(modifier = Modifier.height(12.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("Use custom brand theme")
                Spacer(modifier = Modifier.width(8.dp))
                Switch(checked = useCustomTheme, onCheckedChange = { useCustomTheme = it })
            }
            Spacer(modifier = Modifier.height(8.dp))
            Button(onClick = { showThemedDatePicker = true }) { Text("Open Themed Date Picker") }

            Spacer(modifier = Modifier.height(32.dp))
            HorizontalDivider()
            Spacer(modifier = Modifier.height(32.dp))

            // ── Debug section ──────────────────────────────────────────────
            Text("Calendar Abstraction Debug", style = MaterialTheme.typography.titleMedium)
            Spacer(modifier = Modifier.height(12.dp))

            val epochDay = ShamsiCalendarSystem.toEpochDay(selectedDate.year, selectedDate.month, selectedDate.day)
            val (gy, gm, gd) = GregorianCalendarSystem.fromEpochDay(epochDay)
            val (sy, sm, sd) = ShamsiCalendarSystem.fromEpochDay(epochDay)

            Text(
                text = "Selected Date (Shamsi): ${selectedDate.year}/${selectedDate.month}/${selectedDate.day}",
                style = MaterialTheme.typography.bodyMedium,
            )
            Text(
                text = "Resolved Gregorian: $gy/$gm/$gd",
                style = MaterialTheme.typography.bodyMedium,
            )
            Text(
                text = "Resolved Shamsi: $sy/$sm/$sd",
                style = MaterialTheme.typography.bodyMedium,
            )
        }
    }

    // ── Single picker dialogs ─────────────────────────────────────────────────

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
                ),
        )
    }

    // ── Range picker dialogs ──────────────────────────────────────────────────

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
                ),
        )
    }

    // ── Themed date picker demo ─────────────────────────────────────────────

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
                ),
            colors = if (useCustomTheme) brandColors() else ShamsiPickerDefaults.colors(),
            typography = if (useCustomTheme) brandTypography() else ShamsiPickerDefaults.typography(),
            strings =
                if (useCustomTheme) {
                    ShamsiPickerDefaults.dateStrings(
                        title = "Pick a day",
                        confirmText = "Done",
                        cancelText = "Nevermind",
                    )
                } else {
                    ShamsiPickerDefaults.dateStrings()
                },
        )
    }
}

/** A sample brand palette: deep purple accents on a warm off-white surface. */
@Composable
private fun brandColors(): ShamsiPickerColors =
    ShamsiPickerDefaults.colors(
        accentColor = Color(0xFF6750A4),
        onAccentColor = Color.White,
        dialogContainerColor = Color(0xFFFFF8F0),
        titleColor = Color(0xFF6750A4),
        confirmButtonColors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6750A4)),
    )

/** A sample serif brand font applied across every text style the picker exposes. */
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
