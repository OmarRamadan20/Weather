package com.example.weather

import com.example.weather.data.datasources.local.LocalDataSource
import com.example.weather.data.datasources.remote.network.MyResult
import com.example.weather.data.datasources.remote.network.NetworkDataSource
import com.example.weather.data.models.daily.DailyResponse
import com.example.weather.data.models.weather.WeatherResponse
import com.example.weather.data.repo.WeatherRepositoryImp
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test

class WeatherRepositoryTest {


    private lateinit var repository: WeatherRepositoryImp
    private val mockRemoteDataSource = mockk<NetworkDataSource>()
    private val mockLocalDataSource = mockk<LocalDataSource>()

    @Before
    fun setup() {
        repository = WeatherRepositoryImp(mockRemoteDataSource, mockLocalDataSource)
    }



    @Test
    fun `getCurrentWeather returns Success when remote data source succeeds`() = runTest {
        val lat = 30.0; val lon = 31.0
        val expectedWeather = WeatherResponse(name = "Cairo")
        coEvery {
            mockRemoteDataSource.getCurrentWeather(lat, lon, any(), any(), any())
        } returns MyResult.Success(expectedWeather)

        val result = repository.getCurrentWeather(lat, lon, "api_key", "metric", "en")

        assert(result is MyResult.Success)
        assertEquals(expectedWeather.name, (result as MyResult.Success).data.name)
    }


    @Test
    fun getHourlyForecast_error_returnsErrorMessage() = runTest {

        val errorMsg = "Network Timeout"
        coEvery {
            mockRemoteDataSource.getHourlyForecast(any(), any(), any(), any())
        } returns MyResult.Error(errorMsg)

        val result = repository.getHourlyForecast(30.0, 31.0, "key", "metric")

        assertTrue(result is MyResult.Error)
        assertEquals("Network Timeout", (result as MyResult.Error).message)
    }


    @Test
    fun `dailyForecast calls remote data source with correct parameters`() = runTest {

        val dailyResponse = DailyResponse()
        coEvery {
            mockRemoteDataSource.dailyForecast(any(), any(), any(), any(), any())
        } returns MyResult.Success(dailyResponse)

        repository.dailyForecast(30.0, 31.0, "key", "en", "metric")

        coVerify(exactly = 1) {
            mockRemoteDataSource.dailyForecast(30.0, 31.0, "key", "en", "metric")
        }
    }
}