package com.reveny.nativecheck.datastore

import androidx.datastore.core.CorruptionException
import androidx.datastore.core.Serializer
import com.reveny.nativecheck.datastore.model.AppSettings
import kotlinx.serialization.SerializationException
import java.io.InputStream
import java.io.OutputStream
import javax.inject.Inject

class AppSettingsSerializer @Inject constructor() : Serializer<AppSettings> {
    override val defaultValue = AppSettings()

    override suspend fun readFrom(input: InputStream) =
        try {
            AppSettings.Companion.decodeFrom(input)
        } catch (e: SerializationException) {
            throw CorruptionException("Failed to read proto", e)
        }

    override suspend fun writeTo(t: AppSettings, output: OutputStream) {
        t.encodeTo(output)
    }
}
