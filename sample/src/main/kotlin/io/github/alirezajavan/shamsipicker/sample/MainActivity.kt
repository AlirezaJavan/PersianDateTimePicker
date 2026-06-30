package io.github.alirezajavan.shamsipicker.sample

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import io.github.alirezajavan.shamsipicker.calendar.ShamsiCalendar
import io.github.alirezajavan.shamsipicker.model.ShamsiDate
import io.github.alirezajavan.shamsipicker.model.ShamsiDatePickerConfig
import io.github.alirezajavan.shamsipicker.model.ShamsiDatePickerStyle
import io.github.alirezajavan.shamsipicker.model.ShamsiDateRange
import io.github.alirezajavan.shamsipicker.model.ShamsiDateRangePickerConfig
import io.github.alirezajavan.shamsipicker.model.ShamsiTime
import io.github.alirezajavan.shamsipicker.model.ShamsiTimePickerConfig
import io.github.alirezajavan.shamsipicker.model.ShamsiTimeRange
import io.github.alirezajavan.shamsipicker.model.ShamsiTimeRangePickerConfig
import io.github.alirezajavan.shamsipicker.ui.ShamsiDateFormatter
import io.github.alirezajavan.shamsipicker.ui.ShamsiDatePickerDialog
import io.github.alirezajavan.shamsipicker.ui.ShamsiDateRangePickerDialog
import io.github.alirezajavan.shamsipicker.ui.ShamsiTimePickerDialog
import io.github.alirezajavan.shamsipicker.ui.ShamsiTimeRangePickerDialog

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
    var selectedDate by remember { mutableStateOf(ShamsiCalendar.now()) }
    var selectedDateRange by remember { mutableStateOf<ShamsiDateRange?>(null) }
    var selectedTimeRange by remember { mutableStateOf<ShamsiTimeRange?>(null) }

    var showDatePicker by remember { mutableStateOf(false) }
    var showTimePicker by remember { mutableStateOf(false) }
    // null = hidden; non-null = open with that style
    var showDateRangePickerStyle by remember { mutableStateOf<ShamsiDatePickerStyle?>(null) }
    var showTimeRangePicker by remember { mutableStateOf(false) }

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

            Spacer(modifier = Modifier.height(32.dp))

            // ── Single pickers ────────────────────────────────────────────────
            Text("Single Pickers", style = MaterialTheme.typography.titleMedium)
            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "Date: ${ShamsiDateFormatter.long(selectedDate)}",
                style = MaterialTheme.typography.bodyLarge,
            )
            Text(
                text = "Time: ${ShamsiDateFormatter.time(selectedDate)}",
                style = MaterialTheme.typography.bodyLarge,
            )

            Spacer(modifier = Modifier.height(16.dp))

            Button(onClick = { showDatePicker = true }) { Text("Open Date Picker") }
            Spacer(modifier = Modifier.height(8.dp))
            Button(onClick = { showTimePicker = true }) { Text("Open Time Picker") }

            Spacer(modifier = Modifier.height(32.dp))
            HorizontalDivider()
            Spacer(modifier = Modifier.height(32.dp))

            // ── Range pickers ─────────────────────────────────────────────────
            Text("Range Pickers", style = MaterialTheme.typography.titleMedium)
            Spacer(modifier = Modifier.height(12.dp))

            val dateRangeText =
                selectedDateRange?.let {
                    "${ShamsiDateFormatter.short(it.from)}  →  ${ShamsiDateFormatter.short(it.to)}"
                } ?: "—"
            Text(
                text = "Date Range: $dateRangeText",
                style = MaterialTheme.typography.bodyLarge,
            )

            val timeRangeText =
                selectedTimeRange?.let {
                    fun fmt(t: ShamsiTime) = ShamsiDateFormatter.time(ShamsiDate(1403, 1, 1, t.hour, t.minute))
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
            config = ShamsiTimePickerConfig(initialTime = selectedDate.toTime()),
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
                ),
        )
    }
}
