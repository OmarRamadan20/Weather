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
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
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
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.weather.R
import com.example.weather.presentation.alerts.view.AlertsScreen
import com.example.weather.presentation.alerts.viewmodel.AlertsViewModel
import com.example.weather.presentation.favourite.view.FavouriteWeatherScreen
import com.example.weather.presentation.favourite.viewmodel.FavViewModel
import com.example.weather.presentation.home.view.HomeScreenContent
import com.example.weather.presentation.home.viewmodel.HomeViewModel
import com.example.weather.presentation.settings.view.SettingsScreen
import com.example.weather.presentation.settings.viewmodel.SettingsViewModel
import com.example.weather.presentation.splash.SplashScreen
import java.util.Locale

@RequiresApi(Build.VERSION_CODES.TIRAMISU)
@Composable
fun WeatherRoute(
    homeViewModel: HomeViewModel,
    settingsViewModel: SettingsViewModel,
    favViewModel: FavViewModel,
    alertsViewModel: AlertsViewModel
) {
    val navController = rememberNavController()
    val selectedLang by settingsViewModel.language.collectAsState()
    var showSplash by remember { mutableStateOf(true) }

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    LaunchedEffect(Unit) {
        kotlinx.coroutines.delay(2500)
        showSplash = false
    }

    val currentLocale = if (selectedLang.contains("ar", ignoreCase = true)) Locale("ar") else Locale("en")

    CompositionLocalProvider(
        LocalLayoutDirection provides if (currentLocale.language == "ar") LayoutDirection.Rtl else LayoutDirection.Ltr
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            if (showSplash) {
                SplashScreen()
            } else {
                NavHost(
                    navController = navController,
                    startDestination = "home",
                    modifier = Modifier.fillMaxSize()
                ) {
                    composable("home") {
                        HomeScreenContent(homeViewModel, settingsViewModel)
                    }
                    composable("fav") {
                        FavouriteWeatherScreen(favViewModel, settingsViewModel)
                    }
                    composable("alerts") {
                        AlertsScreen(alertsViewModel, selectedLang)
                    }
                    composable("settings") {
                        SettingsScreen(settingsViewModel)
                    }
                }

                Box(modifier = Modifier.align(Alignment.BottomCenter)) {
                    WeatherBottomBar(
                        currentScreen = currentRoute ?: "home",
                        onNavigate = { route ->
                            if (currentRoute != route) {
                                navController.navigate(route) {
                                    popUpTo("home") { saveState = true }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            }
                        }
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
                NavIcon(
                    icon = R.drawable.ic_home,
                    isSelected = currentScreen == "home",
                    label = "Home"
                ) { onNavigate("home") }

                NavIcon(
                    icon = R.drawable.ic_heart,
                    isSelected = currentScreen == "fav",
                    label = "Favorites"
                ) { onNavigate("fav") }

                NavIcon(
                    icon = R.drawable.ic_notification,
                    isSelected = currentScreen == "alerts",
                    label = "Alerts"
                ) { onNavigate("alerts") }

                NavIcon(
                    icon = R.drawable.ic_settings,
                    isSelected = currentScreen == "settings",
                    label = "Settings"
                ) { onNavigate("settings") }
            }
        }
    }
}

@Composable
fun NavIcon(
    icon: Int,
    isSelected: Boolean,
    label: String,
    onClick: () -> Unit
) {
    IconButton(onClick = onClick) {
        Icon(
            painter = painterResource(id = icon),
            contentDescription = label,
            tint = if (isSelected) Color(0xFF3F51B5) else Color.Gray.copy(alpha = 0.5f),
            modifier = Modifier.size(if (isSelected) 28.dp else 24.dp)
        )
    }
}
