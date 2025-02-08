package com.reveny.nativecheck.ui.navigation.graphs

import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.reveny.nativecheck.app.DetectionData
import com.reveny.nativecheck.ui.screens.home.Home
import com.reveny.nativecheck.ui.navigation.MainScreen
import com.reveny.nativecheck.viewmodel.MainViewModel

enum class HomeScreen(
    val route: String
) {
    Home("Home"),
}

fun NavGraphBuilder.homeScreen(
    navController: NavController,
    viewModel: MainViewModel,
    detections: List<DetectionData>
) = navigation(
    startDestination = HomeScreen.Home.route,
    route = MainScreen.Home.route
) {
    composable(
        route = HomeScreen.Home.route,
        enterTransition = { fadeIn() },
        exitTransition = { fadeOut() }
    ) {
        Home(
            navController = navController,
            viewModel = viewModel,
            detections = detections
        )
    }
}