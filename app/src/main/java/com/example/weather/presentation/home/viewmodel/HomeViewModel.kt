package com.example.weather.presentation.home.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.weather.data.datasources.remote.network.MyResult
import com.example.weather.data.models.daily.DailyResponse
import com.example.weather.data.models.hourly.HourlyResponse
import com.example.weather.data.models.weather.WeatherResponse
import com.example.weather.data.repo.NetworkRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class HomeViewModel(private val repository: NetworkRepository) : ViewModel() {

    companion object {
        private var savedUnit: String = "metric"
        private var savedLang: String = "en"
    }
    private val _weatherState = MutableStateFlow<MyResult<WeatherResponse>>(MyResult.Loading)
    val weatherState = _weatherState.asStateFlow()

    private val _hourlyState = MutableStateFlow<MyResult<HourlyResponse>>(MyResult.Loading)
    val hourlyState = _hourlyState.asStateFlow()

    private val _dailyState = MutableStateFlow<MyResult<DailyResponse>>(MyResult.Loading)
    val dailyState = _dailyState.asStateFlow()

    val selectedLang = MutableStateFlow("en")

    val selectedUnit = MutableStateFlow("metric")



    fun fetchWeather(lat: Double, lon: Double, apiKey: String, units: String, lang: String) {
        viewModelScope.launch {
            _weatherState.value = MyResult.Loading
            _hourlyState.value = MyResult.Loading
            _dailyState.value = MyResult.Loading

            try {
                val weatherResult = repository.getCurrentWeather(lat, lon, apiKey, units, lang)
                val hourlyResponse = repository.getHourlyForecast(lat, lon, apiKey, units)
                val dailyResponse = repository.dailyForecast(lat, lon, apiKey, lang, units)

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
    private var lastLat = 29.8319
    private var lastLon = 31.3601


    fun fetchWeatherWithNewSettings(
        units: String? = null,
        lang: String? = null
    ) {
        units?.let { savedUnit = it }
        lang?.let { savedLang = it }

        selectedUnit.value = savedUnit
        selectedLang.value = savedLang


        fetchWeather(
            lat = lastLat,
            lon = lastLon,
            apiKey = "a50b3547c713e7be1ec57c696006497f",
            units = savedUnit,
            lang = savedLang
        )
    }

    fun fetchWeatherForLocation(lat: Double, lon: Double) {
        lastLat = lat
        lastLon = lon

        fetchWeather(
            lat = lat,
            lon = lon,
            apiKey = "a50b3547c713e7be1ec57c696006497f",
            units = selectedUnit.value,
            lang = selectedLang.value
        )
    }


}