package com.example.weather

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.weather.data.config.db.AppDatabase
import com.example.weatherapp.data.config.db.AlertsDao
import com.example.weatherapp.data.models.Alerts
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith


@RunWith(AndroidJUnit4::class)
class AlertsDaoTest {

    private lateinit var database: AppDatabase
    private lateinit var dao: AlertsDao

    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @Before
    fun setup() {
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            AppDatabase::class.java
        ).allowMainThreadQueries().build()
        dao = database.alertsDao()
    }

    @After
    fun tearDown() {
        database.close()
    }


    @Test
    fun insertAlert_and_getById_returnsCorrectData() = runTest {
        val alert = Alerts(
            id = 10,
            triggerType = "Rain",
            thresholdValue = 50,
            deliveryType = "Notification",
            startDate = 0L,
            endDate = 0L,
            isEnabled = true
        )

        dao.insertAlert(alert)
        val result = dao.getAlertById(10)

        assert(result != null)
        assert(result?.triggerType == "Rain")
        assert(result?.deliveryType == "Notification")
        assert(result?.startDate == 0L)
    }


    @Test
    fun deleteAlert_removesAlertFromDb() = runTest {
        val alert = Alerts(
            id = 5
            , triggerType = "Wind"
            , thresholdValue = 15
            , deliveryType = "Alarm"
            , startDate = 0L
            , endDate = 0L)

        dao.insertAlert(alert)

        dao.deleteAlert(alert)

        val result = dao.getAlertById(5)
        assert(result == null)
    }

    @Test
    fun updateAlertStatus_updatesIsEnabledField() = runTest {
        val alert = Alerts(
            id = 1
            , triggerType = "Temp"
            , thresholdValue = 30
            , deliveryType = "Notification"
            , startDate = 0L
            , endDate = 0L
            , isEnabled = true)
        dao.insertAlert(alert)

        dao.updateAlertStatus(1, false)

        val result = dao.getAlertById(1)
        assert(result?.isEnabled == false)
    }
}