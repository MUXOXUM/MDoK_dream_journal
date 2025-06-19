package com.example.dreamapp.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.dreamapp.data.Dream
import java.time.Duration

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StatisticsScreen(
    dreams: List<Dream>
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Статистика") }
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
            // Общее количество снов
            StatCard(
                title = "Общее количество снов",
                value = dreams.size.toString(),
                subtitle = "Всего записей"
            )

            // Количество обычных и осознанных снов
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                StatCard(
                    title = "Обычные сны",
                    value = dreams.count { !it.isLucid }.toString(),
                    subtitle = "Неосознанные",
                    modifier = Modifier.weight(1f)
                )
                StatCard(
                    title = "Осознанные сны",
                    value = dreams.count { it.isLucid }.toString(),
                    subtitle = "Осознанные",
                    modifier = Modifier.weight(1f)
                )
            }

            // Средняя продолжительность сна
            val averageDuration = if (dreams.isNotEmpty()) {
                val totalMinutes = dreams.sumOf { dream ->
                    val duration = Duration.between(dream.startTime, dream.endTime)
                    duration.toMinutes()
                }
                val averageMinutes = totalMinutes / dreams.size
                val hours = averageMinutes / 60
                val minutes = averageMinutes % 60
                "${hours}ч ${minutes}м"
            } else {
                "0ч 0м"
            }

            StatCard(
                title = "Средняя продолжительность",
                value = averageDuration,
                subtitle = "Время сна"
            )

            // Топ-5 часто встречающихся тегов
            TopTagsCard(dreams = dreams)
        }
    }
}

@Composable
fun StatCard(
    title: String,
    value: String,
    subtitle: String,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = value,
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
fun TopTagsCard(
    dreams: List<Dream>
) {
    val tagCounts = dreams
        .flatMap { it.tags }
        .groupingBy { it }
        .eachCount()
        .toList()
        .sortedByDescending { it.second }
        .take(5)

    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = "Топ-5 тегов",
                style = MaterialTheme.typography.titleMedium
            )
            Spacer(modifier = Modifier.height(12.dp))
            
            if (tagCounts.isEmpty()) {
                Text(
                    text = "Пока нет тегов",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            } else {
                tagCounts.forEachIndexed { index, (tag, count) ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "${index + 1}.",
                                style = MaterialTheme.typography.bodyMedium,
                                modifier = Modifier.width(24.dp)
                            )
                            AssistChip(
                                onClick = { },
                                label = { Text(tag) }
                            )
                        }
                        Text(
                            text = "$count",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }
        }
    }
} 