package com.example.weather.data.repo

import com.example.weather.data.datasources.remote.network.MyResult
import com.example.weather.data.datasources.remote.network.NetworkDataSource
import com.example.weather.data.models.daily.DailyResponse
import com.example.weather.data.models.hourly.HourlyResponse
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

    override suspend fun getHourlyForecast(
        lat: Double,
        lon: Double,
        apiKey: String,
        units: String
    ): MyResult<HourlyResponse> {
        return dataSource.getHourlyForecast(lat=lat,lon=lon,apiKey=apiKey,units=units)
    }

    override suspend fun dailyForecast(
        lat: Double,
        lon: Double,
        apiKey: String
        ): MyResult<DailyResponse> {
        return dataSource.dailyForecast(lat = lat, lon = lon, apiKey = apiKey)
    }
}