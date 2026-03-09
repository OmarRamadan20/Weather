package com.example.weather.presentation.alerts.view.service.worker

import android.content.Context
import androidx.work.ListenableWorker
import androidx.work.WorkerFactory
import androidx.work.WorkerParameters
import com.example.weather.data.repo.WeatherRepository

class WeatherAlertWorkerFactory(
    private val repository: WeatherRepository
) : WorkerFactory() {

    override fun createWorker(
        appContext: Context,
        workerClassName: String,
        workerParameters: WorkerParameters
    ): ListenableWorker? {
        return if (workerClassName == WeatherAlertWorker::class.java.name) {
            WeatherAlertWorker(appContext, workerParameters, repository)
        } else null
    }
}