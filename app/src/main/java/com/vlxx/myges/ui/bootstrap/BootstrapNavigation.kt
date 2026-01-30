package com.vlxx.myges.ui.bootstrap

import androidx.compose.animation.Crossfade
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.vlxx.myges.domain.enums.AppState
import com.vlxx.myges.domain.repositories.UserRepository
import com.vlxx.myges.ui.screens.authenticated.navigation.AuthenticatedNavigation
import com.vlxx.myges.ui.screens.splash.SplashScreen
import com.vlxx.myges.ui.screens.unauthenticated.signInScreen.screen.SignInScreen
import org.koin.compose.koinInject


@Composable
fun BootstrapNavigation(
    userRepository: UserRepository = koinInject()
) {
    val state by userRepository.appState.collectAsStateWithLifecycle()

    Crossfade(targetState = state) { appState ->
        when (appState) {
            AppState.SPLASH -> SplashScreen()
            AppState.UNAUTHENTICATED -> SignInScreen()
            AppState.AUTHENTICATED -> AuthenticatedNavigation()
        }
    }
}