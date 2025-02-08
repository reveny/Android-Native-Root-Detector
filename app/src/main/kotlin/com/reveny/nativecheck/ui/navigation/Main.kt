package com.reveny.nativecheck.ui.navigation

import androidx.annotation.StringRes
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.ui.graphics.vector.ImageVector
import com.reveny.nativecheck.R

enum class MainScreen(
    val route: String,
    @StringRes val label: Int,
    val icon: ImageVector,
    val iconFilled: ImageVector
) {
    Home(
        route = "HomeScreen",
        label = R.string.home_label,
        icon = Icons.Outlined.Home,
        iconFilled = Icons.Filled.Home
    ),

    Settings(
        route = "SettingsScreen",
        label = R.string.settings_label,
        icon = Icons.Outlined.Settings,
        iconFilled = Icons.Filled.Settings
    )
}