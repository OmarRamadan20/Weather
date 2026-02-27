package com.example.weather.data.datasources.local

import com.example.weather.data.config.db.FavLocation
import com.example.weather.data.config.db.WeatherDao

class LocalDataSourceImp(private val dao: WeatherDao) : LocalDataSource {
    override fun getStoredLocations() = dao.getAllFavourites()
    override suspend fun saveToFav(location: FavLocation) = dao.insertFav(location)
    override suspend fun deleteFav(location: FavLocation) = dao.deleteFav(location)

}