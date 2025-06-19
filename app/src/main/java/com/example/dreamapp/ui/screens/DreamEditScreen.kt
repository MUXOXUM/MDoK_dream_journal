package com.example.dreamapp.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.dreamapp.data.Dream
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DreamEditScreen(
    dream: Dream? = null,
    onSave: (Dream) -> Unit,
    onBack: () -> Unit
) {
    var title by remember { mutableStateOf(dream?.title ?: "") }
    var content by remember { mutableStateOf(dream?.content ?: "") }
    var date by remember { mutableStateOf(dream?.date ?: LocalDate.now()) }
    var startTime by remember { mutableStateOf(dream?.startTime ?: LocalTime.now()) }
    var endTime by remember { mutableStateOf(dream?.endTime ?: LocalTime.now()) }
    var tags by remember { mutableStateOf(dream?.tags ?: emptyList()) }
    var isLucid by remember { mutableStateOf(dream?.isLucid ?: false) }
    var newTag by remember { mutableStateOf("") }
    
    var showDatePicker by remember { mutableStateOf(false) }
    var showStartTimeDialog by remember { mutableStateOf(false) }
    var showEndTimeDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (dream == null) "Новый сон" else "Редактировать сон") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Назад")
                    }
                },
                actions = {
                    IconButton(
                        onClick = {
                            val newDream = Dream(
                                id = dream?.id ?: 0,
                                date = date,
                                startTime = startTime,
                                endTime = endTime,
                                title = title,
                                content = content,
                                tags = tags,
                                isLucid = isLucid
                            )
                            onSave(newDream)
                        },
                        enabled = title.isNotBlank() && content.isNotBlank()
                    ) {
                        Icon(Icons.Default.Check, contentDescription = "Сохранить")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Название сна
            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                label = { Text("Название сна") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            // Дата сна
            OutlinedTextField(
                value = date.format(DateTimeFormatter.ofPattern("dd.MM.yyyy")),
                onValueChange = { },
                label = { Text("Дата сна") },
                modifier = Modifier.fillMaxWidth(),
                readOnly = true,
                trailingIcon = {
                    TextButton(onClick = { showDatePicker = true }) {
                        Text("Выбрать")
                    }
                }
            )

            // Время начала и окончания
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedTextField(
                    value = startTime.format(DateTimeFormatter.ofPattern("HH:mm")),
                    onValueChange = { },
                    label = { Text("Время начала") },
                    modifier = Modifier.weight(1f),
                    readOnly = true,
                    trailingIcon = {
                        TextButton(onClick = { showStartTimeDialog = true }) {
                            Text("Выбрать")
                        }
                    }
                )
                
                OutlinedTextField(
                    value = endTime.format(DateTimeFormatter.ofPattern("HH:mm")),
                    onValueChange = { },
                    label = { Text("Время окончания") },
                    modifier = Modifier.weight(1f),
                    readOnly = true,
                    trailingIcon = {
                        TextButton(onClick = { showEndTimeDialog = true }) {
                            Text("Выбрать")
                        }
                    }
                )
            }

            // Осознанность сна
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Осознанный сон",
                    modifier = Modifier.weight(1f)
                )
                Switch(
                    checked = isLucid,
                    onCheckedChange = { isLucid = it }
                )
            }

            // Теги
            Column {
                Text(
                    text = "Теги",
                    style = MaterialTheme.typography.titleSmall
                )
                Spacer(modifier = Modifier.height(8.dp))
                
                // Добавление нового тега
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = newTag,
                        onValueChange = { newTag = it },
                        label = { Text("Новый тег") },
                        modifier = Modifier.weight(1f),
                        singleLine = true
                    )
                    Button(
                        onClick = {
                            if (newTag.isNotBlank() && !tags.contains(newTag)) {
                                tags = tags + newTag
                                newTag = ""
                            }
                        },
                        enabled = newTag.isNotBlank()
                    ) {
                        Text("Добавить")
                    }
                }
                
                // Отображение существующих тегов
                if (tags.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(8.dp))
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        items(tags) { tag ->
                            AssistChip(
                                onClick = { },
                                label = { Text(tag) },
                                trailingIcon = {
                                    IconButton(
                                        onClick = { tags = tags - tag }
                                    ) {
                                        Icon(
                                            Icons.Default.ArrowBack,
                                            contentDescription = "Удалить тег"
                                        )
                                    }
                                }
                            )
                        }
                    }
                }
            }

            // Содержимое сна
            OutlinedTextField(
                value = content,
                onValueChange = { content = it },
                label = { Text("Описание сна") },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp),
                maxLines = 10
            )
        }
    }

    // DatePicker
    if (showDatePicker) {
        val datePickerState = rememberDatePickerState(
            initialSelectedDateMillis = date.toEpochDay() * 24 * 60 * 60 * 1000
        )
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        datePickerState.selectedDateMillis?.let { millis ->
                            date = LocalDate.ofEpochDay(millis / (24 * 60 * 60 * 1000))
                        }
                        showDatePicker = false
                    }
                ) {
                    Text("OK")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) {
                    Text("Отмена")
                }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }

    // Time Dialogs
    if (showStartTimeDialog) {
        TimeSelectionDialog(
            currentTime = startTime,
            onTimeSelected = { 
                startTime = it
                showStartTimeDialog = false
            },
            onDismiss = { showStartTimeDialog = false }
        )
    }

    if (showEndTimeDialog) {
        TimeSelectionDialog(
            currentTime = endTime,
            onTimeSelected = { 
                endTime = it
                showEndTimeDialog = false
            },
            onDismiss = { showEndTimeDialog = false }
        )
    }
}

@Composable
fun TimeSelectionDialog(
    currentTime: LocalTime,
    onTimeSelected: (LocalTime) -> Unit,
    onDismiss: () -> Unit
) {
    var selectedHour by remember { mutableStateOf(currentTime.hour) }
    var selectedMinute by remember { mutableStateOf(currentTime.minute) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Выберите время") },
        text = {
            Column {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Часы:")
                    OutlinedTextField(
                        value = selectedHour.toString(),
                        onValueChange = { 
                            selectedHour = it.toIntOrNull()?.coerceIn(0, 23) ?: 0
                        },
                        modifier = Modifier.width(80.dp),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Минуты:")
                    OutlinedTextField(
                        value = selectedMinute.toString(),
                        onValueChange = { 
                            selectedMinute = it.toIntOrNull()?.coerceIn(0, 59) ?: 0
                        },
                        modifier = Modifier.width(80.dp),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                    )
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    onTimeSelected(LocalTime.of(selectedHour, selectedMinute))
                }
            ) {
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