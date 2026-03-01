package com.example.weather.data.datasources.remote.network

import com.example.weather.data.config.network.WeatherApiService
import com.example.weather.data.models.daily.DailyResponse
import com.example.weather.data.models.hourly.HourlyResponse
import com.example.weather.data.models.map.CityResponse
import com.example.weather.data.models.map.CityResponseItem
import com.example.weather.data.models.weather.WeatherResponse
import com.example.weather.utils.MyResult

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


    override suspend fun getCitySuggestions(
        query: String,
        limit: Int,
        apiKey: String): MyResult<List<CityResponseItem>> {
        return try {
            val response = apiService.getCitySuggestions(query, limit, apiKey)
            MyResult.Success(response)
        } catch (e: Exception) {
            MyResult.Error(e.message ?: "Unknown error")
        }
    }
}