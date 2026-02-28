package com.example.weather.presentation.settings.viewmodel

import android.util.Log
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.weather.data.datasources.remote.network.MyResult
import com.example.weather.data.models.map.CityResponse
import com.example.weather.data.models.map.CityResponseItem
import com.example.weather.data.repo.NetworkRepository
import com.example.weather.presentation.home.viewmodel.HomeViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class SettingsViewModel(
    private val homeViewModel: HomeViewModel,
    private val repository: NetworkRepository
) : ViewModel() {

    private val _tempUnit = MutableStateFlow("metric")
    val tempUnit: StateFlow<String> = _tempUnit

    private val _windUnit = MutableStateFlow("m/s")
    val windUnit: StateFlow<String> = _windUnit

    private val _language = MutableStateFlow("English")
    val language: StateFlow<String> = _language

    private val _isGpsEnabled = MutableStateFlow(false)
    val isGpsEnabled: StateFlow<Boolean> = _isGpsEnabled


    private val _isMapVisible = MutableStateFlow(false)
    val isMapVisible: StateFlow<Boolean> = _isMapVisible

    private val _citySuggestions = MutableStateFlow<MyResult<List<CityResponseItem>>>(MyResult.Success(emptyList()))
    val citySuggestions = _citySuggestions.asStateFlow()







    fun showMap() {
        _isMapVisible.value = true
    }

    fun hideMap() {
        _isMapVisible.value = false
    }
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

    fun updateLocationFromGPS(lat: Double, lon: Double) {
        homeViewModel.fetchWeatherForLocation(lat, lon)
    }

    fun toggleGps(enabled: Boolean) {
        _isGpsEnabled.value = enabled
    }


    fun updateWindUnit(unit: String) {
        _windUnit.value = unit
        homeViewModel.fetchWeatherWithNewSettings(units=unit)
    }

    fun getWeatherByMaps(latitude: Double, longitude: Double) {
        homeViewModel.fetchWeatherForLocation(latitude, longitude)
        hideMap()

    }


    fun searchCities(query: String) {
        Log.d("MapSearch", "Searching for: $query")
        if (query.trim().length < 3) {
            _citySuggestions.value = MyResult.Success(emptyList())
            return
        }

        viewModelScope.launch {
            _citySuggestions.value = MyResult.Loading
            try {
                val results = repository.getCitySuggestions(query= query,8, apiKey = "a50b3547c713e7be1ec57c696006497f")
                Log.d("MapSearch", "Results count: ${results}")
                _citySuggestions.value = results
            } catch (e: Exception) {
                Log.e("MapSearch", "Error: ${e.message}")
                _citySuggestions.value = MyResult.Error(e.message ?: "Unknown error")
            }
        }
    }

    fun onCitySelected(city: CityResponseItem?) {
        getWeatherByMaps(city?.lat?: 0.0, city?.lon?:0.0)
        _citySuggestions.value = MyResult.Success(emptyList())
    }
}