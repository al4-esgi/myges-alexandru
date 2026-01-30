package com.vlxx.myges.ui.screens.authenticated.navigation

import kotlinx.serialization.Serializable

@Serializable
object HomeRoute

@Serializable
data class ProfileRoute(val userId: String? = null)

