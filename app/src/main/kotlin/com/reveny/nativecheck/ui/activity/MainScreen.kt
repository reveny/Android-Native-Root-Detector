package com.reveny.nativecheck.ui.activity

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavController
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.reveny.nativecheck.ui.navigation.MainScreen
import com.reveny.nativecheck.ui.navigation.graphs.homeScreen
import com.reveny.nativecheck.ui.navigation.graphs.settingsScreen
import com.reveny.nativecheck.viewmodel.MainViewModel

@Composable
fun MainScreen(viewModel: MainViewModel) {
    val navController = rememberNavController()
    val isLoading by viewModel.isLoading.collectAsState()
    val detections by viewModel.detections.collectAsState()

    if (isLoading) {
        // Show the loading screen while loading
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator(
                color = MaterialTheme.colorScheme.primary
            )
        }
    } else {
        // Show the main content when loading is complete
        Scaffold(
            bottomBar = {
                BottomNav(navController = navController)
            },
            contentWindowInsets = WindowInsets.navigationBars
        ) {
            NavHost(
                modifier = Modifier
                    .padding(bottom = it.calculateBottomPadding()),
                navController = navController,
                startDestination = MainScreen.Home.route,
            ) {
                homeScreen(
                    navController = navController,
                    viewModel = viewModel,
                    detections = detections
                )
                settingsScreen(navController = navController)
            }
        }
    }
}

@Composable
private fun BottomNav(
    navController: NavController,
) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination
    val mainScreens = MainScreen.entries.toTypedArray()

    NavigationBar(
        modifier = Modifier.imePadding()
    ) {
        mainScreens.forEach { screen ->
            val selected = currentDestination?.hierarchy?.any { it.route == screen.route } == true

            NavigationBarItem(
                icon = {
                    Icon(
                        imageVector = if (selected) screen.iconFilled else screen.icon,
                        contentDescription = null,
                    )
                },
                label = {
                    Text(
                        text = stringResource(id = screen.label),
                        style = MaterialTheme.typography.labelMedium
                    )
                },
                alwaysShowLabel = true,
                selected = selected,
                onClick = { if (!selected) navController.navigate(screen.route) }
            )
        }
    }
}