package com.reveny.nativecheck.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import com.reveny.nativecheck.datastore.model.Language
import com.reveny.nativecheck.datastore.model.ThemeColor
import com.reveny.nativecheck.repository.AppSettingsRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val appSettingsRepository: AppSettingsRepository,
    @ApplicationContext private val context: Context
) : ViewModel() {
    fun setLanguage(language: Language) {
        viewModelScope.launch {
            appSettingsRepository.setLanguage(language)
        }
    }

    fun setThemeColor(themeColor: ThemeColor) {
        viewModelScope.launch {
            appSettingsRepository.setThemeColor(themeColor)
        }
    }

    fun setDynamicColor(dynamicColor: Boolean) {
        viewModelScope.launch {
            appSettingsRepository.setDynamicColor(dynamicColor)
        }
    }

    fun setDarkTheme(mode: Boolean) {
        viewModelScope.launch {
            appSettingsRepository.setDarkTheme(mode)
        }
    }

    fun setContrastMode(contrast: Boolean) {
        viewModelScope.launch {
            appSettingsRepository.setContrastMode(contrast)
        }
    }

    fun setDisableTelemetry(disableTelemetry: Boolean) {
        viewModelScope.launch {
            appSettingsRepository.setDisableTelemetry(disableTelemetry)
        }
    }

    fun setEnableExperimentalDetections(enableExperimentalDetections: Boolean) {
        viewModelScope.launch {
            appSettingsRepository.setEnableExperimentalDetections(enableExperimentalDetections)
        }
    }
}