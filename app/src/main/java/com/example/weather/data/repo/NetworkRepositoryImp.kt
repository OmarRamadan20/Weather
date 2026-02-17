package com.example.weather.data.repo

import com.example.weather.data.datasources.remote.network.MyResult
import com.example.weather.data.datasources.remote.network.NetworkDataSource
import com.example.weather.data.models.forecast.ForecastResponse
import com.example.weather.data.models.weather.WeatherResponse

class NetworkRepositoryImp(private val dataSource: NetworkDataSource): NetworkRepository {
    override suspend fun getCurrentWeather(
        lat: Double,
        lon: Double,
        apiKey: String,
        units: String,
        lang: String
    ): MyResult<WeatherResponse> {
        return dataSource.getCurrentWeather(lat, lon, apiKey, units, lang)
    }

    override suspend fun getForecast(
        cityName: String,
        apiKey: String,
        units: String
    ): MyResult<ForecastResponse> {
        return dataSource.getForecast(cityName, apiKey, units)
    }
}