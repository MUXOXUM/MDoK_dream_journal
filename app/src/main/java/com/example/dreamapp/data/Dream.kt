package com.example.dreamapp.data

import java.time.LocalDate
import java.time.LocalTime

data class Dream(
    val id: Long = 0,
    val date: LocalDate,
    val startTime: LocalTime,
    val endTime: LocalTime,
    val title: String,
    val content: String,
    val tags: List<String> = emptyList(),
    val isLucid: Boolean = false
) 