package com.example.weather

import android.app.Application
import android.util.Log
import androidx.work.Configuration
import com.example.weather.data.config.db.AppDatabase
import com.example.weather.data.config.network.RetrofitClient
import com.example.weather.data.datasources.local.LocalDataSource
import com.example.weather.data.datasources.local.LocalDataSourceImp
import com.example.weather.data.datasources.remote.network.NetworkDataSource
import com.example.weather.data.datasources.remote.network.NetworkDataSourceImp
import com.example.weather.data.repo.WeatherRepository
import com.example.weather.data.repo.WeatherRepositoryImp
import com.example.weather.presentation.alerts.service.worker.WeatherAlertWorkerFactory

class WeatherApplication : Application(), Configuration.Provider {

    private val repository: WeatherRepository by lazy {
        // 1. إعداد الـ Local Data Source (يحتاج Database)
        val database = AppDatabase.getInstance(this) // تأكد من اسم كلاس الـ DB عندك
        val localDataSource = LocalDataSourceImp(database.weatherDao(), database.alertsDao())

        // 2. إعداد الـ Network Data Source (يحتاج ApiService)
        val apiService = RetrofitClient.instance // تأكد من اسم الكلاس المسئول عن Retrofit
        val networkDataSource = NetworkDataSourceImp(apiService)

        // 3. إرجاع نسخة الـ Repository الوحيدة
        WeatherRepositoryImp.getInstance(networkDataSource, localDataSource)
    }

    override val workManagerConfiguration: Configuration
        get() = Configuration.Builder()
            // تمرير الـ Factory الذي قمنا بإنشائه سابقاً
            .setWorkerFactory(WeatherAlertWorkerFactory(repository))
            .setMinimumLoggingLevel(android.util.Log.INFO)
            .build()
}