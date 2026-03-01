package com.example.weather.presentation.home.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.weather.utils.MyResult
import com.example.weather.data.models.daily.DailyResponse
import com.example.weather.data.models.hourly.HourlyResponse
import com.example.weather.data.models.weather.WeatherResponse
import com.example.weather.data.repo.WeatherRepository
import com.example.weather.utils.NetworkObserver
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class HomeViewModel(private val repository: WeatherRepository) : ViewModel() {

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

    private val _networkStatus = MutableStateFlow(NetworkObserver.Status.Available)
    val networkStatus = _networkStatus.asStateFlow()


    fun updateNetworkStatus(status: NetworkObserver.Status) {
        _networkStatus.value = status

        if (status == NetworkObserver.Status.Available && weatherState.value is MyResult.Error) {
            fetchWeatherForLocation(lastLat, lastLon)
        }
    }
    fun fetchWeather(lat: Double, lon: Double, apiKey: String, units: String, lang: String) {

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
    private var lastLat = 62.2786
    private var lastLon = 12.3402


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