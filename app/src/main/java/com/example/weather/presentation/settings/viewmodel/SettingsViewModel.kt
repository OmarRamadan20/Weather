package com.example.weather.presentation.settings.viewmodel

import android.util.Log
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.weather.presentation.home.viewmodel.HomeViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class SettingsViewModel(
    private val homeViewModel: HomeViewModel
) : ViewModel() {

    private val _tempUnit = MutableStateFlow("Celsius")
    val tempUnit: StateFlow<String> = _tempUnit

    private val _windUnit = MutableStateFlow("m/s")
    val windUnit: StateFlow<String> = _windUnit

    private val _language = MutableStateFlow("English")
    val language: StateFlow<String> = _language

    private val _isGpsEnabled = MutableStateFlow(true)
    val isGpsEnabled: StateFlow<Boolean> = _isGpsEnabled

    fun updateTempUnit(unit: String) {
        _tempUnit.value = unit
        val apiUnit = when (unit) {
            "Fahrenheit" -> "imperial"
            "Kelvin" -> "standard"
            else -> "metric"
        }
        homeViewModel.fetchWeatherWithNewSettings(units = apiUnit)
    }

    fun updateLanguage(selectedName: String) {
        // 1. حول الـ String اللي جالك للكود المناسب
        val langCode = when (selectedName) {
            "Arabic", "العربية" -> "ar"
            "English", "الإنجليزية" -> "en"
            else -> "ar"
        }
        Log.d("SettingsViewModel", "Selected Language: $selectedName, Lang Code: $langCode")

        _language.value = selectedName

        val appLocale: LocaleListCompat = LocaleListCompat.forLanguageTags(langCode)
        AppCompatDelegate.setApplicationLocales(appLocale)

        homeViewModel.fetchWeatherWithNewSettings(lang = langCode)
    }

    fun toggleGps(enabled: Boolean) {
        _isGpsEnabled.value = enabled
        // لو قفل الـ GPS ممكن تفتح شاشة الخريطة مثلاً
    }

    fun updateWindUnit(unit: String) {
        _windUnit.value = unit
        homeViewModel.fetchWeatherWithNewSettings(units=unit)

    }
}