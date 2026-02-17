package com.example.weather.data.models.forecast

import kotlinx.parcelize.Parcelize
import android.os.Parcelable
import com.example.weather.data.models.weather.Clouds
import com.example.weather.data.models.weather.Main
import com.example.weather.data.models.weather.WeatherItem
import com.google.gson.annotations.SerializedName

@Parcelize
data class ListItem(

    @field:SerializedName("dt")
	val dt: Int? = null,

    @field:SerializedName("pop")
	val pop: Int? = null,

    @field:SerializedName("visibility")
	val visibility: Int? = null,

    @field:SerializedName("dt_txt")
	val dtTxt: String? = null,

    @field:SerializedName("weather")
	val weather: List<WeatherItem?>? = null,

    @field:SerializedName("main")
	val main: Main? = null,

    @field:SerializedName("clouds")
	val clouds: Clouds? = null,

    @field:SerializedName("sys")
	val sys: Sys? = null,

    @field:SerializedName("wind")
	val wind: Wind? = null
) : Parcelable