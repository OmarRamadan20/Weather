package com.example.weather.presentation.favourite.view

import android.content.res.Configuration
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.weather.R
import com.example.weather.data.config.db.WeatherState
import com.example.weather.presentation.settings.viewmodel.SettingsViewModel
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale

@Composable

fun WeatherDetailSheet(weather: WeatherState,settingsViewModel: SettingsViewModel, onSaveClick: (WeatherState) -> Unit) {

    val context = LocalContext.current

    val currentLang by settingsViewModel.language.collectAsStateWithLifecycle()


    val currentLocale = if (currentLang.contains("ar", ignoreCase = true)) Locale("ar") else Locale("en")
    val configuration = Configuration(context.resources.configuration)
    configuration.setLocale(currentLocale)
    val localizedContext = context.createConfigurationContext(configuration)



    CompositionLocalProvider(
        LocalContext provides localizedContext,
        LocalLayoutDirection provides if (currentLocale.language == "ar") LayoutDirection.Rtl else LayoutDirection.Ltr
    ) {
        Column(modifier = Modifier.padding(24.dp).fillMaxWidth()) {

            Text(
                text = weather.cityName,
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold
            )

            Text(
                text = stringResource(R.string.current_temp, weather.temp),
                style = MaterialTheme.typography.bodyLarge,
                color = Color.Gray
            )

            Spacer(modifier = Modifier.height(24.dp))

            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                WeatherInfoItem(stringResource(R.string.humidity), "${weather.humidity}%")
                WeatherInfoItem(
                    stringResource(R.string.wind_speed),
                    stringResource(R.string.wind_unit, weather.windSpeed)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp), thickness = 0.5.dp)

            Column(Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    stringResource(R.string.description, weather.description),
                    style = MaterialTheme.typography.bodyMedium
                )

                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text(
                        text = stringResource(
                            R.string.sunrise,
                            formatUnixTime(weather.sunrise, currentLocale)
                        ),
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Text(
                        text = stringResource(
                            R.string.sunset,
                            formatUnixTime(weather.sunset, currentLocale)
                        ),
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }

            Button(
                onClick = { onSaveClick(weather) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 32.dp)
                    .height(56.dp),
                shape = RoundedCornerShape(16.dp)
            ) {
                Text(
                    stringResource(R.string.add_to_favourites),
                    style = MaterialTheme.typography.titleMedium
                )
            }
        }
    }
}

fun formatUnixTime(timestamp: Double, locale: Locale, pattern: String = "hh:mm a"): String {
    return try {
        val instant = Instant.ofEpochSecond(timestamp.toLong())
        val formatter = DateTimeFormatter.ofPattern(pattern, locale)
        instant.atZone(ZoneId.systemDefault()).format(formatter)
    } catch (e: Exception) {
        "--:--"
    }
}