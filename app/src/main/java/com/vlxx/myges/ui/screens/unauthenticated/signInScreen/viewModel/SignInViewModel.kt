package com.vlxx.myges.ui.screens.unauthenticated.signInScreen.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vlxx.myges.domain.repositories.UserRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class SignInViewModel(
    private val userRepository: UserRepository
) : ViewModel() {

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    fun connection() {
        _isLoading.value = true
        viewModelScope.launch {
            delay(1000)
            userRepository.login("tmp", "tmp")
            _isLoading.value = false
        }
    }

}
