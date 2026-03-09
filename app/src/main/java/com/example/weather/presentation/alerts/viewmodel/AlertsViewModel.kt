package com.example.weather.presentation.alerts.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.weather.data.repo.WeatherRepository
import com.example.weatherapp.data.models.Alerts
import com.example.weather.presentation.alerts.view.service.AlarmScheduler
import com.example.weather.presentation.settings.viewmodel.SettingsPreferences
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class AlertsViewModel(
    private val repository: WeatherRepository,
    private val settingsPreferences: SettingsPreferences,
    private val alarmScheduler: AlarmScheduler
) : ViewModel() {

    val tempUnit: StateFlow<String> = settingsPreferences.temperatureUnit
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), "metric")


    fun getUnitForTrigger(triggerType: String, currentTempUnit: String, currentWindUnit: String): String {
        return when (triggerType) {
            "Temp" -> when (currentTempUnit) {
                "imperial" -> "°F"
                "standard" -> "K"
                else -> "°C"
            }
            "Wind" -> when (currentWindUnit) {
                "imperial" -> "miles/h"
                else -> "m/s"
            }
            "Rain" -> "mm"
            else -> ""
        }
    }

    val allAlerts: StateFlow<List<Alerts>> = repository.getAllAlerts()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val windUnit: StateFlow<String> = settingsPreferences.windUnit
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = "metric"
        )

    fun addAlert(alert: Alerts) {
        viewModelScope.launch {
            val id = repository.addAlert(alert)
            val alertWithId = alert.copy(id = id.toInt())
            alarmScheduler.schedule(alertWithId)
        }
    }

    fun removeAlert(alert: Alerts) {
        viewModelScope.launch {
            repository.deleteAlert(alert)
            alarmScheduler.cancel(alert)
        }
    }

    fun updateAlertStatus(alertId: Int, isEnabled: Boolean) {
        viewModelScope.launch {
            repository.updateAlertStatus(alertId, isEnabled)

            allAlerts.value.find { it.id == alertId }?.let { alert ->
                val updatedAlert = alert.copy(isEnabled = isEnabled)
                if (isEnabled) {
                    alarmScheduler.schedule(updatedAlert)
                } else {
                    alarmScheduler.cancel(updatedAlert)
                }
            }
        }
    }
}