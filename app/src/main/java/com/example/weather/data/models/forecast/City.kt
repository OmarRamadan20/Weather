package com.example.weather.data.models.forecast

import kotlinx.parcelize.Parcelize
import android.os.Parcelable
import com.example.weather.data.models.weather.Coord
import com.google.gson.annotations.SerializedName

@Parcelize
data class City(

    @field:SerializedName("country")
	val country: String? = null,

    @field:SerializedName("coord")
	val coord: Coord? = null,

    @field:SerializedName("sunrise")
	val sunrise: Int? = null,

    @field:SerializedName("timezone")
	val timezone: Int? = null,

    @field:SerializedName("sunset")
	val sunset: Int? = null,

    @field:SerializedName("name")
	val name: String? = null,

    @field:SerializedName("id")
	val id: Int? = null,

    @field:SerializedName("population")
	val population: Int? = null
) : Parcelable