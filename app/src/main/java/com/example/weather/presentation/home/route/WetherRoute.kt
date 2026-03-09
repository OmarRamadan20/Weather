import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
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
import androidx.compose.ui.graphics.RectangleShape
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
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.res.stringResource

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
            .padding(bottom = 16.dp, start = 16.dp, end = 16.dp)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .height(68.dp),
            shape = RoundedCornerShape(32.dp),
            color = Color.White,
            shadowElevation = 12.dp,
            border = BorderStroke(1.dp, Color.LightGray.copy(alpha = 0.2f))
        ) {
            Row(
                modifier = Modifier.fillMaxSize(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                NavIcon(R.drawable.ic_home, currentScreen == "home", label = stringResource(R.string.home)) { onNavigate("home") }
                NavIcon(R.drawable.ic_heart, currentScreen == "fav",label = stringResource(R.string.favourite) ) { onNavigate("fav") }
                NavIcon(
                    icon = R.drawable.ic_notification,
                    isSelected = currentScreen == "alerts",
                    label = stringResource(R.string.alerts),
                ) { onNavigate("alerts") }
                NavIcon(R.drawable.ic_settings, currentScreen == "settings", label = stringResource(R.string.settings)) { onNavigate("settings") }
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
    val scale by animateFloatAsState(targetValue = if (isSelected) 1.2f else 1.0f)
    val iconColor by animateColorAsState(
        targetValue = if (isSelected) Color(0xFF3F51B5) else Color.Gray.copy(alpha = 0.6f)
    )

    Column(
        modifier = Modifier
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) { onClick() }
            .padding(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box {
            Icon(
                painter = painterResource(id = icon),
                contentDescription = label,
                tint = iconColor,
                modifier = Modifier
                    .size(26.dp)
                    .scale(scale)
            )

        }

        if (isSelected) {
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                color = iconColor
            )
        }
    }
}
