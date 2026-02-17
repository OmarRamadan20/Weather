package com.example.weather.data.repo

import com.example.weather.data.datasources.remote.network.MyResult
import com.example.weather.data.models.forecast.ForecastResponse
import com.example.weather.data.models.weather.WeatherResponse

interface NetworkRepository {
    suspend fun getCurrentWeather(
        lat: Double,
        lon: Double,
        apiKey: String,
        units: String = "metric",
        lang: String = "en"
    ): MyResult<WeatherResponse>

    suspend fun getForecast(
        cityName: String,
        apiKey: String,
        units: String = "metric"
    ): MyResult<ForecastResponse>
}