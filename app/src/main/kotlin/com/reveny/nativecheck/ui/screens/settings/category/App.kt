package com.reveny.nativecheck.ui.screens.settings.category

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.navigation.NavHostController
import com.reveny.nativecheck.viewmodel.SettingsViewModel
import compose.icons.TablerIcons
import compose.icons.tablericons.Outline
import compose.icons.tablericons.outline.ChevronLeft
import com.reveny.nativecheck.R
import com.reveny.nativecheck.ui.component.SettingSwitchItem
import com.reveny.nativecheck.ui.providable.LocalAppSettings
import compose.icons.tablericons.outline.ShieldOff
import compose.icons.tablericons.outline.TestPipe

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun App(
    navController: NavController,
    viewModel: SettingsViewModel = hiltViewModel()
) {
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior(
        rememberTopAppBarState(),
        canScroll = { true }
    )
    val appSettings = LocalAppSettings.current

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            LargeTopAppBar(
                title = { Text(stringResource(R.string.settings_app)) },
                scrollBehavior = scrollBehavior,
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            TablerIcons.Outline.ChevronLeft,
                            contentDescription = null
                        )
                    }
                },
            )
        }
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(it)
        ) {
            item {
                Language(
                    language = appSettings.language,
                    onLanguageChange = viewModel::setLanguage
                )
            }
            item {
                SettingSwitchItem(
                    title = stringResource(R.string.pref_telemetry),
                    icon = TablerIcons.Outline.ShieldOff,
                    isChecked = appSettings.disableTelemetry,
                    onClick = { viewModel.setDisableTelemetry(!appSettings.disableTelemetry) }
                )
            }
            item {
                SettingSwitchItem(
                    title = stringResource(R.string.pref_experimental),
                    icon = TablerIcons.Outline.TestPipe,
                    isChecked = appSettings.enableExperimentalDetections,
                    onClick = { viewModel.setEnableExperimentalDetections(!appSettings.enableExperimentalDetections) }
                )
            }
        }
    }
}
