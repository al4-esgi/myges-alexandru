package com.vlxx.myges.ui.screens.authenticated.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.vlxx.myges.ui.screens.authenticated.home.HomeScreen

@Composable
fun AuthenticatedNavigation() {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = HomeRoute) {
        composable<HomeRoute> { HomeScreen() }

    }
}
