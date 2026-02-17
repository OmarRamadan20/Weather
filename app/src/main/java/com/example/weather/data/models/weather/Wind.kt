package com.example.weather.data.models.weather

import kotlinx.parcelize.Parcelize
import android.os.Parcelable
import com.google.gson.annotations.SerializedName

@Parcelize
data class Wind(

	@field:SerializedName("deg")
	val deg: Double? = null,

	@field:SerializedName("speed")
	val speed: Double? = null,

	@field:SerializedName("gust")
	val gust: Double? = null
) : Parcelable