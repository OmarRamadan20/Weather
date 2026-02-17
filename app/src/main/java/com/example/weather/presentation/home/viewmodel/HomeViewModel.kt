package com.example.weather.presentation.home.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.weather.data.datasources.remote.network.MyResult
import com.example.weather.data.models.weather.WeatherResponse
import com.example.weather.data.repo.NetworkRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class HomeViewModel(private val repository: NetworkRepository) : ViewModel() {

    private val _weatherState = MutableStateFlow<MyResult<WeatherResponse>>(MyResult.Loading)
    val weatherState = _weatherState.asStateFlow()

    fun fetchWeather(lat: Double, lon: Double) {
        viewModelScope.launch {

            val result = repository.getCurrentWeather(
                lat = lat,
                lon = lon,
                apiKey = "a50b3547c713e7be1ec57c696006497f",
                units = "metric",
                lang = "en"
            )

           when (result) {
                is MyResult.Success -> {
                    _weatherState.value = MyResult.Success(result.data)
                }
                is MyResult.Error -> {
                    _weatherState.value = MyResult.Error(result.message ?: "Unknown Error")
                }
                else -> {
                    _weatherState.value = MyResult.Loading
                }
            }
        }
    }
}