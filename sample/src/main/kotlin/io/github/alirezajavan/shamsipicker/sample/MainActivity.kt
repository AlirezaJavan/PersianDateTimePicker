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
import androidx.compose.material3.Button
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
import io.github.alirezajavan.shamsipicker.ui.ShamsiDateFormatter
import io.github.alirezajavan.shamsipicker.ui.ShamsiDatePickerDialog
import io.github.alirezajavan.shamsipicker.ui.ShamsiDatePickerStyle
import io.github.alirezajavan.shamsipicker.ui.ShamsiTimePickerDialog

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
    var showDatePicker by remember { mutableStateOf(false) }
    var showTimePicker by remember { mutableStateOf(false) }

    Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "Shamsi Date & Time Picker",
                style = MaterialTheme.typography.headlineMedium
            )

            Spacer(modifier = Modifier.height(32.dp))

            Text(
                text = "Selected Date: ${ShamsiDateFormatter.long(selectedDate)}",
                style = MaterialTheme.typography.bodyLarge
            )
            Text(
                text = "Selected Time: ${ShamsiDateFormatter.time(selectedDate)}",
                style = MaterialTheme.typography.bodyLarge
            )

            Spacer(modifier = Modifier.height(32.dp))

            Button(onClick = { showDatePicker = true }) {
                Text("Open Date Picker")
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(onClick = { showTimePicker = true }) {
                Text("Open Time Picker")
            }
        }
    }

    if (showDatePicker) {
        ShamsiDatePickerDialog(
            initialDate = selectedDate,
            onConfirm = {
                selectedDate = it
                showDatePicker = false
            },
            onDismiss = { showDatePicker = false },
            style = ShamsiDatePickerStyle.Calendar
        )
    }

    if (showTimePicker) {
        ShamsiTimePickerDialog(
            initialHour = selectedDate.hour,
            initialMinute = selectedDate.minute,
            onConfirm = { h, m ->
                selectedDate = selectedDate.copy(hour = h, minute = m)
                showTimePicker = false
            },
            onDismiss = { showTimePicker = false }
        )
    }
}
