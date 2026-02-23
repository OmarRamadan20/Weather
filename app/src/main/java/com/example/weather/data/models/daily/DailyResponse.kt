package com.example.weather.data.models.daily

import com.google.gson.annotations.SerializedName

data class DailyResponse(

	@field:SerializedName("city")
	val city: City? = null,

	@field:SerializedName("cnt")
	val cnt: Int? = null,

	@field:SerializedName("cod")
	val cod: String? = null,

	@field:SerializedName("message")
	val message: String? = null,

	@field:SerializedName("list")
	val list: List<ListItem?>? = null
)