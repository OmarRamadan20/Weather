package com.example.weather.data.datasources.local

import com.example.weather.data.config.db.FavLocation
import com.example.weather.data.config.db.WeatherDao
import com.example.weatherapp.data.config.db.AlertsDao
import com.example.weatherapp.data.models.Alerts
import kotlinx.coroutines.flow.Flow

class LocalDataSourceImp(private val weatherDao: WeatherDao, private val alertsDao: AlertsDao) : LocalDataSource {
    override fun getStoredLocations() = weatherDao.getAllFavourites()
    override suspend fun saveToFav(location: FavLocation) = weatherDao.insertFav(location)
    override suspend fun deleteFav(location: FavLocation) = weatherDao.deleteFav(location)
    override fun getAllALerts(): Flow<List<Alerts>> {
        return alertsDao.getAllAlerts()
    }

    override suspend fun addAlert(alert: Alerts): Long {
        return alertsDao.insertAlert(alert)
    }

    override suspend fun deleteAlert(alert: Alerts) {
        alertsDao.deleteAlert(alert)
    }

    override suspend fun getAlertById(id: Int): Alerts? {
        return alertsDao.getAlertById(id)
    }

    override suspend fun updateAlertStatus(alertId: Int, isEnabled: Boolean) {
        return alertsDao.updateAlertStatus(alertId, isEnabled)

    }
}