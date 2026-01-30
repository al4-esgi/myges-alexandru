package com.vlxx.myges.domain.repositories

interface LocalSettingsRepository {
    suspend fun setAccessToken(token: String)
    suspend fun clearAccessToken()
    fun getAccessToken() : String?
}