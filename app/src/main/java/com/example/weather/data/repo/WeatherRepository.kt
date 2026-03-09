package com.example.weather.data.repo

import com.example.weather.data.config.db.FavLocation
import com.example.weather.utils.MyResult
import com.example.weather.data.models.daily.DailyResponse
import com.example.weather.data.models.hourly.HourlyResponse
import com.example.weather.data.models.map.CityResponseItem
import com.example.weather.data.models.weather.WeatherResponse
import com.example.weatherapp.data.models.Alerts
import kotlinx.coroutines.flow.Flow

interface WeatherRepository {
    suspend fun getCurrentWeather(
        lat: Double,
        lon: Double,
        units: String="metric",
        lang: String="en"
    ): MyResult<WeatherResponse>

    suspend fun getHourlyForecast(
        lat: Double,
        lon: Double,
        units: String
    ): MyResult<HourlyResponse>


    suspend fun dailyForecast(
        lat: Double,
        lon: Double,
        lang: String,
        units: String
        ): MyResult<DailyResponse>


    suspend fun getCitySuggestions(
        query: String,
        limit: Int = 8
    ): MyResult<List<CityResponseItem>>


    fun getFavourites(): Flow<List<FavLocation>>

    suspend fun addLocationToFav(location: FavLocation)

    suspend fun deleteLocationFromFav(location: FavLocation)

    fun getAllAlerts(): Flow<List<Alerts>>

    suspend fun addAlert(alert: Alerts):Long

    suspend fun deleteAlert(alert: Alerts)

    suspend fun getAlertById(id: Int): Alerts?

    suspend fun updateAlertStatus(alertId: Int, isEnabled: Boolean)




}