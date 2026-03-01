import android.os.Build
import androidx.annotation.RequiresApi
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
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import com.example.weather.R
import com.example.weather.utils.MyResult
import com.example.weather.presentation.alerts.view.AlertsScreen
import com.example.weather.presentation.alerts.viewmodel.AlertsViewModel
import com.example.weather.presentation.favourite.view.FavouriteWeatherScreen
import com.example.weather.presentation.favourite.viewmodel.FavViewModel
import com.example.weather.presentation.home.view.WeatherScreen
import com.example.weather.presentation.home.viewmodel.HomeViewModel
import com.example.weather.presentation.settings.view.SettingsScreen
import com.example.weather.presentation.settings.viewmodel.SettingsViewModel
import com.example.weather.presentation.splash.SplashScreen
import com.example.weather.utils.NetworkObserver
import com.example.weather.utils.NoInternetView
import java.util.Locale

@RequiresApi(Build.VERSION_CODES.TIRAMISU)
@Composable
fun WeatherRoute(viewModel: HomeViewModel, settingsViewModel: SettingsViewModel, favViewModel: FavViewModel,
                 alertsViewModel: AlertsViewModel) {
    val weatherState by viewModel.weatherState.collectAsState()
    val forecastState by viewModel.hourlyState.collectAsState()
    val dailyState by viewModel.dailyState.collectAsState()
    val tempUnit by settingsViewModel.tempUnit.collectAsState()
    val selectedLang by settingsViewModel.language.collectAsState()

    val networkStatus by viewModel.networkStatus.collectAsState()

    val isOnline = networkStatus == NetworkObserver.Status.Available
    val isOffline = networkStatus == NetworkObserver.Status.Lost
    var showSplash by remember { mutableStateOf(true) }






    var lastLat = 62.2786
    var lastLon = 12.3402
    var currentScreen by remember { mutableStateOf("home") }
    val apiUnits = when(tempUnit) {
        "Fahrenheit" -> "imperial"
        "Kelvin" -> "standard"
        else -> "metric"
    }


    LaunchedEffect(Unit) {
        kotlinx.coroutines.delay(2500)
        showSplash = false
    }
    LaunchedEffect(apiUnits, selectedLang, isOnline) {
        if (isOnline) {
            viewModel.fetchWeather(
                lat = 62.2786,
                lon = 12.3402,
                apiKey = "a50b3547c713e7be1ec57c696006497f",
                units = apiUnits,
                lang = selectedLang
            )
        }
    }

    val currentLocale = if (selectedLang.contains("ar", ignoreCase = true))
        Locale("ar") else Locale("en")

    CompositionLocalProvider(
        LocalLayoutDirection provides
                if (currentLocale.language == "ar")
                    LayoutDirection.Rtl
                else
                    LayoutDirection.Ltr
    ) {

        Box(modifier = Modifier.fillMaxSize()) {

            if (!showSplash) {

                if (!isOnline && currentScreen == "home" && weatherState !is MyResult.Success) {
                    NoInternetView(onRetry = {
                        viewModel.fetchWeatherForLocation(lastLat, lastLon)
                    })
                }
             else {
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
                                    selectedLang = selectedLang,
                                    isOnline = networkStatus,
                                    onRetry = {
                                        viewModel.fetchWeatherForLocation(lastLat, lastLon)
                                    }
                                )

                                "settings" -> SettingsScreen(settingsViewModel)
                                "fav" -> FavouriteWeatherScreen(favViewModel, settingsViewModel)
                                "alerts" -> AlertsScreen(alertsViewModel, selectedLang)
                            }
                        }

                        weatherState is MyResult.Error -> Text(
                            "Error: ${(weatherState as MyResult.Error).message}",
                            modifier = Modifier.align(Alignment.Center)
                        )
                    }
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
            if (showSplash) {

                SplashScreen()
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
