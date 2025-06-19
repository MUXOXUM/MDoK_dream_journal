package com.example.dreamapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.dreamapp.data.FirebaseAuthRepository
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AuthViewModel : ViewModel() {
    private val authRepository = FirebaseAuthRepository()

    private val _authState = MutableStateFlow<AuthState>(AuthState.Loading)
    val authState: StateFlow<AuthState> = _authState.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    init {
        observeAuthState()
    }

    private fun observeAuthState() {
        viewModelScope.launch {
            authRepository.getAuthStateFlow().collect { user ->
                _authState.value = if (user != null) {
                    AuthState.Authenticated(user)
                } else {
                    AuthState.Unauthenticated
                }
            }
        }
    }

    fun signIn(email: String, password: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null

            val result = authRepository.signIn(email, password)
            result.fold(
                onSuccess = { user ->
                    _authState.value = AuthState.Authenticated(user)
                },
                onFailure = { exception ->
                    _errorMessage.value = getErrorMessage(exception)
                }
            )
            _isLoading.value = false
        }
    }

    fun signUp(email: String, password: String, displayName: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null

            val result = authRepository.signUp(email, password, displayName)
            result.fold(
                onSuccess = { user ->
                    _authState.value = AuthState.Authenticated(user)
                },
                onFailure = { exception ->
                    _errorMessage.value = getErrorMessage(exception)
                }
            )
            _isLoading.value = false
        }
    }

    fun signOut() {
        viewModelScope.launch {
            authRepository.signOut()
        }
    }

    fun resetPassword(email: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null

            val result = authRepository.resetPassword(email)
            result.fold(
                onSuccess = {
                    _errorMessage.value = "Письмо для сброса пароля отправлено на ваш email"
                },
                onFailure = { exception ->
                    _errorMessage.value = getErrorMessage(exception)
                }
            )
            _isLoading.value = false
        }
    }

    fun clearError() {
        _errorMessage.value = null
    }

    private fun getErrorMessage(throwable: Throwable): String {
        return when (throwable.message) {
            "The email address is badly formatted." -> "Неверный формат email"
            "The password is invalid or the user does not have a password." -> "Неверный пароль"
            "There is no user record corresponding to this identifier. The user may have been deleted." -> "Пользователь не найден"
            "The email address is already in use by another account." -> "Этот email уже используется"
            "The given password is invalid. [ Password should be at least 6 characters ]" -> "Пароль должен содержать минимум 6 символов"
            "A network error (such as timeout, interrupted connection or unreachable host) has occurred." -> "Ошибка сети. Проверьте подключение к интернету"
            else -> throwable.message ?: "Произошла неизвестная ошибка"
        }
    }
}

sealed class AuthState {
    object Loading : AuthState()
    object Unauthenticated : AuthState()
    data class Authenticated(val user: FirebaseUser) : AuthState()
} 