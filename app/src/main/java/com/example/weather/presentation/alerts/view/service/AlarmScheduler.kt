package com.example.weather.presentation.alerts.view.service

import com.example.weatherapp.data.models.Alerts
    interface AlarmScheduler {
        fun schedule(alert: Alerts)
        fun cancel(alert: Alerts)

}