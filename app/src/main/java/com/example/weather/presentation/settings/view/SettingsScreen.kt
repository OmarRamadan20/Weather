package com.example.weather.presentation.settings.view

import android.content.res.Configuration
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.weather.R
import com.example.weather.presentation.settings.viewmodel.SettingsViewModel
import java.util.Locale

@Composable
fun SettingsScreen(viewModel: SettingsViewModel) {

    val tempUnit by viewModel.tempUnit.collectAsState()
    val windUnit by viewModel.windUnit.collectAsState()
    val selectedLang by viewModel.language.collectAsState()
    val useGPS by viewModel.isGpsEnabled.collectAsState()

    val context = LocalContext.current
    val currentLocale = if (selectedLang.contains("ar", ignoreCase = true))
        Locale("ar") else Locale("en")

    val configuration = Configuration(context.resources.configuration)
    configuration.setLocale(currentLocale)

    val localizedContext = context.createConfigurationContext(configuration)

    CompositionLocalProvider(LocalContext provides localizedContext,
        LocalLayoutDirection provides if (currentLocale.language == "ar") LayoutDirection.Rtl else LayoutDirection.Ltr) {

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
                Triple("metric", stringResource(R.string.metric_label), stringResource(R.string.celsius)+" "+ " "+ stringResource(R.string.m_s)),
                Triple("imperial", stringResource(R.string.imperial_label), stringResource(R.string.fahrenheit)+" "+ " "+ stringResource(R.string.mph)),
                Triple("standard", stringResource(R.string.standard_label), stringResource(R.string.kelvin)+" "+ " "+ stringResource(R.string.m_s))
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(15.dp))
                    .background(Color.Gray.copy(alpha = 0.1f))
                    .padding(4.dp)
            ) {
                unitOptions.forEach { option ->
                    val (apiCode, label, symbol) = option
                    val isSelected = tempUnit == apiCode

                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(12.dp))
                            .background(if (isSelected) Color.White else Color.Transparent)
                            .clickable { viewModel.updateUnits(apiCode) }
                            .padding(vertical = 12.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = label,
                                color = if (isSelected) Color(0xFF3F51B5) else Color.Gray,
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp
                            )
                            Text(
                                text = "($symbol)",
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
            Text(
                text = stringResource(R.string.or_select_manually_on_map),
                color = Color(0xFF3F51B5),
                fontWeight = FontWeight.Bold,
                modifier = Modifier.clickable {

                    viewModel.getWeatherByMaps()
                }
            )
        }


        // 4. Language (Dropdown Menu)
        SettingsCard(title = stringResource(R.string.language)) {
            var expanded by remember { mutableStateOf(false) }

            val languages = listOf(
                stringResource(R.string.english) to "en",
                stringResource(R.string.arabic) to "ar"
            )
            val displayText = if (selectedLang.contains("ar", ignoreCase = true))
                stringResource(R.string.arabic)
            else
                stringResource(R.string.english)

            Box {
                Text(
                    text = displayText,
                    color = Color(0xFF3F51B5),
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { expanded = true }
                        .padding(vertical = 8.dp)
                )


                DropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false },
                    modifier = Modifier.background(Color.White.copy(alpha = 0.9f))
                ) {
                    languages.forEach { lang ->
                        DropdownMenuItem(
                            text = { Text(text = lang.first) },
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
    }
}

@Composable
fun SettingsCard(title: String, content: @Composable ColumnScope.() -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 10.dp),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White.copy(alpha = 0.7f) // زجاجي خفيف
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp), // شيل الظل التقيل
        border = BorderStroke(1.dp, Color.White.copy(alpha = 0.5f)) // تحديد أبيض شيك
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Text(text = title, fontSize = 14.sp, color = Color.Gray, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(15.dp))
            content()
        }
    }
}

@Composable
fun SegmentedControl(
    options: List<String>,
    selectedOption: String, // نعدل دي
    onOptionSelected: (String) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(15.dp))
            .background(Color.Gray.copy(alpha = 0.1f))
            .padding(4.dp)
    ) {
        options.forEach { option ->
            val isSelected = selectedOption == option
            Box(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(12.dp))
                    .background(if (isSelected) Color.White else Color.Transparent)
                    .clickable { onOptionSelected(option) }
                    .padding(vertical = 8.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = option,
                    color = if (isSelected) Color(0xFF3F51B5) else Color.Gray,
                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                    fontSize = 12.sp
                )
            }
        }
    }
}