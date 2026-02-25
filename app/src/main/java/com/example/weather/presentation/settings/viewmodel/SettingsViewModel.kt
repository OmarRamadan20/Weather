package com.example.weather.presentation.settings.viewmodel

import android.util.Log
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat
import androidx.lifecycle.ViewModel
import com.example.weather.presentation.home.viewmodel.HomeViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class SettingsViewModel(
    private val homeViewModel: HomeViewModel
) : ViewModel() {

    private val _tempUnit = MutableStateFlow("metric")
    val tempUnit: StateFlow<String> = _tempUnit

    private val _windUnit = MutableStateFlow("m/s")
    val windUnit: StateFlow<String> = _windUnit

    private val _language = MutableStateFlow("English")
    val language: StateFlow<String> = _language

    private val _isGpsEnabled = MutableStateFlow(true)
    val isGpsEnabled: StateFlow<Boolean> = _isGpsEnabled




    fun updateUnits(apiUnit: String) {
        _tempUnit.value = apiUnit
        homeViewModel.fetchWeatherWithNewSettings(units = apiUnit, lang = language.value)
    }

    fun updateLanguage(langName: String) {
        _language.value = langName

        val langCode = when (langName) {
            "ar" -> "ar"
            "en" -> "en"
            else -> "ar"
        }


        val appLocale: LocaleListCompat = LocaleListCompat.forLanguageTags(langCode)
        AppCompatDelegate.setApplicationLocales(appLocale)

        Log.d("SettingsViewModel", "${_tempUnit.value}")

        homeViewModel.fetchWeatherWithNewSettings(lang = langCode, units = _tempUnit.value)
    }

    fun toggleGps(enabled: Boolean) {
        _isGpsEnabled.value = enabled
        //Open GPS
    }

    fun updateWindUnit(unit: String) {
        _windUnit.value = unit
        homeViewModel.fetchWeatherWithNewSettings(units=unit)
    }

    fun getWeatherByMaps(){
        homeViewModel.fetchWeatherWithNewSettings(units = tempUnit.value, lang = language.value)

    }
}