package com.example.weather.presentation.favourite.view

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
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.weather.data.config.db.WeatherState
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale

@Composable
fun WeatherDetailSheet(weather: WeatherState, onSaveClick: (WeatherState) -> Unit) {

    Column(modifier = Modifier.padding(24.dp).fillMaxWidth()) {
        Text(
            text = weather.cityName,
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )

        Text(
            text = "Current Temperature: ${weather.temp}°C",
            style = MaterialTheme.typography.bodyLarge,
            color = Color.Gray
        )

        Spacer(modifier = Modifier.height(24.dp))

        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            WeatherInfoItem("Humidity", "${weather.humidity}%")
            WeatherInfoItem("Wind", "${weather.windSpeed} km/h")
        }

        Spacer(modifier = Modifier.height(24.dp))

        HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp), thickness = 0.5.dp)

        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text("Description: ${weather.description}", style = MaterialTheme.typography.bodyMedium)

            Text(
                text = "🌅 Sunrise: ${formatUnixTime(weather.sunrise)}",
                style = MaterialTheme.typography.bodyMedium
            )
            Text(
                text = "🌇 Sunset: ${formatUnixTime(weather.sunset)}",
                style = MaterialTheme.typography.bodyMedium
            )
        }

        Button(
            onClick = { onSaveClick(weather) },
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 32.dp)
                .height(56.dp),
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary
            )
        ) {
            Text("Add to Favourites ❤️", style = MaterialTheme.typography.titleMedium)
        }
    }
}

fun formatUnixTime(timestamp: Double, pattern: String = "hh:mm a"): String {
    return try {
        val instant = Instant.ofEpochSecond(timestamp.toLong())
        val formatter = DateTimeFormatter.ofPattern(pattern, Locale.ENGLISH)
        instant.atZone(ZoneId.systemDefault()).format(formatter)
    } catch (e: Exception) {
        "--:--"
    }
}