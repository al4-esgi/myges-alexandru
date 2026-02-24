package com.vlxx.myges.ui.screens.unauthenticated.signInScreen.viewModel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.vlxx.myges.R
import com.vlxx.myges.domain.repositories.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import timber.log.Timber

class SignInViewModel(
    application: Application,
    private val userRepository: UserRepository
) : AndroidViewModel(application) {

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    private val _email = MutableStateFlow("")
    val email: StateFlow<String> = _email.asStateFlow()

    private val _password = MutableStateFlow("")
    val password: StateFlow<String> = _password.asStateFlow()

    fun onEmailChange(newEmail: String) {
        _email.value = newEmail
        _errorMessage.value = null
    }

    fun onPasswordChange(newPassword: String) {
        _password.value = newPassword
        _errorMessage.value = null
    }

    fun login() {
        if (_email.value.isBlank() || _password.value.isBlank()) {
            _errorMessage.value = getApplication<Application>().getString(R.string.signin_error_empty_fields)
            return
        }

        _isLoading.value = true
        _errorMessage.value = null

        viewModelScope.launch {
            try {
                userRepository.login(_email.value, _password.value)
                Timber.d("Login successful")
            } catch (e: Exception) {
                Timber.e(e, "Login error")
                val context = getApplication<Application>()
                _errorMessage.value = when {
                    e.message?.contains("401") == true || e.message?.contains("403") == true ->
                        context.getString(R.string.signin_error_invalid_credentials)
                    e.message?.contains("network") == true || e.message?.contains("timeout") == true ->
                        context.getString(R.string.signin_error_network)
                    else -> context.getString(R.string.signin_error_unknown, e.message ?: "")
                }
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun clearError() {
        _errorMessage.value = null
    }

}
