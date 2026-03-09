package com.example.weather.presentation.home.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.weather.utils.MyResult
import com.example.weather.data.models.daily.DailyResponse
import com.example.weather.data.models.hourly.HourlyResponse
import com.example.weather.data.models.weather.WeatherResponse
import com.example.weather.data.repo.WeatherRepository
import com.example.weather.presentation.settings.viewmodel.SettingsPreferences
import com.example.weather.utils.NetworkObserver
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class HomeViewModel(private val repository: WeatherRepository,
                    private val settingsPreferences: SettingsPreferences
) : ViewModel() {

    private val _weatherState = MutableStateFlow<MyResult<WeatherResponse>>(MyResult.Loading)
    val weatherState = _weatherState.asStateFlow()

    private val _hourlyState = MutableStateFlow<MyResult<HourlyResponse>>(MyResult.Loading)
    val hourlyState = _hourlyState.asStateFlow()

    private val _dailyState = MutableStateFlow<MyResult<DailyResponse>>(MyResult.Loading)
    val dailyState = _dailyState.asStateFlow()

    val selectedLang = MutableStateFlow("en")

    val selectedUnit = MutableStateFlow("metric")

    private val _networkStatus = MutableStateFlow(NetworkObserver.Status.Available)
    val networkStatus = _networkStatus.asStateFlow()


    init {
        observeSettingsAndFetch()
    }

    private fun observeSettingsAndFetch() {
        viewModelScope.launch {
            combine(
                settingsPreferences.temperatureUnit,
                settingsPreferences.language,
                settingsPreferences.latitude,
                settingsPreferences.longitude,
                networkStatus
            ) { unit, lang, lat, lon, status ->
                if (status == NetworkObserver.Status.Available) {
                    fetchWeather(lat, lon, unit, lang)
                } else {
                    _weatherState.value = MyResult.Error("No Internet Connection")
                }
            }.collect()
        }
    }


    fun updateNetworkStatus(status: NetworkObserver.Status) {
        _networkStatus.value = status
    }
    fun fetchWeather(lat: Double, lon: Double, units: String, lang: String) {


        if (_networkStatus.value == NetworkObserver.Status.Lost) {
            val noNetMessage = "No Internet Connection"
            _weatherState.value = MyResult.Error(noNetMessage)
            _hourlyState.value = MyResult.Error(noNetMessage)
            _dailyState.value = MyResult.Error(noNetMessage)
            return
        }
        viewModelScope.launch {
            _weatherState.value = MyResult.Loading
            _hourlyState.value = MyResult.Loading
            _dailyState.value = MyResult.Loading

            try {
                val weatherResult = repository.getCurrentWeather(lat, lon, units, lang)
                val hourlyResponse = repository.getHourlyForecast(lat, lon, units)
                val dailyResponse = repository.dailyForecast(lat, lon, lang, units)

                _weatherState.value = weatherResult
                _hourlyState.value = hourlyResponse
                _dailyState.value = dailyResponse

                _weatherState.value = weatherResult

                _hourlyState.value = hourlyResponse

            } catch (e: Exception) {
                val errorMessage = e.message ?: "Unknown Network Error"
                _weatherState.value = MyResult.Error(errorMessage)
                _hourlyState.value = MyResult.Error(errorMessage)
                _dailyState.value = MyResult.Error(errorMessage)
            }
        }
    }


    fun refresh() {
        viewModelScope.launch {
            try {
                val lat = settingsPreferences.latitude.first()
                val lon = settingsPreferences.longitude.first()
                val unit = settingsPreferences.temperatureUnit.first()
                val lang = settingsPreferences.language.first()

                val apiUnits = when (unit) {
                    "Fahrenheit" -> "imperial"
                    "Kelvin" -> "standard"
                    else -> "metric"
                }

                fetchWeather(lat, lon, apiUnits, lang)
            } catch (e: Exception) {
                Log.e("HomeViewModel", "Refresh failed: ${e.message}")
            }
        }
    }

}