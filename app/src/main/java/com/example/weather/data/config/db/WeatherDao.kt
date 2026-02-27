package com.example.weather.data.config.db

import androidx.room.Dao
import androidx.room.Database
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.RoomDatabase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow

@Dao
interface WeatherDao {
    @Query("SELECT * FROM fav_locations")
    fun getAllFavourites(): Flow<List<FavLocation>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFav(location: FavLocation)

    @Delete
    suspend fun deleteFav(location: FavLocation)
}

