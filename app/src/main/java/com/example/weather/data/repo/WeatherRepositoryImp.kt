package com.example.weather.data.repo

import com.example.weather.data.config.db.FavLocation
import com.example.weather.data.datasources.local.LocalDataSource
import com.example.weather.utils.MyResult
import com.example.weather.data.datasources.remote.network.NetworkDataSource
import com.example.weather.data.models.daily.DailyResponse
import com.example.weather.data.models.hourly.HourlyResponse
import com.example.weather.data.models.map.CityResponseItem
import com.example.weather.data.models.weather.WeatherResponse
import com.example.weatherapp.data.models.Alerts
import kotlinx.coroutines.flow.Flow

class WeatherRepositoryImp(private val remoteDataSource: NetworkDataSource,
                           private val localDataSource: LocalDataSource): WeatherRepository {
    override suspend fun getCurrentWeather(
        lat: Double,
        lon: Double,
        apiKey: String,
        units: String,
        lang: String
    ): MyResult<WeatherResponse> {
        return remoteDataSource.getCurrentWeather(lat, lon, apiKey, units, lang)
    }

    override suspend fun getHourlyForecast(
        lat: Double,
        lon: Double,
        apiKey: String,
        units: String
    ): MyResult<HourlyResponse> {
        return remoteDataSource.getHourlyForecast(lat=lat,lon=lon,apiKey=apiKey,units=units)
    }

    override suspend fun dailyForecast(
        lat: Double,
        lon: Double,
        apiKey: String,
        lang: String,
        units: String
        ): MyResult<DailyResponse> {
        return remoteDataSource.dailyForecast(lat = lat, lon = lon, apiKey = apiKey, lang = lang, units = units)
    }


    override suspend fun getCitySuggestions(
        query: String,
        limit: Int,
        apiKey: String): MyResult<List<CityResponseItem>> {
        return remoteDataSource.getCitySuggestions(query = query, limit = limit, apiKey = apiKey)
    }

    override fun getFavourites(): Flow<List<FavLocation>> {
        return localDataSource.getStoredLocations()
    }

    override suspend fun addLocationToFav(location: FavLocation) {
        return localDataSource.saveToFav(location)
    }

    override suspend fun deleteLocationFromFav(location: FavLocation) {
        return localDataSource.deleteFav(location)
    }

    override fun getAllAlerts(): Flow<List<Alerts>> {
        return localDataSource.getAllALerts()
    }

    override suspend fun addAlert(alert: Alerts): Long {
        return localDataSource.addAlert(alert)
    }

    override suspend fun deleteAlert(alert: Alerts) {
        return localDataSource.deleteAlert(alert)
    }

    override suspend fun getAlertById(id: Int): Alerts? {
        return localDataSource.getAlertById(id)
    }

    override suspend fun updateAlertStatus(alertId: Int, isEnabled: Boolean) {
        return localDataSource.updateAlertStatus(alertId, isEnabled)
    }



    companion object {
        @Volatile
        private var INSTANCE: WeatherRepositoryImp? = null

        fun getInstance(
            remoteDataSource: NetworkDataSource,
            localDataSource: LocalDataSource
        ): WeatherRepositoryImp {
            return INSTANCE ?: synchronized(this) {
                val instance = WeatherRepositoryImp(remoteDataSource, localDataSource)
                INSTANCE = instance
                instance
            }
        }
    }
}