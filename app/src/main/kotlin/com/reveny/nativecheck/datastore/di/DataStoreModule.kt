package com.reveny.nativecheck.datastore.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.core.DataStoreFactory
import androidx.datastore.dataStoreFile
import com.reveny.nativecheck.datastore.AppSettingsSerializer
import com.reveny.nativecheck.datastore.model.AppSettings
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DataStoreModule {
    @Provides
    @Singleton
    fun providesUserPreferencesDataStore(
        @ApplicationContext context: Context,
        appSettingsSerializer: AppSettingsSerializer
    ): DataStore<AppSettings> =
        DataStoreFactory.create(
            serializer = appSettingsSerializer
        ) {
            context.dataStoreFile("app_settings.pb")
        }
}