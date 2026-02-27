package com.example.weather.presentation.favourite.view

import android.Manifest
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.weather.data.datasources.remote.network.MyResult
import com.example.weather.presentation.favourite.viewmodel.FavViewModel
import com.example.weather.presentation.settings.view.findActivity
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.*
import kotlinx.coroutines.launch

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun FavMapPickerScreen(
    viewModel: FavViewModel,
    onLocationSelected: (LatLng) -> Unit,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current

    val activity = remember(context) { context.findActivity() }

    if (activity != null) {
        CompositionLocalProvider(LocalContext provides activity) {
            val locationPermissionState = rememberPermissionState(
                Manifest.permission.ACCESS_FINE_LOCATION
            )

            LaunchedEffect(Unit) {
                locationPermissionState.launchPermissionRequest()
            }

            Surface(
                modifier = Modifier.fillMaxSize(),
                color = Color.White
            ) {
                FavMapLayout(
                    viewModel = viewModel,
                    isPermissionGranted = locationPermissionState.status.isGranted,
                    onLocationSelected = onLocationSelected,
                    onDismiss = onDismiss
                )
            }
        }
    }
}

@Composable
fun FavMapLayout(
    viewModel: FavViewModel,
    isPermissionGranted: Boolean,
    onLocationSelected: (LatLng) -> Unit,
    onDismiss: () -> Unit
) {
    val suggestionsResult by viewModel.citySuggestions.collectAsState()
    var searchQuery by remember { mutableStateOf("") }
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(LatLng(30.0444, 31.2357), 10f)
    }
    val scope = rememberCoroutineScope()

    Box(modifier = Modifier.fillMaxSize()) {
        GoogleMap(
            modifier = Modifier.fillMaxSize(),
            cameraPositionState = cameraPositionState,
            properties = MapProperties(isMyLocationEnabled = isPermissionGranted),
            uiSettings = MapUiSettings(myLocationButtonEnabled = isPermissionGranted, zoomControlsEnabled = false)
        )

        Column(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = 50.dp, start = 16.dp, end = 16.dp)
                .fillMaxWidth()
        ) {
            Card(shape = RoundedCornerShape(28.dp), elevation = CardDefaults.cardElevation(8.dp)) {
                TextField(
                    value = searchQuery,
                    onValueChange = {
                        searchQuery = it
                        viewModel.searchCities(it)
                    },
                    placeholder = { Text("Search City...") },
                    modifier = Modifier.fillMaxWidth(),
                    leadingIcon = { Icon(Icons.Default.Search, null) },
                    singleLine = true,
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color.White,
                        unfocusedContainerColor = Color.White,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent
                    )
                )
            }

            when (val result = suggestionsResult) {
                is MyResult.Success -> {
                    val cities = result.data
                    if (cities.isNotEmpty() && searchQuery.length >= 3) {
                        Card(
                            modifier = Modifier.padding(top = 8.dp).fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = Color.White)
                        ) {
                            LazyColumn(modifier = Modifier.heightIn(max = 250.dp)) {
                                items(cities) { city ->
                                    Text(
                                        text = "${city.name}, ${city.country}",
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clickable {
                                                val lat = city.lat ?: 0.0
                                                val lon = city.lon ?: 0.0
                                                scope.launch {
                                                    cameraPositionState.animate(
                                                        CameraUpdateFactory.newLatLngZoom(LatLng(lat, lon), 12f)
                                                    )
                                                }
                                                searchQuery = city.name ?: ""
                                                viewModel.onCitySelected(city)
                                            }
                                            .padding(16.dp)
                                    )
                                    HorizontalDivider(color = Color.LightGray.copy(alpha = 0.5f))
                                }
                            }
                        }
                    }
                }
                is MyResult.Loading -> {
                    LinearProgressIndicator(modifier = Modifier.fillMaxWidth().padding(top = 2.dp))
                }
                else -> {}
            }
        }

        Icon(
            imageVector = Icons.Default.LocationOn,
            contentDescription = null,
            tint = Color.Red,
            modifier = Modifier.size(50.dp).align(Alignment.Center).padding(bottom = 25.dp)
        )

        Row(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 50.dp, start = 20.dp, end = 20.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Button(
                onClick = { onDismiss() },
                modifier = Modifier.weight(1f).height(50.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color.Gray),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Cancel")
            }
            Button(
                onClick = { onLocationSelected(cameraPositionState.position.target) },
                modifier = Modifier.weight(1f).height(50.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF3F51B5)),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Confirm")
            }
        }
    }
}