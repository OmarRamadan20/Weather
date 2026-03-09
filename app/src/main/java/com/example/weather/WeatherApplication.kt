package com.example.weather

import android.app.Application
import androidx.work.Configuration
import com.example.weather.data.config.db.AppDatabase
import com.example.weather.data.config.network.RetrofitClient
import com.example.weather.data.datasources.local.LocalDataSourceImp
import com.example.weather.data.datasources.remote.network.NetworkDataSourceImp
import com.example.weather.data.repo.WeatherRepository
import com.example.weather.data.repo.WeatherRepositoryImp
import com.example.weather.presentation.alerts.view.service.worker.WeatherAlertWorkerFactory

class WeatherApplication : Application(), Configuration.Provider {

    private val repository: WeatherRepository by lazy {
        val database = AppDatabase.getInstance(this)
        val localDataSource = LocalDataSourceImp(database.weatherDao(), database.alertsDao())

        val apiService = RetrofitClient.instance
        val networkDataSource = NetworkDataSourceImp(apiService)

        WeatherRepositoryImp.getInstance(networkDataSource, localDataSource)
    }

    override val workManagerConfiguration: Configuration
        get() = Configuration.Builder()
            .setWorkerFactory(WeatherAlertWorkerFactory(repository))
            .setMinimumLoggingLevel(android.util.Log.INFO)
            .build()
}