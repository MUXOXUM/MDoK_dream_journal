package com.example.dreamapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.dreamapp.data.Dream
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalTime

class DreamViewModel : ViewModel() {
    private val _dreams = MutableStateFlow<List<Dream>>(emptyList())
    val dreams: StateFlow<List<Dream>> = _dreams.asStateFlow()
    
    private var nextId = 1L
    
    init {
        // Добавим несколько тестовых записей для демонстрации
        addSampleDreams()
    }
    
    fun addDream(dream: Dream) {
        viewModelScope.launch {
            val newDream = if (dream.id == 0L) {
                dream.copy(id = nextId++)
            } else {
                dream
            }
            
            val currentDreams = _dreams.value.toMutableList()
            val existingIndex = currentDreams.indexOfFirst { it.id == newDream.id }
            
            if (existingIndex >= 0) {
                currentDreams[existingIndex] = newDream
            } else {
                currentDreams.add(newDream)
            }
            
            // Сортируем по дате (новые сверху)
            currentDreams.sortByDescending { it.date }
            
            _dreams.value = currentDreams
        }
    }
    
    fun deleteDream(dreamId: Long) {
        viewModelScope.launch {
            val currentDreams = _dreams.value.toMutableList()
            currentDreams.removeAll { it.id == dreamId }
            _dreams.value = currentDreams
        }
    }
    
    private fun addSampleDreams() {
        val sampleDreams = listOf(
            Dream(
                id = nextId++,
                date = LocalDate.now().minusDays(1),
                startTime = LocalTime.of(23, 0),
                endTime = LocalTime.of(7, 30),
                title = "Полёт над городом",
                content = "Я летал над своим родным городом. Вид был потрясающий - все здания были освещены, а я парил между ними как птица. Ощущение свободы было невероятным.",
                tags = listOf("полёт", "город", "свобода"),
                isLucid = true
            ),
            Dream(
                id = nextId++,
                date = LocalDate.now().minusDays(2),
                startTime = LocalTime.of(22, 30),
                endTime = LocalTime.of(6, 45),
                title = "Встреча с детством",
                content = "Я оказался в доме своего детства. Все было точно так же, как много лет назад. Я встретил своих старых друзей и мы играли в те же игры.",
                tags = listOf("детство", "друзья", "воспоминания"),
                isLucid = false
            ),
            Dream(
                id = nextId++,
                date = LocalDate.now().minusDays(3),
                startTime = LocalTime.of(0, 15),
                endTime = LocalTime.of(8, 0),
                title = "Подводное путешествие",
                content = "Я плавал в океане среди коралловых рифов. Рыбы всех цветов радуги окружали меня. Вода была кристально чистой.",
                tags = listOf("океан", "рыбы", "природа"),
                isLucid = false
            ),
            Dream(
                id = nextId++,
                date = LocalDate.now().minusDays(4),
                startTime = LocalTime.of(23, 30),
                endTime = LocalTime.of(6, 15),
                title = "Космическое путешествие",
                content = "Я был в космосе и видел Землю издалека. Звезды были невероятно яркими, а планеты казались такими близкими.",
                tags = listOf("космос", "звезды", "планеты", "путешествие"),
                isLucid = true
            ),
            Dream(
                id = nextId++,
                date = LocalDate.now().minusDays(5),
                startTime = LocalTime.of(22, 0),
                endTime = LocalTime.of(7, 0),
                title = "Волшебный лес",
                content = "Я гулял по волшебному лесу, где деревья светились и разговаривали. Феи летали вокруг и пели прекрасные песни.",
                tags = listOf("лес", "волшебство", "феи", "природа"),
                isLucid = false
            ),
            Dream(
                id = nextId++,
                date = LocalDate.now().minusDays(6),
                startTime = LocalTime.of(1, 0),
                endTime = LocalTime.of(8, 30),
                title = "Древний замок",
                content = "Я исследовал древний замок с множеством комнат и секретных проходов. В каждой комнате была своя тайна.",
                tags = listOf("замок", "тайны", "исследование", "история"),
                isLucid = true
            ),
            Dream(
                id = nextId++,
                date = LocalDate.now().minusDays(7),
                startTime = LocalTime.of(0, 30),
                endTime = LocalTime.of(6, 45),
                title = "Город будущего",
                content = "Я оказался в футуристическом городе с летающими машинами и небоскребами из стекла. Технологии были невероятными.",
                tags = listOf("будущее", "технологии", "город", "машины"),
                isLucid = false
            )
        )
        
        _dreams.value = sampleDreams
    }
} 