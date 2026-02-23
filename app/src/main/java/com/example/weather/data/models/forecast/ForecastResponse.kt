package com.example.weather.data.models.forecast

import kotlinx.parcelize.Parcelize
import android.os.Parcelable
import com.example.weather.data.models.weather.Main
import com.example.weather.data.models.weather.Sys
import com.example.weather.data.models.weather.WeatherItem
import com.google.gson.annotations.SerializedName

@Parcelize
data class ForecastResponse(
    @SerializedName("current")
    val current: CurrentWeather? = null,

    @SerializedName("hourly")
    val list: List<HourlyItem>? = null,

    @SerializedName("daily")
    val daily: List<DailyItem>? = null,

    @SerializedName("timezone")
    val timezone: String? = null,

    @field:SerializedName("sys")
    val sys: Sys? = null,
) : Parcelable

@Parcelize
data class CurrentWeather(
    @SerializedName("dt") val dt: Long,
    @SerializedName("temp") val temp: Double,
    @SerializedName("humidity") val humidity: Int,
    @SerializedName("uvi") val uvi: Double,
    @SerializedName("weather") val weather: List<WeatherItem>,
    @SerializedName("sys") val sys: Sys? = null,
) : Parcelable

@Parcelize
data class HourlyItem(
    @SerializedName("dt") val dt: Int? = null,
    @SerializedName("temp") val temp: Double? = null,
    @SerializedName("weather") val weather: List<WeatherItem?>? = null,
    @SerializedName("pop") val pop: Double? = null,
    @SerializedName("main") val main: Main? = null,
) : Parcelable

@Parcelize
data class DailyItem(
    @SerializedName("dt") val dt: Long,
    @SerializedName("temp") val temp: DailyTemp,
    @SerializedName("weather") val weather: List<WeatherItem>,
    @SerializedName("sunrise") val sunrise: Long,
    @SerializedName("sunset") val sunset: Long
) : Parcelable

@Parcelize
data class DailyTemp(
    @SerializedName("min") val min: Double,
    @SerializedName("max") val max: Double,
    @SerializedName("day") val day: Double? = null
) : Parcelable