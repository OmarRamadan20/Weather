package com.example.weather.data.datasources.local

import com.example.weather.data.config.db.FavLocation
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow

interface LocalDataSource {

     fun getStoredLocations(): Flow<List<FavLocation>>
    suspend fun saveToFav(location: FavLocation)
    suspend fun deleteFav(location: FavLocation)
}
