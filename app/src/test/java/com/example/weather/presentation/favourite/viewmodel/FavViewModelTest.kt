package com.example.weather.presentation.favourite.viewmodel

import com.example.weather.data.config.db.FavLocation
import com.example.weather.utils.MyResult
import com.example.weather.data.models.weather.Main
import com.example.weather.data.models.weather.Sys
import com.example.weather.data.models.weather.WeatherResponse
import com.example.weather.data.models.weather.Wind
import com.example.weather.data.repo.WeatherRepository
import io.mockk.Runs
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.TestDispatcher
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TestWatcher
import org.junit.runner.Description

@OptIn(ExperimentalCoroutinesApi::class)
class FavViewModelTest {


    private lateinit var viewModel: FavViewModel
    private val mockRepo = mockk<WeatherRepository>()
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @Before
    fun setup() {
        every { mockRepo.getFavourites() } returns flowOf(emptyList())
        viewModel = FavViewModel(mockRepo)
    }

    @Test
    fun `fetchWeatherForMapPoint updates selectedWeather state on success`() = runTest {
        // Arrange
        val weatherData = WeatherResponse(
            name = "Cairo",
            main = Main(temp = 25.0, humidity = 50),
            sys = Sys(sunrise = 1000, sunset = 2000),
            wind = Wind(speed = 5.0)
        )
        coEvery {
            mockRepo.getCurrentWeather(any(), any(), any(), any(), any())
        } returns MyResult.Success(weatherData)

        // Act
        viewModel.fetchWeatherForMapPoint(30.0, 31.0, "key", "metric", "en")

        advanceUntilIdle()

        // Assert
        val state = viewModel.selectedWeather.value
        assertEquals("Cairo", state?.cityName)
    }


    @Test
    fun `deleteFromFav calls repository delete method with correct object`() = runTest {
        // 1. Arrange
        val location = FavLocation(id = 1, name = "Alex", lat = 31.0, lon = 29.0,
            temp = "20", humidity = 60.0, windSpeed = 5.0,
            sunrise = 0.0, sunset = 0.0)
        coEvery { mockRepo.deleteLocationFromFav(any()) } just Runs

        // 2. Act
        viewModel.deleteFromFav(location)
        advanceUntilIdle()

        // 3. Assert
        coVerify(exactly = 1) { mockRepo.deleteLocationFromFav(location) }
    }

    @Test
    fun `searchCities with less than 3 chars should return empty list without calling repository`() = runTest {
        // 1. Arrange
        val query = "Ca"

        // 2. Act
        viewModel.searchCities(query)

        // 3. Assert
        val result = viewModel.citySuggestions.value

        assertTrue(result is MyResult.Success && (result as MyResult.Success).data.isEmpty())

        coVerify(exactly = 0) { mockRepo.getCitySuggestions(any(), any(), any()) }
    }
}


@OptIn(ExperimentalCoroutinesApi::class)
class MainDispatcherRule(
    val testDispatcher: TestDispatcher = UnconfinedTestDispatcher()
) : TestWatcher() {
    override fun starting(description: Description) {
        Dispatchers.setMain(testDispatcher)
    }
    override fun finished(description: Description) {
        Dispatchers.resetMain()
    }
}

