package com.vlxx.myges.ui.screens.splash.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vlxx.myges.domain.repositories.LocalSettingsRepository
import com.vlxx.myges.domain.repositories.UserRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class SplashViewModel(
    private val userRepository: UserRepository,
    private val localSettingsRepository: LocalSettingsRepository
) : ViewModel() {

    fun fetch() {
        viewModelScope.launch {
            delay(1000)
            val accessToken = localSettingsRepository.getAccessToken()
            userRepository.updateAppStateFromAccess(accessToken)
        }
    }

}
