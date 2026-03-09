package com.example.weather.presentation.alerts.view.service

import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import com.example.weatherapp.data.models.Alerts
import kotlin.jvm.java

@SuppressLint("ScheduleExactAlarm")
class AlarmSchedulerImp(private val context: Context) : AlarmScheduler {

    private val alarmManager = context.getSystemService(AlarmManager::class.java)


    override fun schedule(alert: Alerts) {
        val intent = Intent(context, AlarmReceiver::class.java).apply {
            putExtra("ALERT_ID", alert.id)
        }

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            alert.id,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        alarmManager.setExactAndAllowWhileIdle(
            AlarmManager.RTC_WAKEUP,
            alert.startDate,
            pendingIntent
        )
    }

    override fun cancel(alert: Alerts) {
        alarmManager.cancel(
            PendingIntent.getBroadcast(
                context,
                alert.id,
                Intent(context, AlarmReceiver::class.java),
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
        )
    }
}