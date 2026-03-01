package com.example.weather

import WeatherRoute
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.lifecycle.lifecycleScope
import com.example.weather.data.config.db.AppDatabase
import com.example.weather.data.config.network.RetrofitClient
import com.example.weather.data.datasources.local.LocalDataSourceImp
import com.example.weather.data.datasources.remote.network.NetworkDataSourceImp
import com.example.weather.data.repo.WeatherRepositoryImp
import com.example.weather.presentation.alerts.service.AlarmSchedulerImp
import com.example.weather.presentation.alerts.viewmodel.AlertsViewModel
import com.example.weather.presentation.favourite.viewmodel.FavViewModel
import com.example.weather.presentation.home.viewmodel.HomeViewModel
import com.example.weather.presentation.settings.viewmodel.SettingsPreferences
import com.example.weather.presentation.settings.viewmodel.SettingsViewModel
import com.example.weather.utils.NetworkObserver
import kotlinx.coroutines.launch

@RequiresApi(Build.VERSION_CODES.TIRAMISU)
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val apiService = RetrofitClient.instance
        val database = AppDatabase.getInstance(this)
        val weatherDao = database.weatherDao()
        val alertsDao = database.alertsDao()


        val remoteDataSource = NetworkDataSourceImp(apiService)
        val localDataSource = LocalDataSourceImp(weatherDao,alertsDao)

        val repository = WeatherRepositoryImp(remoteDataSource,localDataSource)

        val alarmScheduler = AlarmSchedulerImp(this)

        val viewModel = HomeViewModel(repository)
        val settingsViewModel = SettingsViewModel(viewModel, repository, SettingsPreferences(this))
        val favViewModel = FavViewModel( repository)
        val alertsViewModel = AlertsViewModel(
            repository = repository,
            settingsPreferences = settingsViewModel.settingsPreferences,
            alarmScheduler = alarmScheduler,
        )


        val networkObserver = NetworkObserver(applicationContext)
        lifecycleScope.launch {
            networkObserver.observe.collect { status ->
                viewModel.updateNetworkStatus(status)
                favViewModel.updateNetworkStatus(status)
                settingsViewModel.updateNetworkStatus(status)}
        }


        setContent {
            WeatherRoute(
                viewModel = viewModel,
                settingsViewModel = settingsViewModel,
                favViewModel = favViewModel,
                alertsViewModel = alertsViewModel
            )
        }
    }
}

