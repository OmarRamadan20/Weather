package com.example.weather.presentation.favourite.view
import android.content.res.Configuration
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
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
import java.util.Locale


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FavouriteWeatherScreen(viewModel: FavViewModel, settingsViewModel: SettingsViewModel) {

    val favs by viewModel.favLocations.collectAsStateWithLifecycle()
    var showMapPicker by remember { mutableStateOf(false) }

    val currentUnits by settingsViewModel.tempUnit.collectAsStateWithLifecycle()



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
                FloatingActionButton(
                    onClick = { showMapPicker = true },
                    containerColor = Color(0xFF3F51B5),
                    contentColor = Color.White
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
                            viewModel.fetchWeatherForMapPoint(
                                lat = latLng.latitude,
                                lon = latLng.longitude,
                                apiKey = "a50b3547c713e7be1ec57c696006497f",
                                units = currentUnits,
                                lang = currentLang
                            )
                            showBottomSheet = true
                        },
                        onDismiss = { showMapPicker = false }
                    )
                }
            }
        }


    }
}



@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FavItemRow(
    location: FavLocation,
    currentLang: String,
    onDelete: (FavLocation) -> Unit
) {
    val dismissState = rememberSwipeToDismissBoxState(
        confirmValueChange = {
            if (it == SwipeToDismissBoxValue.EndToStart) {
                onDelete(location)
                true
            } else false
        }
    )

    SwipeToDismissBox(
        state = dismissState,
        enableDismissFromStartToEnd = false,
        backgroundContent = {
            val color = if (dismissState.dismissDirection == SwipeToDismissBoxValue.EndToStart) Color.Red else Color.Transparent
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
                    .background(color, RoundedCornerShape(16.dp)),
                contentAlignment = Alignment.CenterEnd
            ) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Delete",
                    tint = Color.White,
                    modifier = Modifier.padding(end = 16.dp)
                )
            }
        }
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(2.dp)
        ) {
            Row(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = location.name,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                    val latText = if (currentLang == "ar") "خط عرض" else "Lat"
                    val lonText = if (currentLang == "ar") "خط طول" else "Lon"

                    Text(
                        text = "$latText: ${String.format("%.2f", location.lat)}, $lonText: ${String.format("%.2f", location.lon)}",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.Gray
                    )
                }

                val unit = if (currentLang == "ar") "°م" else "°C"
                Text(
                    text = "${location.temp}$unit",
                    style = MaterialTheme.typography.headlineMedium,
                    color = Color(0xFF3F51B5),
                    fontWeight = FontWeight.ExtraBold
                )
            }
        }
    }
}