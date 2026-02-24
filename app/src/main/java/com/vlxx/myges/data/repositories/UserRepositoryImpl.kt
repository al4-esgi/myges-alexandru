package com.vlxx.myges.data.repositories

import com.vlxx.myges.data.network.Api
import com.vlxx.myges.data.network.AuthInterceptor
import com.vlxx.myges.domain.enums.AppState
import com.vlxx.myges.domain.repositories.LocalSettingsRepository
import com.vlxx.myges.domain.repositories.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import timber.log.Timber
import java.util.regex.Pattern

class UserRepositoryImpl(
    private val api: Api,
    private val localSettingsRepository: LocalSettingsRepository,
    private val authInterceptor: AuthInterceptor
) : UserRepository {

    private val _appState = MutableStateFlow(AppState.SPLASH)
    override val appState: StateFlow<AppState> = _appState.asStateFlow()

    override suspend fun login(email: String, password: String) {
        try {
            // Create Basic Auth header
            val basicAuth = AuthInterceptor.createBasicAuth(email, password)

            // Call authentication endpoint
            val response = api.authenticate(basicAuth)

            // Extract token from Location header
            val locationHeader = response.headers()["Location"]
            Timber.d("Location header: $locationHeader")

            if (locationHeader.isNullOrEmpty()) {
                throw Exception("No location header found in authentication response")
            }

            // Parse token from redirect URL
            // Format: comreseaugesskolae:/oauth2redirect#access_token=TOKEN&token_type=bearer
            val pattern = Pattern.compile("access_token=([^&]+)")
            val matcher = pattern.matcher(locationHeader)

            if (!matcher.find()) {
                throw Exception("Token not found in location header")
            }

            val token = matcher.group(1) ?: throw Exception("Failed to extract token")
            Timber.d("Token acquired successfully")

            // Save token and update interceptor
            localSettingsRepository.setAccessToken(token)
            authInterceptor.setToken(token)

            // Get user profile to verify authentication
            val profile = api.getProfile()
            Timber.d("Profile loaded: ${profile.result.firstname} ${profile.result.lastname}")

            setAppState(AppState.AUTHENTICATED)
        } catch (e: Exception) {
            Timber.e(e, "Login failed")
            throw e
        }
    }

    override fun updateAppStateFromAccess(accessToken: String?) {
        if (!accessToken.isNullOrEmpty()) {
            authInterceptor.setToken(accessToken)
        }
        setAppState(if (accessToken.isNullOrEmpty()) AppState.UNAUTHENTICATED else AppState.AUTHENTICATED)
    }

    override fun setAppState(state: AppState) {
        _appState.value = state
    }

    override suspend fun logout() {
        localSettingsRepository.clearAccessToken()
        authInterceptor.setToken(null)
        setAppState(AppState.UNAUTHENTICATED)
    }

}
