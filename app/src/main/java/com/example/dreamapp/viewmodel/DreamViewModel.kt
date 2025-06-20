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
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalTime

class DreamViewModel(
    application: Application,
    private val authViewModel: AuthViewModel? = null
) : AndroidViewModel(application) {
    private val repository: DreamRepository
    private val firestoreRepository: FirestoreRepository
    val dreams: StateFlow<List<Dream>>

    private var isUserAuthenticated = false

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
                // addSampleDreams() // Удалено: больше не добавляем тестовые записи
            }
        }

        // Если передан AuthViewModel — следим за авторизацией
        authViewModel?.let { authVM ->
            viewModelScope.launch {
                authVM.authState.collectLatest { state ->
                    val wasAuthenticated = isUserAuthenticated
                    isUserAuthenticated = state is AuthState.Authenticated
                    if (!wasAuthenticated && isUserAuthenticated) {
                        // Только что вошли — синхронизируем локальные записи в облако
                        syncLocalToCloud()
                    }
                }
            }
        }
    }
    
    fun addDream(dream: Dream) {
        viewModelScope.launch {
            val localId = repository.insertDream(dream)
            if (isUserAuthenticated) {
                try {
                    firestoreRepository.addDream(dream.copy(id = localId))
                } catch (_: Exception) {}
            }
        }
    }
    
    fun updateDream(dream: Dream) {
        viewModelScope.launch {
            repository.updateDream(dream)
            if (isUserAuthenticated) {
                try {
                    firestoreRepository.updateDream(dream)
                } catch (_: Exception) {}
            }
        }
    }
    
    fun deleteDream(dream: Dream) {
        viewModelScope.launch {
            repository.deleteDream(dream)
            if (isUserAuthenticated) {
                try {
                    firestoreRepository.deleteDream(dream.id)
                } catch (_: Exception) {}
            }
        }
    }
    
    fun deleteDreamById(id: Long) {
        viewModelScope.launch {
            repository.deleteDreamById(id)
            if (isUserAuthenticated) {
                try {
                    firestoreRepository.deleteDream(id)
                } catch (_: Exception) {}
            }
        }
    }

    private suspend fun syncLocalToCloud() {
        val localDreams = repository.allDreams.stateIn(viewModelScope, SharingStarted.Eagerly, emptyList()).value
        val cloudDreams = firestoreRepository.getAllDreams().stateIn(viewModelScope, SharingStarted.Eagerly, emptyList()).value
        val cloudIds = cloudDreams.map { it.id }.toSet()
        val toUpload = localDreams.filter { it.id !in cloudIds }
        toUpload.forEach { firestoreRepository.addDream(it) }
    }

    companion object {
        fun provideFactory(application: Application, authViewModel: AuthViewModel? = null): ViewModelProvider.Factory {
            return object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T {
                    return DreamViewModel(application, authViewModel) as T
                }
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
} 