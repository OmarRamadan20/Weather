package com.example.weather.presentation.favourite.view
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
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
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.weather.data.config.db.FavLocation
import com.example.weather.data.config.db.WeatherState
import com.example.weather.presentation.favourite.viewmodel.FavViewModel
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FavouriteWeatherScreen(viewModel: FavViewModel) {
    val favs by viewModel.favLocations.collectAsStateWithLifecycle()
    var showMapPicker by remember { mutableStateOf(false) }

    val selectedWeather by viewModel.selectedWeather.collectAsStateWithLifecycle()
    var showBottomSheet by remember { mutableStateOf(false) }



    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    Log.d("FAB", "Clicked!")
                    showMapPicker = true },
                containerColor = Color(0xFF3F51B5),
                contentColor = Color.White
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Location")
            }
        }
    ) { padding ->
        Column(modifier = Modifier.padding(padding).fillMaxSize()) {
            Text(
                "My Favourite Locations",
                style = MaterialTheme.typography.headlineMedium,
                modifier = Modifier.padding(16.dp)
            )

            if (favs.isEmpty()) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("No favourites added yet", color = Color.Gray)
                }
            } else {
                LazyColumn {
                    items(favs, key = { it.id }) { location ->
                        FavItemRow(location) {
                            viewModel.deleteFromFav(location)
                        }
                    }
                }
            }
        }

        // إظهار الـ Map Picker كـ Overlay فوق الشاشة
    }
    if (showMapPicker) {
        Dialog(
            onDismissRequest = { showMapPicker = false },
            properties = DialogProperties(
                usePlatformDefaultWidth = false // عشان يملأ الشاشة كلها كأنه شاشة جديدة
            )
        ) {
            // نغلف الـ Picker بـ Surface عشان نضمن الخلفية والـ Size
            Surface(modifier = Modifier.fillMaxSize()) {
                FavMapPickerScreen(
                    viewModel = viewModel,
                    onLocationSelected = { latLng ->
                        viewModel.fetchWeatherForMapPoint(
                            latLng.latitude,
                            latLng.longitude,
                            apiKey = "a50b3547c713e7be1ec57c696006497f",
                            units = "metric",
                            lang = "en"
                        )
                        showBottomSheet = true

                    },
                    onDismiss = { showMapPicker = false }
                )
            }
        }
    }
    val context = LocalContext.current
    if (showBottomSheet && selectedWeather != null) {
        ModalBottomSheet(
            onDismissRequest = {
                showBottomSheet = false
            },
            containerColor = Color.White
        ) {

            WeatherDetailSheet(
                weather = selectedWeather!!,
                onSaveClick = { weather ->
                    viewModel.saveToFavourites(weather)
                    showBottomSheet = false
                    Toast.makeText(context,
                        "${weather.cityName} added to favourites!",
                        Toast.LENGTH_SHORT
                    ).show()
                }


            )
        }
    }

}



@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FavItemRow(
    location: FavLocation,
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
                    Text(
                        text = "Lat: ${String.format("%.2f", location.lat)}, Lon: ${String.format("%.2f", location.lon)}",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.Gray
                    )
                }

                Text(
                    text = "${location.temp}°C",
                    style = MaterialTheme.typography.headlineMedium,
                    color = Color(0xFF3F51B5),
                    fontWeight = FontWeight.ExtraBold
                )
            }
        }
    }
}