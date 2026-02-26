package com.example.weather

import android.content.res.Configuration
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import com.example.weather.data.config.network.RetrofitClient
import com.example.weather.data.datasources.remote.network.MyResult
import com.example.weather.data.datasources.remote.network.NetworkDataSourceImp
import com.example.weather.data.repo.NetworkRepositoryImp
import com.example.weather.presentation.home.view.WeatherScreen
import com.example.weather.presentation.home.viewmodel.HomeViewModel
import com.example.weather.presentation.settings.view.SettingsScreen
import com.example.weather.presentation.settings.viewmodel.SettingsViewModel
import java.util.Locale

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val apiService = RetrofitClient.instance
        val dataSource = NetworkDataSourceImp(apiService)
        val repository = NetworkRepositoryImp(dataSource)

        val viewModel = HomeViewModel(repository)
        val settingsViewModel = SettingsViewModel(viewModel, repository)


        setContent {
            WeatherRoute(viewModel = viewModel, settingsViewModel = settingsViewModel)
        }
    }
}

@Composable
fun WeatherRoute(viewModel: HomeViewModel, settingsViewModel: SettingsViewModel) {
    val weatherState by viewModel.weatherState.collectAsState()
    val forecastState by viewModel.hourlyState.collectAsState()
    val dailyState by viewModel.dailyState.collectAsState()
    val tempUnit by settingsViewModel.tempUnit.collectAsState()
    val selectedLang by settingsViewModel.language.collectAsState()


    var currentScreen by remember { mutableStateOf("home") }
    val apiUnits = when(tempUnit) {
        "Fahrenheit" -> "imperial"
        "Kelvin" -> "standard"
        else -> "metric"
    }


    LaunchedEffect(apiUnits, selectedLang) {
        viewModel.fetchWeather(
            lat = 29.8319,
            lon = 31.3601,
            apiKey = "a50b3547c713e7be1ec57c696006497f",
            units = apiUnits,
            lang = selectedLang
        )
    }

    val context = LocalContext.current
    val currentLocale = if (selectedLang.contains("ar", ignoreCase = true))
        Locale("ar") else Locale("en")

    val configuration = Configuration(context.resources.configuration)
    configuration.setLocale(currentLocale)

    val localizedContext = context.createConfigurationContext(configuration)

    CompositionLocalProvider(LocalContext provides localizedContext,
        LocalLayoutDirection provides if (currentLocale.language == "ar") LayoutDirection.Rtl else LayoutDirection.Ltr) {


        Box(modifier = Modifier.fillMaxSize()) {

            when {
                weatherState is MyResult.Loading || forecastState is MyResult.Loading || dailyState is MyResult.Loading -> {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }

                weatherState is MyResult.Success && forecastState is MyResult.Success && dailyState is MyResult.Success -> {
                    val weatherData = (weatherState as MyResult.Success).data
                    val hourlyData = (forecastState as MyResult.Success).data
                    val dailyData = (dailyState as MyResult.Success).data

                    when (currentScreen) {
                        "home" -> WeatherScreen(
                            weatherData = weatherData,
                            hourlyResponse = hourlyData,
                            dailyData = dailyData,
                            selectedLang = selectedLang
                        )

                        "settings" -> SettingsScreen(settingsViewModel)
                        "fav" -> Text(
                            "Favorites Screen",
                            modifier = Modifier.align(Alignment.Center)
                        )

                        "alerts" -> Text(
                            "Alerts Screen",
                            modifier = Modifier.align(Alignment.Center)
                        )
                    }
                }

                weatherState is MyResult.Error -> Text(
                    "Error: ${(weatherState as MyResult.Error).message}",
                    modifier = Modifier.align(Alignment.Center)
                )
            }


            if (weatherState is MyResult.Success && forecastState is MyResult.Success) {
                Box(modifier = Modifier.align(Alignment.BottomCenter)) {
                    WeatherBottomBar(
                        currentScreen = currentScreen,
                        onNavigate = { newScreen -> currentScreen = newScreen }
                    )
                }


            }
        }
    }
}
    @Composable
    fun WeatherBottomBar(currentScreen: String, onNavigate: (String) -> Unit) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 25.dp, vertical = 20.dp)
        ) {
            Surface(
                modifier = Modifier
                    .height(75.dp)
                    .fillMaxWidth(),
                shape = RoundedCornerShape(38.dp),
                color = Color.White.copy(alpha = 0.9f),
                border = BorderStroke(1.dp, Color.White.copy(alpha = 0.5f)),
                shadowElevation = 25.dp
            ) {
                Row(
                    modifier = Modifier.fillMaxSize(),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Home
                    NavIcon(icon = R.drawable.ic_home, isSelected = currentScreen == "home") { onNavigate("home") }
                    // Favorites
                    NavIcon(icon = R.drawable.ic_heart, isSelected = currentScreen == "fav") { onNavigate("fav") }
                    // Alerts
                    NavIcon(icon = R.drawable.ic_notification, isSelected = currentScreen == "alerts") { onNavigate("alerts") }
                    // Settings
                    NavIcon(icon = R.drawable.ic_settings, isSelected = currentScreen == "settings") { onNavigate("settings") }
                }
            }
        }
    }

    @Composable
    fun NavIcon(icon: Int, isSelected: Boolean, onClick: () -> Unit) {
        IconButton(onClick = onClick) {
            Icon(
                painter = painterResource(id = icon),
                contentDescription = null,
                tint = if (isSelected) Color(0xFF3F51B5) else Color.Gray.copy(alpha = 0.5f),
                modifier = Modifier.size(if (isSelected) 28.dp else 24.dp)
            )
        }
    }
