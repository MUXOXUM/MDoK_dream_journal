package com.example.dreamapp.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.dreamapp.viewmodel.SettingsViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(viewModel: SettingsViewModel) {
    val notificationsEnabled by viewModel.notificationsEnabled.collectAsState()
    val hour by viewModel.notificationHour.collectAsState()
    val minute by viewModel.notificationMinute.collectAsState()
    var showTimePicker by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Настройки") }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Уведомления", modifier = Modifier.weight(1f))
                Switch(
                    checked = notificationsEnabled,
                    onCheckedChange = { viewModel.setNotificationsEnabled(it) }
                )
            }

            if (notificationsEnabled) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Время напоминания", modifier = Modifier.weight(1f))
                    TextButton(onClick = { showTimePicker = true }) {
                        Text(String.format("%02d:%02d", hour, minute))
                    }
                }
            }
        }
    }

    if (showTimePicker) {
        TimePickerDialog(
            initialHour = hour,
            initialMinute = minute,
            onTimeSelected = { h, m ->
                viewModel.setNotificationTime(h, m)
                showTimePicker = false
            },
            onDismiss = { showTimePicker = false }
        )
    }
}

@Composable
fun TimePickerDialog(
    initialHour: Int,
    initialMinute: Int,
    onTimeSelected: (Int, Int) -> Unit,
    onDismiss: () -> Unit
) {
    var hour by remember { mutableStateOf(initialHour) }
    var minute by remember { mutableStateOf(initialMinute) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Выберите время") },
        text = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("Часы:")
                Spacer(modifier = Modifier.width(8.dp))
                OutlinedTextField(
                    value = hour.toString(),
                    onValueChange = { hour = it.toIntOrNull()?.coerceIn(0, 23) ?: 0 },
                    modifier = Modifier.width(60.dp),
                    singleLine = true
                )
                Spacer(modifier = Modifier.width(16.dp))
                Text("Минуты:")
                Spacer(modifier = Modifier.width(8.dp))
                OutlinedTextField(
                    value = minute.toString(),
                    onValueChange = { minute = it.toIntOrNull()?.coerceIn(0, 59) ?: 0 },
                    modifier = Modifier.width(60.dp),
                    singleLine = true
                )
            }
        },
        confirmButton = {
            TextButton(onClick = { onTimeSelected(hour, minute) }) {
                Text("OK")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Отмена")
            }
        }
    )
} 