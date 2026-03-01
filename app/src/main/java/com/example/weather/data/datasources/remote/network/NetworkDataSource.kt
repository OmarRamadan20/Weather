package com.example.weather.data.datasources.remote.network

import com.example.weather.data.models.daily.DailyResponse
import com.example.weather.data.models.hourly.HourlyResponse
import com.example.weather.data.models.map.CityResponse
import com.example.weather.data.models.map.CityResponseItem
import com.example.weather.data.models.weather.WeatherResponse
import com.example.weather.utils.MyResult

interface NetworkDataSource {

    suspend fun getCurrentWeather(
        lat: Double,
        lon: Double,
        apiKey: String,
        units: String,
        lang: String)
    : MyResult<WeatherResponse>

    suspend fun getHourlyForecast(
        lat: Double,
        lon: Double,
        apiKey: String,
        units: String
    ): MyResult<HourlyResponse>


    suspend fun dailyForecast(
        lat: Double,
        lon: Double,
        apiKey: String,
        lang: String,
        units: String
    ): MyResult<DailyResponse>


    suspend fun getCitySuggestions(
        query: String,
        limit: Int = 8,
        apiKey: String
    ): MyResult<List<CityResponseItem>>
}
