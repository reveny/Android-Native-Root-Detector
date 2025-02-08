package com.reveny.nativecheck.ui.screens.settings

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.reveny.nativecheck.R
import com.reveny.nativecheck.ui.component.SettingItem
import com.reveny.nativecheck.ui.component.SettingSubtitleItem
import com.reveny.nativecheck.ui.component.SettingSwitchItem
import com.reveny.nativecheck.viewmodel.SettingsViewModel
import compose.icons.TablerIcons
import compose.icons.tablericons.Filled
import compose.icons.tablericons.Outline
import compose.icons.tablericons.filled.Palette
import compose.icons.tablericons.filled.Settings
import compose.icons.tablericons.outline.ColorPicker
import compose.icons.tablericons.outline.Contrast
import compose.icons.tablericons.outline.Moon

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Settings(
    navController: NavController,
    viewModel: SettingsViewModel = hiltViewModel()
) {
    val scrollBehavior =
        TopAppBarDefaults.exitUntilCollapsedScrollBehavior(
            rememberTopAppBarState(),
            canScroll = { true })

    Scaffold(
        modifier = Modifier
        .fillMaxSize()
        .nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            LargeTopAppBar(
                title = { Text(stringResource(R.string.settings)) },
                scrollBehavior = scrollBehavior,
            )
        }
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(it),
        ) {
            item {
                SettingItem(
                    title = stringResource(R.string.settings_app),
                    description = "Language, and more",
                    icon = TablerIcons.Filled.Settings,
                    onClick = {
                        navController.navigate("App")
                    }
                )
            }
            item {
                SettingItem(
                    title = "Appearance",
                    description = "Theme, color, and more",
                    icon = TablerIcons.Filled.Palette,
                    onClick = {
                        navController.navigate("Appearance")
                    }
                )
            }
        }
    }
}