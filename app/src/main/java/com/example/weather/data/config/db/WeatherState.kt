package com.example.weather.data.config.db

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class WeatherState(
    val cityName: String = "Unknown Location",
    val temp: Double = 0.0,
    val humidity: Double = 0.0,
    val windSpeed: Double = 0.0,
    val sunrise: Double = 0.0,
    val sunset: Double = 0.0,
    val description: String = "",
    val icon: String = "",
    val lat: Double = 0.0,
    val lon: Double = 0.0,
) : Parcelable