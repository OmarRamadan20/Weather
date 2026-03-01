package com.example.weather.presentation.home.viewmodel

import android.util.Log
import com.example.weather.utils.MyResult
import com.example.weather.data.repo.WeatherRepository
import com.example.weather.presentation.favourite.viewmodel.MainDispatcherRule
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import junit.framework.TestCase
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(JUnit4::class)
class HomeViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private lateinit var viewModel: HomeViewModel
    private val mockRepo = mockk<WeatherRepository>(relaxed = true)

    @Before
    fun setup() {

        mockkStatic(Log::class)
        viewModel = HomeViewModel(mockRepo)
    }

    @Test
    fun `fetchWeatherWithNewSettings updates unit and lang flows`() = runTest {
        // 1. Act:
        viewModel.fetchWeatherWithNewSettings(units = "imperial", lang = "ar")

        // 2. Assert:
        TestCase.assertEquals("imperial", viewModel.selectedUnit.value)
        TestCase.assertEquals("ar", viewModel.selectedLang.value)
    }


    @Test
    fun `fetchWeatherForLocation calls repository with correct coordinates`() = runTest {
        // 1. Act
        viewModel.fetchWeatherForLocation(20.0, 30.0)
        advanceUntilIdle()

        // 2. Assert:
        coVerify { mockRepo.getCurrentWeather(20.0, 30.0, any(), any(), any()) }
    }


    @Test
    fun `fetchWeather sets error state when repository fails`() = runTest {
        // 1. Arrange
        coEvery { mockRepo.getCurrentWeather(any(), any(), any(), any(), any()) } returns MyResult.Error("Network Fail")

        // 2. Act
        viewModel.fetchWeather(0.0, 0.0, "key", "metric", "en")
        advanceUntilIdle()

        // 3. Assert
        assertTrue(viewModel.weatherState.value is MyResult.Error)
        assertEquals("Network Fail", (viewModel.weatherState.value as MyResult.Error).message)
    }



}