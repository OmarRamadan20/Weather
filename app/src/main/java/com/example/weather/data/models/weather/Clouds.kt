package com.example.weather.data.models.weather

import kotlinx.parcelize.Parcelize
import android.os.Parcelable
import com.google.gson.annotations.SerializedName

@Parcelize
data class Clouds(

	@field:SerializedName("all")
	val all: Int? = null
) : Parcelable