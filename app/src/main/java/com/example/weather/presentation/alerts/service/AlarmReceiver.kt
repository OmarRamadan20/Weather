package com.example.weather.presentation.alerts.service

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.work.Constraints
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.workDataOf
import com.example.weather.presentation.alerts.service.worker.WeatherAlertWorker



class AlarmReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val alertId = intent.getIntExtra("ALERT_ID", -1)

        if (alertId != -1) {
            val workRequest = OneTimeWorkRequestBuilder<WeatherAlertWorker>()
                .setInputData(workDataOf("ALERT_ID" to alertId))
                .setConstraints(
                    Constraints.Builder()
                        .setRequiredNetworkType(NetworkType.CONNECTED)
                        .build()
                )
                .build()

            WorkManager.getInstance(context).enqueue(workRequest)
        }
    }
}