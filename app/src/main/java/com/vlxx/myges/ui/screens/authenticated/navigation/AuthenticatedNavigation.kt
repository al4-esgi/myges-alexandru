package com.vlxx.myges.ui.screens.authenticated.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.School
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.vlxx.myges.R
import com.vlxx.myges.ui.screens.authenticated.agenda.screen.AgendaScreen
import com.vlxx.myges.ui.screens.authenticated.grades.screen.GradesScreen
import com.vlxx.myges.ui.screens.authenticated.home.HomeScreen
import com.vlxx.myges.ui.screens.authenticated.profile.screen.ProfileScreen

sealed class BottomNavItem(
    val route: Any,
    val icon: ImageVector,
    val labelRes: Int
) {
    data object Home : BottomNavItem(HomeRoute, Icons.Default.Home, R.string.home_title)
    data object Agenda : BottomNavItem(AgendaRoute, Icons.Default.CalendarToday, R.string.agenda_title)
    data object Grades : BottomNavItem(GradesRoute, Icons.Default.School, R.string.grades_title)
    data object Profile : BottomNavItem(ProfileRoute, Icons.Default.Person, R.string.profile_title)
}

@Composable
fun AuthenticatedNavigation() {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    val bottomNavItems = listOf(
        BottomNavItem.Home,
        BottomNavItem.Agenda,
        BottomNavItem.Grades,
        BottomNavItem.Profile
    )

    Scaffold(
        bottomBar = {
            NavigationBar {
                bottomNavItems.forEach { item ->
                    NavigationBarItem(
                        icon = { Icon(item.icon, contentDescription = stringResource(item.labelRes)) },
                        label = { Text(stringResource(item.labelRes)) },
                        selected = currentDestination?.hierarchy?.any {
                            it.hasRoute(item.route::class)
                        } == true,
                        onClick = {
                            navController.navigate(item.route) {
                                // Pop up to the start destination to avoid building up a large stack
                                popUpTo(HomeRoute) {
                                    saveState = true
                                }
                                // Avoid multiple copies of the same destination
                                launchSingleTop = true
                                // Restore state when reselecting a previously selected item
                                restoreState = true
                            }
                        }
                    )
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = HomeRoute,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable<HomeRoute> {
                HomeScreen()
            }
            composable<AgendaRoute> {
                AgendaScreen()
            }
            composable<GradesRoute> {
                GradesScreen()
            }
            composable<ProfileRoute> {
                ProfileScreen()
            }
        }
    }
}
