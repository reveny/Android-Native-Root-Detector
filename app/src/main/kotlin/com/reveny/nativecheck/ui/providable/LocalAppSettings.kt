package com.reveny.nativecheck.ui.providable

import androidx.compose.runtime.compositionLocalOf
import com.reveny.nativecheck.datastore.model.AppSettings

val LocalAppSettings = compositionLocalOf { AppSettings() }