package com.example.repnote.data.repository

import android.content.Context
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.repnote.model.RestTimerSnapshot
import kotlinx.serialization.json.Json
import com.example.repnote.model.ThemeMode
import com.example.repnote.model.AppLanguage
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.settingsDataStore: DataStore<Preferences>
        by preferencesDataStore(name = "settings")

class SettingsRepository(context: Context) {
    private val dataStore =
        context.applicationContext.settingsDataStore

    private val themeModeKey =
        stringPreferencesKey("theme_mode")

    val bootCount: Int = android.provider.Settings.Global.getInt(
        context.applicationContext.contentResolver, android.provider.Settings.Global.BOOT_COUNT, -1
    )
    private val timerKey = stringPreferencesKey("rest_timer")

    private fun readTimer(preferences: Preferences): RestTimerSnapshot {
        val json = preferences[timerKey] ?: return RestTimerSnapshot()
        return try {
            Json.decodeFromString<RestTimerSnapshot>(json).let {
                if (it.durationSeconds !in listOf(60, 90, 120) || it.remainingSeconds !in 0..it.durationSeconds) RestTimerSnapshot() else it
            }
        } catch (error: kotlinx.serialization.SerializationException) {
            RestTimerSnapshot()
        }
    }

    fun observeRestTimer(): Flow<RestTimerSnapshot> = dataStore.data.map(::readTimer)

    suspend fun updateRestTimer(transform: (RestTimerSnapshot) -> RestTimerSnapshot) {
        dataStore.edit { preferences ->
            preferences[timerKey] = Json.encodeToString(transform(readTimer(preferences)))
        }
    }

    private val language = MutableStateFlow(AppLanguage.SYSTEM)

    fun refreshLanguage() {
        val tag = AppCompatDelegate.getApplicationLocales().toLanguageTags().substringBefore(',')
        language.value = AppLanguage.entries.firstOrNull { it.tag == tag } ?: AppLanguage.SYSTEM
    }

    fun observeLanguage(): Flow<AppLanguage> = language.asStateFlow()

    suspend fun setLanguage(value: AppLanguage) = withContext(Dispatchers.Main.immediate) {
        AppCompatDelegate.setApplicationLocales(LocaleListCompat.forLanguageTags(value.tag))
        language.value = value
    }

    suspend fun reset() {
        dataStore.edit { it.clear() }
        setLanguage(AppLanguage.SYSTEM)
    }

    fun observeThemeMode(): Flow<ThemeMode> {

        val preferences = dataStore.data.map { preferences ->

            val savedValue = preferences[themeModeKey]

            ThemeMode.entries.firstOrNull { mode ->
                mode.name == savedValue
            } ?: ThemeMode.SYSTEM
        }

        return preferences.distinctUntilChanged()
    }

    suspend fun setThemeMode(mode: ThemeMode) {
        dataStore.edit { preferences ->
            preferences[themeModeKey] = mode.name
        }
    }
}
