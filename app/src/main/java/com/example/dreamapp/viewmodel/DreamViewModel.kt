package com.example.dreamapp.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.dreamapp.data.Dream
import com.example.dreamapp.data.DreamDatabase
import com.example.dreamapp.data.DreamRepository
import com.example.dreamapp.data.FirestoreRepository
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalTime

class DreamViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: DreamRepository
    private val firestoreRepository: FirestoreRepository
    val dreams: StateFlow<List<Dream>>

    init {
        val database = DreamDatabase.getDatabase(application)
        val dreamDao = database.dreamDao()
        repository = DreamRepository(dreamDao)
        firestoreRepository = FirestoreRepository()
        
        dreams = repository.allDreams.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )
        
        // Добавим тестовые данные только если база пустая
        viewModelScope.launch {
            if (repository.getDreamCount() == 0) {
                addSampleDreams()
            }
        }
    }
    
    fun addDream(dream: Dream) {
        viewModelScope.launch {
            // Сохраняем локально
            val localId = repository.insertDream(dream)
            
            // Синхронизируем с облаком
            try {
                firestoreRepository.addDream(dream.copy(id = localId))
            } catch (e: Exception) {
                // Если облако недоступно, продолжаем работать локально
            }
        }
    }
    
    fun updateDream(dream: Dream) {
        viewModelScope.launch {
            // Обновляем локально
            repository.updateDream(dream)
            
            // Синхронизируем с облаком
            try {
                firestoreRepository.updateDream(dream)
            } catch (e: Exception) {
                // Если облако недоступно, продолжаем работать локально
            }
        }
    }
    
    fun deleteDream(dream: Dream) {
        viewModelScope.launch {
            // Удаляем локально
            repository.deleteDream(dream)
            
            // Синхронизируем с облаком
            try {
                firestoreRepository.deleteDream(dream.id)
            } catch (e: Exception) {
                // Если облако недоступно, продолжаем работать локально
            }
        }
    }
    
    fun deleteDreamById(id: Long) {
        viewModelScope.launch {
            repository.deleteDreamById(id)
            
            // Синхронизируем с облаком
            try {
                firestoreRepository.deleteDream(id)
            } catch (e: Exception) {
                // Если облако недоступно, продолжаем работать локально
            }
        }
    }

    fun syncWithCloud() {
        viewModelScope.launch {
            try {
                val localDreams = repository.allDreams.first()
                firestoreRepository.syncDreamsToCloud(localDreams)
            } catch (e: Exception) {
                // Обработка ошибок синхронизации
            }
        }
    }
    
    private suspend fun addSampleDreams() {
        val sampleDreams = listOf(
            Dream(
                id = 0,
                date = LocalDate.now().minusDays(1),
                startTime = LocalTime.of(23, 0),
                endTime = LocalTime.of(7, 30),
                title = "Полёт над городом",
                content = "Я летал над своим родным городом. Вид был потрясающий - все здания были освещены, а я парил между ними как птица. Ощущение свободы было невероятным.",
                tags = listOf("полёт", "город", "свобода"),
                isLucid = true
            ),
            Dream(
                id = 0,
                date = LocalDate.now().minusDays(2),
                startTime = LocalTime.of(22, 30),
                endTime = LocalTime.of(6, 45),
                title = "Встреча с детством",
                content = "Я оказался в доме своего детства. Все было точно так же, как много лет назад. Я встретил своих старых друзей и мы играли в те же игры.",
                tags = listOf("детство", "друзья", "воспоминания"),
                isLucid = false
            ),
            Dream(
                id = 0,
                date = LocalDate.now().minusDays(3),
                startTime = LocalTime.of(0, 15),
                endTime = LocalTime.of(8, 0),
                title = "Подводное путешествие",
                content = "Я плавал в океане среди коралловых рифов. Рыбы всех цветов радуги окружали меня. Вода была кристально чистой.",
                tags = listOf("океан", "рыбы", "природа"),
                isLucid = false
            ),
            Dream(
                id = 0,
                date = LocalDate.now().minusDays(4),
                startTime = LocalTime.of(23, 30),
                endTime = LocalTime.of(6, 15),
                title = "Космическое путешествие",
                content = "Я был в космосе и видел Землю издалека. Звезды были невероятно яркими, а планеты казались такими близкими.",
                tags = listOf("космос", "звезды", "планеты", "путешествие"),
                isLucid = true
            ),
            Dream(
                id = 0,
                date = LocalDate.now().minusDays(5),
                startTime = LocalTime.of(22, 0),
                endTime = LocalTime.of(7, 0),
                title = "Волшебный лес",
                content = "Я гулял по волшебному лесу, где деревья светились и разговаривали. Феи летали вокруг и пели прекрасные песни.",
                tags = listOf("лес", "волшебство", "феи", "природа"),
                isLucid = false
            ),
            Dream(
                id = 0,
                date = LocalDate.now().minusDays(6),
                startTime = LocalTime.of(1, 0),
                endTime = LocalTime.of(8, 30),
                title = "Древний замок",
                content = "Я исследовал древний замок с множеством комнат и секретных проходов. В каждой комнате была своя тайна.",
                tags = listOf("замок", "тайны", "исследование", "история"),
                isLucid = true
            ),
            Dream(
                id = 0,
                date = LocalDate.now().minusDays(7),
                startTime = LocalTime.of(0, 30),
                endTime = LocalTime.of(6, 45),
                title = "Город будущего",
                content = "Я оказался в футуристическом городе с летающими машинами и небоскребами из стекла. Технологии были невероятными.",
                tags = listOf("будущее", "технологии", "город", "машины"),
                isLucid = false
            )
        )
        
        sampleDreams.forEach { dream ->
            repository.insertDream(dream)
        }
    }

    companion object {
        fun provideFactory(application: Application): ViewModelProvider.Factory {
            return object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T {
                    return DreamViewModel(application) as T
                }
            }
        }
    }
} 