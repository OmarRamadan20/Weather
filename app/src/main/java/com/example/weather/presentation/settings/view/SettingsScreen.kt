package com.example.weather.presentation.settings.view

import android.content.res.Configuration
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupProperties
import com.example.weather.R
import com.example.weather.presentation.settings.viewmodel.SettingsViewModel
import java.util.Locale

@Composable
fun SettingsScreen(viewModel: SettingsViewModel) {

    val tempUnit by viewModel.tempUnit.collectAsState()
    val windUnit by viewModel.windUnit.collectAsState()
    val selectedLang by viewModel.language.collectAsState()
    val useGPS by viewModel.isGpsEnabled.collectAsState()
    val isMapVisible by viewModel.isMapVisible.collectAsState()

    val context = LocalContext.current
    val currentLocale = if (selectedLang.contains("ar", ignoreCase = true)) Locale("ar") else Locale("en")
    val configuration = Configuration(context.resources.configuration)
    configuration.setLocale(currentLocale)
    val localizedContext = context.createConfigurationContext(configuration)

    CompositionLocalProvider(
        LocalContext provides localizedContext,
        LocalLayoutDirection provides if (currentLocale.language == "ar") LayoutDirection.Rtl else LayoutDirection.Ltr
    ) {
        Box(modifier = Modifier.fillMaxSize().background(Color(0xFFF5F7FA))) {

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(20.dp)
            ) {
                Text(
                    text = stringResource(R.string.settings),
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Black,
                    color = Color(0xFF2D3142),
                    modifier = Modifier.padding(top = 40.dp, bottom = 20.dp)
                )

                SettingsCard(title = stringResource(R.string.temp_unit)) {
                    val unitOptions = listOf(
                        Triple("metric", stringResource(R.string.metric_label), stringResource(R.string.celsius) + " " + stringResource(R.string.m_s)),
                        Triple("imperial", stringResource(R.string.imperial_label), stringResource(R.string.fahrenheit) + " " + stringResource(R.string.mph)),
                        Triple("standard", stringResource(R.string.standard_label), stringResource(R.string.kelvin) + " " + stringResource(R.string.m_s))
                    )

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(15.dp))
                            .background(Color.Gray.copy(alpha = 0.1f))
                            .padding(4.dp)
                    ) {
                        unitOptions.forEach { option ->
                            val isSelected = tempUnit == option.first

                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(if (isSelected) Color.White else Color.Transparent)
                                    .clickable {
                                        viewModel.updateUnits(option.first)
                                    }
                                    .padding(vertical = 12.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text(
                                        text = option.second,
                                        color = if (isSelected) Color(0xFF3F51B5) else Color.Gray,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 13.sp
                                    )
                                    Text(
                                        text = "(${option.third})",
                                        color = if (isSelected) Color(0xFF3F51B5).copy(alpha = 0.7f) else Color.Gray,
                                        fontSize = 10.sp
                                    )
                                }
                            }
                        }
                    }
                }

                SettingsCard(title = stringResource(R.string.location_source)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.LocationOn, contentDescription = null, tint = Color(0xFF3F51B5))
                        Spacer(Modifier.width(10.dp))
                        Text(stringResource(R.string.use_gps_for_location), modifier = Modifier.weight(1f))
                        Switch(checked = useGPS, onCheckedChange = { viewModel.toggleGps(it) })
                    }
                    Spacer(Modifier.height(8.dp))
                    Text(
                        text = stringResource(R.string.or_select_manually_on_map),
                        color = Color(0xFF3F51B5),
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.clickable { viewModel.showMap() }.padding(vertical = 4.dp)
                    )
                }

                SettingsCard(title = stringResource(R.string.language)) {
                    var expanded by remember { mutableStateOf(false) }

                    val displayText = if (selectedLang.contains("ar", ignoreCase = true))
                        stringResource(R.string.arabic)
                    else
                        stringResource(R.string.english)

                    Box(modifier = Modifier.fillMaxWidth()) {
                        OutlinedCard(
                            onClick = { expanded = true },
                            shape = RoundedCornerShape(12.dp),
                            border = BorderStroke(1.dp, Color.LightGray.copy(alpha = 0.5f)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier
                                    .padding(16.dp)
                                    .fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = displayText,
                                    color = Color(0xFF3F51B5),
                                    fontWeight = FontWeight.Bold
                                )
                                Icon(
                                    painter = painterResource(id = android.R.drawable.arrow_down_float),
                                    contentDescription = null,
                                    tint = Color.Gray,
                                    modifier = Modifier.size(12.dp)
                                )
                            }
                        }

                        DropdownMenu(
                            expanded = expanded,
                            onDismissRequest = { expanded = false },
                            modifier = Modifier
                                .background(Color.White)
                                .fillMaxWidth(0.8f)
                        ) {
                            listOf(
                                stringResource(R.string.english) to "en",
                                stringResource(R.string.arabic) to "ar"
                            ).forEach { lang ->
                                DropdownMenuItem(
                                    text = {
                                        Text(
                                            text = lang.first,
                                            modifier = Modifier.fillMaxWidth(),
                                            fontWeight = if (selectedLang == lang.second) FontWeight.Bold else FontWeight.Normal
                                        )
                                    },
                                    onClick = {
                                        viewModel.updateLanguage(lang.second)
                                        expanded = false
                                    }
                                )
                            }
                        }
                    }
                }
                Spacer(modifier = Modifier.height(100.dp))
            }

            if (isMapVisible) {
                Popup(
                    properties = PopupProperties(
                        focusable = true,
                        excludeFromSystemGesture = true
                    )
                ) {
                    Surface(modifier = Modifier.fillMaxSize(), color = Color.White) {
                        MapPickerScreen(
                            viewModel = viewModel,
                            onLocationSelected = { latLng ->
                                viewModel.getWeatherByMaps(latLng.latitude, latLng.longitude)
                            },
                            onDismiss = {
                                viewModel.hideMap()
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun SettingsCard(title: String, content: @Composable ColumnScope.() -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth().padding(vertical = 10.dp),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.9f)),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        border = BorderStroke(1.dp, Color.White.copy(alpha = 0.5f))
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Text(text = title, fontSize = 14.sp, color = Color.Gray, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(15.dp))
            content()
        }
    }
}