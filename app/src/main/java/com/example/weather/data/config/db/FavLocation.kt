package com.example.weather.data.config.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "fav_locations")
data class FavLocation(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val lat: Double,
    val lon: Double,
    val temp: String,
    val humidity : Double,
    val windSpeed : Double,
    val sunrise : Double,
    val sunset : Double
)