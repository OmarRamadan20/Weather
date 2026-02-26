package com.example.weather.data.config.network

import com.example.weather.data.models.daily.DailyResponse
import com.example.weather.data.models.hourly.HourlyResponse
import com.example.weather.data.models.forecast.ForecastResponse
import com.example.weather.data.models.map.CityResponse
import com.example.weather.data.models.map.CityResponseItem
import com.example.weather.data.models.weather.WeatherResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface WeatherApiService {

    @GET("data/2.5/weather")
    suspend fun getCurrentWeather(
        @Query("lat") lat: Double,
        @Query("lon") lon: Double,
        @Query("appid") apiKey: String,
        @Query("units") units: String ,
        @Query("lang") lang: String
    ): WeatherResponse


    @GET("data/2.5/forecast/daily")
    suspend fun dailyForecast(
        @Query("lat") lat: Double,
        @Query("lon") lon: Double,
        @Query("appid") apiKey: String,
        @Query("lang") lang: String,
        @Query("units") units: String,
        @Query("cnt") count: Int = 7
    ): DailyResponse

    @GET("data/2.5/forecast/hourly")
    suspend fun getHourlyForecast(
        @Query("lat") lat: Double,
        @Query("lon") lon: Double,
        @Query("appid") apiKey: String,
        @Query("units") units: String,
        @Query("cnt") count: Int = 24
    ): HourlyResponse


    @GET("geo/1.0/direct")
    suspend fun getCitySuggestions(
        @Query("q") query: String,
        @Query("limit") limit: Int = 8,
        @Query("appid") apiKey: String = "a50b3547c713e7be1ec57c696006497f"
    ): List<CityResponseItem>
}