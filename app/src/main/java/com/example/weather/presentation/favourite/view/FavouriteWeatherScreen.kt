package com.example.weather.presentation.favourite.view
import android.content.res.Configuration
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Air
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.Text
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.weather.data.config.db.FavLocation
import com.example.weather.presentation.favourite.viewmodel.FavViewModel
import com.example.weather.presentation.settings.viewmodel.SettingsViewModel
import com.example.weather.utils.NetworkObserver
import java.util.Locale


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FavouriteWeatherScreen(viewModel: FavViewModel, settingsViewModel: SettingsViewModel) {

    val favs by viewModel.favLocations.collectAsStateWithLifecycle()
    var showMapPicker by remember { mutableStateOf(false) }

    val currentUnits by settingsViewModel.tempUnit.collectAsStateWithLifecycle()

    val networkStatus by viewModel.networkStatus.collectAsState()


    val currentLang by settingsViewModel.language.collectAsStateWithLifecycle()

    var showBottomSheet by remember { mutableStateOf(false) }
    val selectedWeather by viewModel.selectedWeather.collectAsStateWithLifecycle()



    val context = LocalContext.current

    val currentLocale = if (currentLang.contains("ar", ignoreCase = true)) Locale("ar") else Locale("en")
    val configuration = Configuration(context.resources.configuration)
    configuration.setLocale(currentLocale)
    val localizedContext = context.createConfigurationContext(configuration)

    CompositionLocalProvider(
        LocalContext provides localizedContext,
        LocalLayoutDirection provides if (currentLocale.language == "ar") LayoutDirection.Rtl else LayoutDirection.Ltr
    ) {
        Log.e("FavScreen", "Lang: $currentLang")
        Scaffold(
            floatingActionButton = {
                val isOffline = networkStatus == NetworkObserver.Status.Lost
                FloatingActionButton(
                    onClick = {
                        if (isOffline) {
                            Toast.makeText(context, "No Internet", Toast.LENGTH_SHORT).show()
                        } else {
                            showMapPicker = true
                        }
                    },
                    containerColor = if (isOffline) Color.Gray else Color(0xFF3F51B5),
                    contentColor = Color.White,
                    shape = CircleShape,
                    modifier = Modifier.padding(bottom = 80.dp,end = 10.dp)
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Add Location")
                }
            }
        ) { padding ->
            Column(modifier = Modifier.padding(padding).fillMaxSize()) {
                Text(
                    text = if (currentLang == "ar") "مواقعي المفضلة" else "My Favourite Locations",
                    style = MaterialTheme.typography.headlineMedium,
                    modifier = Modifier.padding(16.dp)
                )

                if (favs.isEmpty()) {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text(
                            text = if (currentLang == "ar") "لا يوجد مفضلات بعد" else "No favourites added yet",
                            color = Color.Gray
                        )
                    }
                } else {
                    LazyColumn {
                        items(favs, key = { it.id }) { location ->
                            FavItemRow(location, currentLang) {
                                viewModel.deleteFromFav(location)
                            }
                        }
                    }
                }
            }
        }
        val context = LocalContext.current
        if (showBottomSheet && selectedWeather != null) {
            ModalBottomSheet(
                onDismissRequest = { showBottomSheet = false },
                containerColor = Color.White
            ) {
                WeatherDetailSheet(
                    settingsViewModel =settingsViewModel,
                    weather = selectedWeather!!,
                    onSaveClick = { weather ->
                        viewModel.saveToFavourites(weather)
                        showBottomSheet = false

                        val message = if (currentLang == "ar")
                            "تم إضافة ${weather.cityName} للمفضلة!"
                        else "${weather.cityName} added to favourites!"

                        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
                    }
                )
            }
        }

        if (showMapPicker) {
            Dialog(
                onDismissRequest = { showMapPicker = false },
                properties = DialogProperties(usePlatformDefaultWidth = false)
            ) {
                Surface(modifier = Modifier.fillMaxSize()) {
                    FavMapPickerScreen(
                        favViewModel = viewModel,
                        settingsViewModel = settingsViewModel,
                        onLocationSelected = { latLng ->
                            if (networkStatus == NetworkObserver.Status.Available) {
                                viewModel.fetchWeatherForMapPoint(
                                    lat = latLng.latitude,
                                    lon = latLng.longitude,
                                    apiKey = "a50b3547c713e7be1ec57c696006497f",
                                    units = currentUnits,
                                    lang = currentLang
                                )
                                showBottomSheet = true
                            } else {
                                Toast.makeText(
                                    context,
                                    if (currentLang == "ar") "انقطع الاتصال بالإنترنت" else "Connection lost",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        },
                        onDismiss = { showMapPicker = false }
                    )
                }
            }
        }


    }
}



