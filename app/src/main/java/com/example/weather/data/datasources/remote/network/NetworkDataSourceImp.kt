package com.example.weather.data.datasources.remote.network

import com.example.weather.data.config.network.WeatherApiService
import com.example.weather.data.models.forecast.ForecastResponse
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

    override suspend fun getForecast(
        cityName: String,
        apiKey: String,
        units: String
    ): MyResult<ForecastResponse> {
        val result = apiService.getForecast(cityName,apiKey,units)
        return MyResult.Success(result)
    }
}