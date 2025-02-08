package com.reveny.nativecheck.ui.navigation.graphs

import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.reveny.nativecheck.ui.screens.settings.Settings
import com.reveny.nativecheck.ui.navigation.MainScreen
import com.reveny.nativecheck.ui.screens.settings.category.App
import com.reveny.nativecheck.ui.screens.settings.category.Appearance

enum class SettingsScreen(
    val route: String
) {
    Home("Settings"),
    App("App"),
    Appearance("Appearance"),
}

fun NavGraphBuilder.settingsScreen(
    navController: NavController
) = navigation(
    startDestination = SettingsScreen.Home.route,
    route = MainScreen.Settings.route
) {
    composable(
        route = SettingsScreen.Home.route,
        enterTransition = { fadeIn() },
        exitTransition = { fadeOut() }
    ) {
        Settings(
            navController = navController
        )
    }
    composable(
        route = SettingsScreen.App.route,
        enterTransition = { fadeIn() },
        exitTransition = { fadeOut() }
    ) {
        App(navController = navController)
    }
    composable(
        route = SettingsScreen.Appearance.route,
        enterTransition = { fadeIn() },
        exitTransition = { fadeOut() }
    ) {
        Appearance(navController = navController)
    }
}