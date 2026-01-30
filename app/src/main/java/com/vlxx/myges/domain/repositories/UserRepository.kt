package com.vlxx.myges.domain.repositories

import com.vlxx.myges.domain.enums.AppState
import kotlinx.coroutines.flow.StateFlow

interface UserRepository {
    val appState: StateFlow<AppState>

    suspend fun login(email: String, password: String)
    fun setAppState(state: AppState)
    fun updateAppStateFromAccess(accessToken: String?)
    suspend fun logout()
}