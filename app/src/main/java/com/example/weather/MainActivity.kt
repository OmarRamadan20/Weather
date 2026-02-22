package com.example.weather

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.example.weather.data.config.network.RetrofitClient
import com.example.weather.data.datasources.remote.network.MyResult
import com.example.weather.data.datasources.remote.network.NetworkDataSourceImp
import com.example.weather.data.repo.NetworkRepositoryImp
import com.example.weather.presentation.home.view.WeatherScreen
import com.example.weather.presentation.home.viewmodel.HomeViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val apiService = RetrofitClient.instance
        val dataSource = NetworkDataSourceImp(apiService)
        val repository = NetworkRepositoryImp(dataSource)

        val viewModel = HomeViewModel(repository)

        setContent {
            WeatherRoute(viewModel = viewModel)
        }
    }
}

@Composable
fun WeatherRoute(viewModel: HomeViewModel) {
    val weatherState by viewModel.weatherState.collectAsState()
    val forecastState by viewModel.forecastState.collectAsState()


    LaunchedEffect(Unit) {
        viewModel.fetchWeather(lat = 52.5200, lon = 13.4050,city = "Berlin")
    }

    Box(modifier = Modifier.fillMaxSize()) {

        when {
            weatherState is MyResult.Loading || forecastState is MyResult.Loading -> {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            }

            weatherState is MyResult.Success && forecastState is MyResult.Success -> {
                val weatherData = (weatherState as MyResult.Success).data
                val forecastData = (forecastState as MyResult.Success).data

                WeatherScreen(
                    weatherData = weatherData,
                    forecastData = forecastData
                )
            }

            weatherState is MyResult.Error -> {
                Text(
                    text = "Weather Error: ${(weatherState as MyResult.Error).message}",
                    modifier = Modifier.align(Alignment.Center)
                )
            }

            forecastState is MyResult.Error -> {
                Text(
                    text = "Forecast Error: ${(forecastState as MyResult.Error).message}",
                    modifier = Modifier.align(Alignment.Center)
                )
            }
        }
    }
}