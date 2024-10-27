package com.example.mybottomnavigation.ui.setting

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class SettingPreference private constructor(private val dataStore: DataStore<Preferences>) {

    private val themeKey = booleanPreferencesKey(THEME_KEY)
    private val notificationKey = booleanPreferencesKey(NOTIFICATION_KEY)

    fun getThemeSetting(): Flow<Boolean> {
        return dataStore.data.map { preferences ->
            preferences[themeKey] ?: false
        }
    }

    suspend fun saveThemeSetting(isDarkModeActive: Boolean) {
        dataStore.edit { preferences ->
            preferences[themeKey] = isDarkModeActive
        }
    }

    fun getNotificationSetting(): Flow<Boolean> {
        return dataStore.data.map { preferences ->
            preferences[notificationKey] ?: false
        }
    }

    suspend fun saveNotificationSetting(isEnabled: Boolean) {
        dataStore.edit { preferences ->
            preferences[notificationKey] = isEnabled
        }
    }

    companion object {

        private const val THEME_KEY = "theme_setting"
        private const val NOTIFICATION_KEY = "notification_setting"

        @Volatile
        private var INSTANCE: SettingPreference? = null

        fun getInstance(
            dataStore: DataStore<Preferences>
        ): SettingPreference {
            return INSTANCE ?: synchronized(this) {
                val instance = SettingPreference(dataStore)
                INSTANCE = instance
                instance
            }
        }
    }
}