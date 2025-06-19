package com.example.dreamapp.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDate
import java.time.LocalTime

@Entity(tableName = "dreams")
data class DreamEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val date: String, // LocalDate as String
    val startTime: String, // LocalTime as String
    val endTime: String, // LocalTime as String
    val title: String,
    val content: String,
    val tags: String, // List<String> as JSON string
    val isLucid: Boolean
) {
    fun toDream(): Dream {
        return Dream(
            id = id,
            date = LocalDate.parse(date),
            startTime = LocalTime.parse(startTime),
            endTime = LocalTime.parse(endTime),
            title = title,
            content = content,
            tags = tags.split(",").filter { it.isNotBlank() },
            isLucid = isLucid
        )
    }

    companion object {
        fun fromDream(dream: Dream): DreamEntity {
            return DreamEntity(
                id = dream.id,
                date = dream.date.toString(),
                startTime = dream.startTime.toString(),
                endTime = dream.endTime.toString(),
                title = dream.title,
                content = dream.content,
                tags = dream.tags.joinToString(","),
                isLucid = dream.isLucid
            )
        }
    }
} 