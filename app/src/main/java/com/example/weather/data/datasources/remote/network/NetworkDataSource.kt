package com.example.weather.data.datasources.remote.network

import com.example.weather.data.models.forecast.ForecastResponse
import com.example.weather.data.models.weather.WeatherResponse

interface NetworkDataSource {

    suspend fun getCurrentWeather(
        lat: Double,
        lon: Double,
        apiKey: String,
        units: String = "metric",
        lang: String = "en")
    : MyResult<WeatherResponse>

    suspend fun getForecast(
        cityName: String,
        apiKey: String,
        units: String = "metric"
    ): MyResult<ForecastResponse>
}
