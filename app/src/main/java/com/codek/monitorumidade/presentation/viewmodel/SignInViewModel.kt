package com.codek.monitorumidade.presentation.viewmodel

import android.content.SharedPreferences
import android.util.Log
import android.util.Patterns
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.codek.monitorumidade.data.model.Login
import com.codek.monitorumidade.data.repository.LoginRepository
import com.codek.monitorumidade.presentation.states.SignInUiState
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class SignInViewModel(
    private val repository: LoginRepository,
    private val preferences: SharedPreferences
) : ViewModel() {

    private val _uiState = MutableStateFlow(SignInUiState())
    val uiState = _uiState.asStateFlow()
    private val _signInIsSucessful = MutableSharedFlow<Boolean>()
    val signInIsSucessful = _signInIsSucessful.asSharedFlow()
    private val _resultId = MutableStateFlow<Int?>(null)
    val resultId = _resultId.asStateFlow()

    init {
        val savedEmail = preferences.getString("email", "")
        val savedPassword = preferences.getString("password", "")

        if (!savedEmail.isNullOrEmpty() && !savedPassword.isNullOrEmpty()) {
            viewModelScope.launch {
                signIn()
            }
        }

        _uiState.update { currentState ->
            currentState.copy(
                email = savedEmail ?: "",
                password = savedPassword ?: "",
                onEmailChange = { user ->
                    _uiState.update {
                        it.copy(email = user)
                    }
                },
                onPasswordChange = { password ->
                    _uiState.update {
                        it.copy(password = password)
                    }
                },
                onTogglePasswordVisibility = {
                    _uiState.update {
                        it.copy(isShowPassword = !_uiState.value.isShowPassword)
                    }
                }
            )
        }
    }

    suspend fun signIn() {
        val email = _uiState.value.email
        val senha = _uiState.value.password

        if (!validateField(email.isEmpty(), "Por favor, insira seu email.")) return
        if (!validateField(!Patterns.EMAIL_ADDRESS.matcher(email).matches(), "Formato de email inválido.")) return
        if (!validateField(senha.isEmpty(), "Por favor, insira sua senha.")) return

        try {
            val loginRequest = Login(email = email, senha = senha)
            val result: List<Login> = repository.enterLogin(loginRequest)

            if (result[0].id != null) {
                result[0].id?.let {
                    preferences.edit()
                        .putString("email", email)
                        .putString("password", senha)
                        .putInt("userId", it)
                        .putBoolean("isLoggedIn", true)
                        .apply()
                }
                _resultId.emit(result[0].id)
                _signInIsSucessful.emit(true)
                Log.d("LoginViewModel", "signIn: $result")
                Log.d("LoginViewModel", "signIn: $resultId")
            } else {
                showError("Erro ao fazer login")
            }
        } catch (e: Exception) {
            showError("Usuário ou senha incorretos")
        }
    }

    private fun showError(message: String) {
        _uiState.update { it.copy(error = message) }
        Log.d("LoginViewModel", "signIn: $message")
        viewModelScope.launch {
            delay(3000)
            _uiState.update { it.copy(error = null) }
        }
    }

    private fun validateField(condition: Boolean, errorMessage: String): Boolean {
        return if (condition) {
            showError(errorMessage)
            false
        } else {
            true
        }
    }
}