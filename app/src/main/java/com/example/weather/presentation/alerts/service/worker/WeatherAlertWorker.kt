package com.example.weather.presentation.alerts.service.worker

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.weather.R
import com.example.weather.data.datasources.remote.network.MyResult
import com.example.weather.data.repo.WeatherRepository
import com.example.weatherapp.data.models.Alerts
import com.example.weather.presentation.alerts.service.AlarmService

class WeatherAlertWorker(
    context: Context,
    workerParams: WorkerParameters,
    private val repository: WeatherRepository
) : CoroutineWorker(context, workerParams) {

    override suspend fun doWork(): Result {
        val alertId = inputData.getInt("ALERT_ID", -1)
        Log.d("WeatherAlertWorker", "Worker started, alertId: $alertId")

        if (alertId == -1) return Result.failure()

        return try {
            val alert = repository.getAlertById(alertId) ?: return Result.success()
            Log.d("WeatherAlertWorker", "Alert found: $alert")

            if (!alert.isEnabled) return Result.success()

            val currentTime = System.currentTimeMillis()
            if (currentTime > alert.endDate) {
                Log.d("WeatherAlertWorker", "Alert period expired.")
                return Result.success()
            }

            if (currentTime < alert.startDate) {
                Log.d("WeatherAlertWorker", "Alert period hasn't started yet.")
                return Result.success()
            }

            val prefs =
                applicationContext.getSharedPreferences("WeatherPrefs", Context.MODE_PRIVATE)
            val lat = prefs.getFloat("lat", 0f).toDouble()
            val lon = prefs.getFloat("lon", 0f).toDouble()

            val weatherResponse = repository.getCurrentWeather(lat, lon)
            var isTriggered = false

            when(weatherResponse){
                is MyResult.Success -> {

                     isTriggered = when (alert.triggerType) {

                        "Temp" -> weatherResponse.data.main?.temp!! >= alert.thresholdValue
                         "Wind" -> weatherResponse.data.wind?.speed!! >= alert.thresholdValue
                        "Rain" -> weatherResponse.data.weather!!.any { it?.main?.contains("Rain", true)
                            ?: false }
                        "Storm" -> weatherResponse.data.weather!!.any {
                            it?.main?.contains("Thunderstorm", true)?: false || it?.main?.contains("Storm", true)?:false
                        }

                        else -> false
                    }


                }
                else -> {
                }
            }

            if (isTriggered) {
                when(weatherResponse){
                    is MyResult.Success -> {
                        if (alert.deliveryType == "Notification") {
                            showNotification(alert, weatherResponse.data.weather?.get(0)?.description?:"Unknown")
                        } else {
                            val details =
                                "🌡 Trigger: ${alert.triggerType}\n📊 Threshold: ${alert.thresholdValue}\n🌤 " +
                                        "Current: ${weatherResponse.data.weather?.get(0)?.description}"
                            startAlarmService(alert, details)
                        }
                    }
                    else -> {
                    }

                }

            }

            Result.success()
        } catch (e: Exception) {
            Log.e("WeatherAlertWorker", "Error: ${e.message}")
            if (runAttemptCount < 3) Result.retry() else Result.failure()
        }
    }

    private fun showNotification(alert: Alerts, weatherDescription: String) {
        val channelId = "weather_alerts_channel"
        val notificationManager =
            applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Weather Alerts",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Weather condition alerts"
            }
            notificationManager.createNotificationChannel(channel)
        }

        val triggerName = when (alert.triggerType) {
            "Rain" -> applicationContext.getString(R.string.rain)
            "Wind" -> applicationContext.getString(R.string.wind)
            "Temp" -> applicationContext.getString(R.string.temp)
            "Storm" -> applicationContext.getString(R.string.storm)
            else -> alert.triggerType
        }

        val notification = NotificationCompat.Builder(applicationContext, channelId)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle("⚠️ $triggerName")
            .setContentText("${applicationContext.getString(R.string.threshold_level)}: ${alert.thresholdValue} | $weatherDescription")
            .setStyle(
                NotificationCompat.BigTextStyle()
                    .bigText("🌡 $triggerName\n📊 ${applicationContext.getString(R.string.threshold_level)}: ${alert.thresholdValue}\n🌤 $weatherDescription")
            )
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .build()

        notificationManager.notify(alert.id, notification)
    }

    private fun startAlarmService(alert: Alerts, detailedMessage: String) {
        val intent = Intent(applicationContext, AlarmService::class.java).apply {
            putExtra("ALERT_MESSAGE", detailedMessage)
            putExtra("TRIGGER_TYPE", alert.triggerType)
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            applicationContext.startForegroundService(intent)
        } else {
            applicationContext.startService(intent)
        }
    }
}