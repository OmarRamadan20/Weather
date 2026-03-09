package com.example.weather.presentation.settings.viewmodel

import android.content.Context
import androidx.datastore.preferences.core.doublePreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore(name = "settings")

class SettingsPreferences(context: Context) {
    private val dataStore = context.dataStore

    companion object {
        val TEMP_UNIT = stringPreferencesKey("temp_unit")
        val WIND_UNIT = stringPreferencesKey("wind_unit")
        val LANGUAGE = stringPreferencesKey("language")

        val LATITUDE = doublePreferencesKey("latitude")
        val LONGITUDE = doublePreferencesKey("longitude")

    }

    val temperatureUnit: Flow<String> = dataStore.data.map { it[TEMP_UNIT] ?: "metric" }
    val windUnit: Flow<String> = dataStore.data.map { it[WIND_UNIT] ?: "metric" }
    val language: Flow<String> = dataStore.data.map { it[LANGUAGE] ?: "en" }

    suspend fun saveTempUnit(unit: String) {
        dataStore.edit { it[TEMP_UNIT] = unit }
    }

    suspend fun saveWindUnit(unit: String) {
        dataStore.edit { it[WIND_UNIT] = unit }
    }
    suspend fun saveLanguage(lang: String) {
        dataStore.edit { it[LANGUAGE] = lang }
        }

    val latitude: Flow<Double> = dataStore.data.map { it[LATITUDE] ?: 30.0444 }
    val longitude: Flow<Double> = dataStore.data.map { it[LONGITUDE] ?: 31.2357 }


    suspend fun saveLocation(lat: Double, lon: Double) {
        dataStore.edit { preferences ->
            preferences[LATITUDE] = lat
            preferences[LONGITUDE] = lon
        }
    }
}