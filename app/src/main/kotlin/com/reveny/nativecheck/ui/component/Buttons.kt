package com.reveny.nativecheck.ui.component

import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import compose.icons.TablerIcons
import compose.icons.tablericons.Outline
import compose.icons.tablericons.outline.ChevronLeft

@Composable
fun NavBackButton(navController: NavController) {
    IconButton(onClick = { navController.popBackStack() }) {
        Icon(TablerIcons.Outline.ChevronLeft, null)
    }
}