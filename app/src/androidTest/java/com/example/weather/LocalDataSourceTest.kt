package com.example.weather
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.weather.data.config.db.AppDatabase
import com.example.weather.data.config.db.FavLocation
import com.example.weather.data.datasources.local.LocalDataSourceImp
import com.example.weatherapp.data.config.db.AlertsDao
import com.example.weatherapp.data.models.Alerts
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertNotNull
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class LocalDataSourceTest {

    private lateinit var database: AppDatabase
    private lateinit var localDataSource: LocalDataSourceImp

    @Before
    fun setup() {
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            AppDatabase::class.java
        ).allowMainThreadQueries().build()

        localDataSource = LocalDataSourceImp(database.weatherDao(),database.alertsDao())
    }

    @After
    fun tearDown() {
        database.close()
    }


    @Test
    fun deleteLocation_checkIfListIsEmpty() = runTest {
        //1. Arrange
        val location = FavLocation(
            id = 5,
            name = "Alex",
            lat = 31.0,
            lon = 29.0,
            temp = "20",
            humidity = 0.0,
            windSpeed = 0.0,
            sunrise = 0.0,
            sunset = 0.0
        )
        localDataSource.saveToFav(location)

        // 2. Act
        localDataSource.deleteFav(location)
        val result = localDataSource.getStoredLocations().first()

        // 3. Assert
        assertTrue(result.isEmpty())
    }


    @Test
    fun getAlertById_returnsCorrectData() = runTest {
        //1. Arrange
        val alert = Alerts(id = 10, triggerType = "Rain", thresholdValue = 20, deliveryType = "Push", startDate = 0L, endDate = 0L)
        localDataSource.addAlert(alert)

        // 2. Act
        val result = localDataSource.getAlertById(10)

        // 3. Assert
        assertNotNull(result)
        assertEquals("Rain", result?.triggerType)
    }


    @Test
    fun insertTwoLocations_checkIfCountIsTwo() = runTest {
        //1. Arrange
        val loc1 = FavLocation(id = 1, name = "Cairo", lat = 0.0, lon = 0.0, temp = "20", humidity = 0.0, windSpeed = 0.0, sunrise = 0.0, sunset = 0.0)
        val loc2 = FavLocation(id = 2, name = "Alex", lat = 0.0, lon = 0.0, temp = "15", humidity = 0.0, windSpeed = 0.0, sunrise = 0.0, sunset = 0.0)

        // 2. Act
        localDataSource.saveToFav(loc1)
        localDataSource.saveToFav(loc2)

        val result = localDataSource.getStoredLocations().first()

        // 3. Assert
        assertEquals(2, result.size)
    }
}