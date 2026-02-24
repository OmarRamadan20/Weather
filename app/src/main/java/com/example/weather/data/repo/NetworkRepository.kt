package com.example.weather.data.repo

import com.example.weather.data.datasources.remote.network.MyResult
import com.example.weather.data.models.daily.DailyResponse
import com.example.weather.data.models.hourly.HourlyResponse
import com.example.weather.data.models.weather.WeatherResponse

interface NetworkRepository {
    suspend fun getCurrentWeather(
        lat: Double,
        lon: Double,
        apiKey: String,
        units: String = "metric",
        lang: String = "en"
    ): MyResult<WeatherResponse>

    suspend fun getHourlyForecast(
        lat: Double,
        lon: Double,
        apiKey: String,
        units: String = "metric"
    ): MyResult<HourlyResponse>


    suspend fun dailyForecast(
        lat: Double,
        lon: Double,
        apiKey: String,
        lang: String
        ): MyResult<DailyResponse>
}