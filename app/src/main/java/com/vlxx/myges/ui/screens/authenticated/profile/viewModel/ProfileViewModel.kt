package com.vlxx.myges.ui.screens.authenticated.profile.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vlxx.myges.data.dtos.ProfileResponseDto
import com.vlxx.myges.domain.repositories.ProfileRepository
import com.vlxx.myges.domain.repositories.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import timber.log.Timber

sealed class ProfileUiState {
    object Loading : ProfileUiState()
    data class Success(val profile: ProfileResponseDto) : ProfileUiState()
    data class Error(val message: String) : ProfileUiState()
}

class ProfileViewModel(
    private val profileRepository: ProfileRepository,
    private val userRepository: UserRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<ProfileUiState>(ProfileUiState.Loading)
    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()

    init {
        loadProfile()
    }

    fun loadProfile() {
        _uiState.value = ProfileUiState.Loading
        Timber.d("Loading profile...")
        viewModelScope.launch {
            try {
                val profile = profileRepository.getProfile()
                Timber.d("Profile loaded successfully: uid=${profile.uid}, name=${profile.firstname} ${profile.name}")
                _uiState.value = ProfileUiState.Success(profile)
            } catch (e: Exception) {
                Timber.e(e, "Error loading profile - Message: ${e.message}")
                e.printStackTrace()
                _uiState.value = ProfileUiState.Error(e.message ?: "Unknown error")
            }
        }
    }

    fun logout() {
        viewModelScope.launch {
            try {
                Timber.d("Logging out...")
                userRepository.logout()
            } catch (e: Exception) {
                Timber.e(e, "Error during logout")
            }
        }
    }
}
