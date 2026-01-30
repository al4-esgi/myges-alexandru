package com.vlxx.myges.data.repositories

import com.vlxx.myges.data.dtos.test.dummyUser
import com.vlxx.myges.data.network.Api
import com.vlxx.myges.domain.enums.AppState
import com.vlxx.myges.domain.repositories.LocalSettingsRepository
import com.vlxx.myges.domain.repositories.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class UserRepositoryImpl(
    private val api: Api,
    private val localSettingsRepository: LocalSettingsRepository
) : UserRepository {

    private val _appState = MutableStateFlow(AppState.SPLASH)
    override val appState: StateFlow<AppState> = _appState.asStateFlow()

    override suspend fun login(email: String, password: String) {
         try {
//            val dto = api.login(email)
            val dto = dummyUser
            localSettingsRepository.setAccessToken(dto.accessToken)
            setAppState(AppState.SPLASH)
        } catch (e: Exception) {
            throw e
        }
    }

    override fun updateAppStateFromAccess(accessToken: String?) {
        setAppState(if (accessToken.isNullOrEmpty()) AppState.UNAUTHENTICATED else AppState.AUTHENTICATED)
    }

    override fun setAppState(state: AppState) {
        _appState.value = state
    }

    override suspend fun logout() {
        localSettingsRepository.clearAccessToken()
        setAppState(AppState.UNAUTHENTICATED)
    }

}
