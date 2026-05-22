package fr.taoufikcode.data.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.core.handlers.ReplaceFileCorruptionHandler
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.preferencesDataStoreFile
import androidx.room.Room
import fr.taoufikcode.data.smartphones.local.SmartphoneDatabase
import fr.taoufikcode.data.smartphones.local.dao.HomeDao
import org.koin.core.annotation.Module
import org.koin.core.annotation.Singleton

@Module
class LocalDataModule {
    // --- Local Database ---
    @Singleton
    fun provideSmartphoneDatabase(context: Context): SmartphoneDatabase =
        Room
            .databaseBuilder(context, SmartphoneDatabase::class.java, "smartphone_database")
            .fallbackToDestructiveMigration(false)
            .build()

    @Singleton
    fun provideHomeDao(database: SmartphoneDatabase): HomeDao = database.homeListDao()

    // --- DataStore  preferences ---

    @Singleton
    fun providePreferencesDataStore(context: Context): DataStore<Preferences> =
        PreferenceDataStoreFactory.create(
            corruptionHandler = ReplaceFileCorruptionHandler { emptyPreferences() },
            produceFile = { context.preferencesDataStoreFile("app_prefs") },
        )
}
