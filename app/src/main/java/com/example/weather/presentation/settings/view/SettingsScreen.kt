package com.example.weather.presentation.settings.view

import android.R.attr.onClick
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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.weather.R
import com.example.weather.presentation.settings.viewmodel.SettingsViewModel

@Composable
fun SettingsScreen(viewModel: SettingsViewModel) {

    val tempUnit by viewModel.tempUnit.collectAsState()
    val windUnit by viewModel.windUnit.collectAsState()
    val selectedLang by viewModel.language.collectAsState()
    val useGPS by viewModel.isGpsEnabled.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(20.dp)
    ) {
        Text(
            text = "Settings",
            fontSize = 32.sp,
            fontWeight = FontWeight.Black,
            color = Color(0xFF2D3142),
            modifier = Modifier.padding(top = 40.dp, bottom = 20.dp)
        )

        SettingsCard(title = stringResource(R.string.temp_unit)) {
            SegmentedControl(
                options = listOf(stringResource(R.string.celsius), stringResource(R.string.fahrenheit),
                    stringResource(R.string.kelvin)),
                selectedOption = tempUnit,
                onOptionSelected = { viewModel.updateTempUnit(it) }
            )
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
                modifier = Modifier.clickable { /* افتح الخريطة */ }
            )
        }


        // 4. Language (Dropdown Menu)
        SettingsCard(title = stringResource(R.string.language)) {
            var expanded by remember { mutableStateOf(false) }

            // اللستة اللي فيها الاسم المترجم والكود
            val languages = listOf(
                stringResource(R.string.english) to "en",
                stringResource(R.string.arabic) to "ar"
            )

            Box {
                Text(
                    text = selectedLang, // القيمة اللي جاية من الـ ViewModel
                    color = Color(0xFF3F51B5),
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { expanded = true }
                        .padding(vertical = 8.dp)
                )

                // لازم الـ DropdownMenu يكون شايل الـ Items جواه
                DropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false },
                    modifier = Modifier.background(Color.White.copy(alpha = 0.9f))
                ) {
                    languages.forEach { lang ->
                        DropdownMenuItem(
                            text = { Text(text = lang.first) }, // بيعرض الاسم (English أو العربية)
                            onClick = {
                                viewModel.updateLanguage(lang.first) // بيبعت الاسم للـ ViewModel
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