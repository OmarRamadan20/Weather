package com.example.weather.presentation.home.view
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.example.weather.presentation.home.viewmodel.HomeViewModel
import com.example.weather.presentation.settings.viewmodel.SettingsViewModel
import com.example.weather.utils.MyResult
import com.example.weather.utils.NetworkObserver
import com.example.weather.utils.NoInternetView


@Composable
fun HomeScreenContent(viewModel: HomeViewModel, settingsViewModel: SettingsViewModel) {
    val weatherState by viewModel.weatherState.collectAsState()
    val forecastState by viewModel.hourlyState.collectAsState()
    val dailyState by viewModel.dailyState.collectAsState()

    val selectedLang by settingsViewModel.language.collectAsState()
    val tempUnit by settingsViewModel.tempUnit.collectAsState()
    val networkStatus by viewModel.networkStatus.collectAsState()

    val isOnline = networkStatus == NetworkObserver.Status.Available

    val apiUnits = when (tempUnit) {
        "Fahrenheit" -> "imperial"
        "Kelvin" -> "standard"
        else -> "metric"
    }


    Box(modifier = Modifier.fillMaxSize()) {
        when {
            weatherState is MyResult.Loading ||
                    forecastState is MyResult.Loading ||
                    dailyState is MyResult.Loading -> {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            }

            weatherState is MyResult.Success &&
                    forecastState is MyResult.Success &&
                    dailyState is MyResult.Success -> {
                WeatherScreen(
                    weatherData = (weatherState as MyResult.Success).data,
                    hourlyResponse = (forecastState as MyResult.Success).data,
                    dailyData = (dailyState as MyResult.Success).data,
                    selectedLang = selectedLang,
                    isOnline = networkStatus,
                    onRetry = {
                        viewModel.refresh()
                    }
                )
            }

            !isOnline && weatherState !is MyResult.Success -> {
                NoInternetView(onRetry = {
                    viewModel.refresh()
                })
            }

            weatherState is MyResult.Error -> {
                Text(
                    text = "Error: ${(weatherState as MyResult.Error).message}",
                    modifier = Modifier.align(Alignment.Center)
                )
            }
        }
    }
}