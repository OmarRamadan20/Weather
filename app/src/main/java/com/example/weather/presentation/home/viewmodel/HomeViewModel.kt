package com.example.weather.presentation.home.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.weather.data.datasources.remote.network.MyResult
import com.example.weather.data.models.forecast.ForecastResponse
import com.example.weather.data.models.weather.WeatherResponse
import com.example.weather.data.repo.NetworkRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class HomeViewModel(private val repository: NetworkRepository) : ViewModel() {

    private val _weatherState = MutableStateFlow<MyResult<WeatherResponse>>(MyResult.Loading)
    val weatherState = _weatherState.asStateFlow()
    private val _forecastState = MutableStateFlow<MyResult<ForecastResponse>>(MyResult.Loading)

    val forecastState = _forecastState.asStateFlow()


    fun fetchWeather(lat: Double, lon: Double,city:String) {
        viewModelScope.launch {

            val result = repository.getCurrentWeather(
                lat = lat,
                lon = lon,
                apiKey = "a50b3547c713e7be1ec57c696006497f",
                units = "metric",
                lang = "en"
            )
            val forecastResult = repository.getForecast(
                cityName = city,
                apiKey = "a50b3547c713e7be1ec57c696006497f"
                ,units = "metric"
            )

            when(forecastResult){
                is MyResult.Success -> {
                    _forecastState.value = MyResult.Success(forecastResult.data)
                }
                is MyResult.Error -> {
                    _forecastState.value = MyResult.Error(forecastResult.message)
                }
                is MyResult.Loading -> {
                    _forecastState.value = MyResult.Loading
                }


            }

           when (result) {
                is MyResult.Success -> {
                    _weatherState.value = MyResult.Success(result.data)

                }
                is MyResult.Error -> {
                    _weatherState.value = MyResult.Error(result.message)
                }
                else -> {
                    _weatherState.value = MyResult.Loading
                }
            }
        }
    }
}