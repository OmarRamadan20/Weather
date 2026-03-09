package com.example.weather.presentation.alerts.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.weather.data.repo.WeatherRepository
import com.example.weather.presentation.alerts.view.service.AlarmSchedulerImp
import com.example.weather.presentation.settings.viewmodel.SettingsPreferences

class AlertsViewModelFactory(
    private val repository: WeatherRepository,
    private val alarmScheduler: AlarmSchedulerImp,
    private val settings: SettingsPreferences
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return AlertsViewModel(repository, settings, alarmScheduler,) as T
    }
}