package com.example.weather.presentation.alerts.service
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.media.MediaPlayer
import android.media.RingtoneManager
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import com.example.weather.MainActivity
import com.example.weather.R

@RequiresApi(Build.VERSION_CODES.O)

class AlarmService : Service() {
    private var mediaPlayer: MediaPlayer? = null
    private var vibrator: Vibrator? = null

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {

        if (intent?.action == "STOP_ALARM") {
            stopForeground(STOP_FOREGROUND_REMOVE)
            stopSelf()
            return START_NOT_STICKY
        }

        val channelId = "weather_alarm_channel"

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Weather Alarm",
                NotificationManager.IMPORTANCE_HIGH
            )
            val manager = getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(channel)
        }

        val notification = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle("Weather Alert")
            .setContentText(intent?.getStringExtra("ALERT_MESSAGE") ?: "achieved the threshold")
            .setPriority(NotificationCompat.PRIORITY_MAX)
            .setCategory(NotificationCompat.CATEGORY_ALARM)
            .setFullScreenIntent(getPendingIntent(), true)
            .addAction(R.drawable.ic_save, "Stop", getStopPendingIntent())
            .build()

        startForeground(1001, notification)
        playAlarm()

        return START_NOT_STICKY
    }

    private fun getPendingIntent(): PendingIntent {
        val intent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        return PendingIntent.getActivity(
            this,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
    }

    private fun playAlarm() {
        val alertUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM)
        mediaPlayer = MediaPlayer.create(this, alertUri)
        mediaPlayer?.isLooping = true
        mediaPlayer?.start()

        vibrator = getSystemService(Vibrator::class.java)
        vibrator?.vibrate(VibrationEffect.createWaveform(longArrayOf(0, 500, 500), 0))
    }

    private fun getStopPendingIntent(): PendingIntent {
        val intent = Intent(this, AlarmService::class.java).apply {
            action = "STOP_ALARM"
        }
        return PendingIntent.getService(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)
    }

    override fun onBind(intent: Intent?) = null

    override fun onDestroy() {
        mediaPlayer?.stop()
        vibrator?.cancel()
        super.onDestroy()
    }
}