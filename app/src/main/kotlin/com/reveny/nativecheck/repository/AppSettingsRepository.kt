package com.reveny.nativecheck.repository

import android.content.Context
import android.content.res.Configuration
import com.reveny.nativecheck.datastore.AppSettingsDataStore
import com.reveny.nativecheck.datastore.model.Language
import com.reveny.nativecheck.datastore.model.ThemeColor
import javax.inject.Inject

class AppSettingsRepository @Inject constructor(
    private val userPreferencesDataSource: AppSettingsDataStore,
) {
    val data get() = userPreferencesDataSource.data

    suspend fun setLanguage(value: Language) = userPreferencesDataSource.setLanguage(value)
    suspend fun setThemeColor(value: ThemeColor) = userPreferencesDataSource.setThemeColor(value)
    suspend fun setDynamicColor(value: Boolean) = userPreferencesDataSource.setDynamicColor(value)
    suspend fun setDarkTheme(value: Boolean) = userPreferencesDataSource.setDarkTheme(value)
    suspend fun setContrastMode(value: Boolean) = userPreferencesDataSource.setContrastMode(value)
    suspend fun setDisableTelemetry(value: Boolean) = userPreferencesDataSource.setDisableTelemetry(value)
    suspend fun setEnableExperimentalDetections(value: Boolean) = userPreferencesDataSource.setEnableExperimentalDetections(value)

    private fun isSystemDarkTheme(context: Context): Boolean {
        val uiMode = context.resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK
        return uiMode == Configuration.UI_MODE_NIGHT_YES
    }

    suspend fun updateDarkThemeBasedOnSystem(context: Context) {
        val isSystemDarkTheme = isSystemDarkTheme(context)
        userPreferencesDataSource.setDarkTheme(isSystemDarkTheme)
    }
}