@file:OptIn(ExperimentalSerializationApi::class)

package com.reveny.nativecheck.datastore.model

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializable
import kotlinx.serialization.decodeFromByteArray
import kotlinx.serialization.encodeToByteArray
import kotlinx.serialization.protobuf.ProtoBuf
import java.io.InputStream
import java.io.OutputStream

@Serializable
data class AppSettings(
    val language: Language = Language.SYSTEM_DEFAULT,
    val dynamicColor: Boolean = true,
    val themeColor: ThemeColor = ThemeColor.CoralBurst,
    val darkTheme: Boolean = false,
    val highContrast: Boolean = false,
    val disableTelemetry: Boolean = false,
    val enableExperimentalDetections: Boolean = false
) {
    fun encodeTo(output: OutputStream) = output.write(
        ProtoBuf.encodeToByteArray(this)
    )

    companion object {
        fun decodeFrom(input: InputStream): AppSettings =
            ProtoBuf.decodeFromByteArray(input.readBytes())
    }

    fun updateDarkThemeBasedOnSystem(isSystemDarkTheme: Boolean): AppSettings {
        return this.copy(darkTheme = isSystemDarkTheme)
    }
}