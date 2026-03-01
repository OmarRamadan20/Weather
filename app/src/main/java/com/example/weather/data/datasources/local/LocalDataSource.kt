package com.example.weather.data.datasources.local

import com.example.weather.data.config.db.FavLocation
import com.example.weatherapp.data.models.Alerts
import kotlinx.coroutines.flow.Flow

interface LocalDataSource {

     fun getStoredLocations(): Flow<List<FavLocation>>
    suspend fun saveToFav(location: FavLocation)
    suspend fun deleteFav(location: FavLocation)

    fun getAllALerts(): Flow<List<Alerts>>

    suspend fun addAlert(alert: Alerts):Long

    suspend fun deleteAlert(alert: Alerts)

    suspend fun getAlertById(id: Int): Alerts?

    suspend fun updateAlertStatus(alertId: Int, isEnabled: Boolean)




}
