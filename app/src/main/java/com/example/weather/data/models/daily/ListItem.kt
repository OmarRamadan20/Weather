package com.example.weather.data.models.daily

import com.google.gson.annotations.SerializedName

data class ListItem(

	@field:SerializedName("sunrise")
	val sunrise: Int? = null,

	@field:SerializedName("temp")
	val temp: Temp? = null,

	@field:SerializedName("deg")
	val deg: Int? = null,

	@field:SerializedName("pressure")
	val pressure: Int? = null,

	@field:SerializedName("clouds")
	val clouds: Int? = null,

	@field:SerializedName("feels_like")
	val feelsLike: FeelsLike? = null,

	@field:SerializedName("speed")
	val speed: Double? = null,

	@field:SerializedName("dt")
	val dt: Int? = null,

	@field:SerializedName("pop")
	val pop: Double? = null,

	@field:SerializedName("sunset")
	val sunset: Int? = null,

	@field:SerializedName("weather")
	val weather: List<WeatherItem?>? = null,

	@field:SerializedName("humidity")
	val humidity: Int? = null,

	@field:SerializedName("gust")
	val gust: Double? = null,

	@field:SerializedName("rain")
	val rain: Double? = null
)