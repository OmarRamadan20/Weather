package com.example.weather.data.datasources.remote.network

import com.example.weather.data.config.network.WeatherApiService
import com.example.weather.data.models.daily.DailyResponse
import com.example.weather.data.models.hourly.HourlyResponse
import com.example.weather.data.models.weather.WeatherResponse

class NetworkDataSourceImp(private val apiService: WeatherApiService): NetworkDataSource {
    override suspend fun getCurrentWeather(
        lat: Double,
        lon: Double,
        apiKey: String,
        units: String,
        lang: String
    ): MyResult<WeatherResponse> {
        val result = apiService.getCurrentWeather(lat, lon, apiKey, units, lang)
        return MyResult.Success(result)
    }

    override suspend fun getHourlyForecast(
        lat: Double,
        lon: Double,
        apiKey: String,
        units: String
    ): MyResult<HourlyResponse> {
        val result = apiService.getHourlyForecast(lat, lon,apiKey,units)
        return MyResult.Success(result)
    }

    override suspend fun dailyForecast(
        lat: Double,
        lon: Double,
        apiKey: String,
        lang: String,
        units: String
    ): MyResult<DailyResponse> {
        val result = apiService.dailyForecast(
            lat, lon, apiKey, lang,
            units = units,

        )
        return MyResult.Success(result)
    }
}