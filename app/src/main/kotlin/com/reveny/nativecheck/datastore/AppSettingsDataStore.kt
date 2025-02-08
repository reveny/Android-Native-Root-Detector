package com.reveny.nativecheck.datastore

import androidx.datastore.core.DataStore
import com.reveny.nativecheck.datastore.model.*
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class AppSettingsDataStore @Inject constructor(
    private val appSettings: DataStore<AppSettings>
) {
    val data: Flow<AppSettings> = appSettings.data

    private suspend fun update(prefs: AppSettings.() -> AppSettings) {
        appSettings.updateData { it.prefs() }
    }

    suspend fun setLanguage(value: Language) = update { copy(language = value) }
    suspend fun setThemeColor(value: ThemeColor) = update { copy(themeColor = value) }
    suspend fun setDynamicColor(value: Boolean) = update { copy(dynamicColor = value) }
    suspend fun setDarkTheme(value: Boolean) = update { copy(darkTheme = value) }
    suspend fun setContrastMode(value: Boolean) = update { copy(highContrast = value) }
    suspend fun setDisableTelemetry(value: Boolean) = update { copy(disableTelemetry = value) }
    suspend fun setEnableExperimentalDetections(value: Boolean) = update { copy(enableExperimentalDetections = value) }
}