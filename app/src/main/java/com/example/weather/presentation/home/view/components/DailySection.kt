package com.example.weather.presentation.home.view.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.weather.R
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


@Composable
fun DailyForecastSection(dailyData: List<com.example.weather.data.models.daily.ListItem>,
                         selectedLang: String) {
    if (dailyData.isEmpty()) return

    val header = if(selectedLang.contains("ar", ignoreCase = true) || selectedLang == "Arabic") "الايام القادمه" else "Next 7 Days"

    Column(
        modifier = Modifier
            .padding(horizontal = 22.dp, vertical = 20.dp)
            .fillMaxWidth()
    ) {
        Text(
            text = header,
            style = TextStyle(
                fontSize = 22.sp,
                fontWeight = FontWeight.Black,
                color = Color(0xFF2D3142),
                shadow = Shadow(color = Color.Black.copy(alpha = 0.1f), blurRadius = 5f)
            ),
            modifier = Modifier.padding(bottom = 16.dp)
        )

        dailyData.take(7).forEachIndexed { index, day ->
            DailyWeatherCard(day, index, selectedLang)
            Spacer(modifier = Modifier.height(12.dp))
        }
    }
}

@Composable
fun DailyWeatherCard(day: com.example.weather.data.models.daily.ListItem, index: Int, selectedLang: String) {

    val timestamp = (day.dt?.toLong() ?: 0L) * 1000L

    val currentLocale = if (selectedLang.contains("ar", ignoreCase = true) || selectedLang == "Arabic") {
        Locale("ar")
    } else {
        Locale("en")
    }

    val sdf = SimpleDateFormat("EEEE", currentLocale)


    val maxTemp = (day.temp?.max as? Number)?.toInt() ?: 0
    val minTemp = (day.temp?.min as? Number)?.toInt() ?: 0


    val todayStr = stringResource(id = R.string.today)

    val dayName = if (index == 0) todayStr else sdf.format(Date(timestamp))

    val iconCode = day.weather?.getOrNull(0)?.icon ?: "01d"
    val iconUrl = "https://openweathermap.org/img/wn/$iconCode@4x.png"

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(24.dp))
            .background(
                brush = Brush.linearGradient(
                    colors = listOf(
                        Color.White.copy(alpha = 0.9f),
                        Color.White.copy(alpha = 0.6f)
                    )
                )
            )
            .border(1.dp, Color.White, RoundedCornerShape(24.dp))
            .padding(16.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = dayName,
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF2D3142)
                )
                Text(
                    text = day.weather?.getOrNull(0)?.description?.replaceFirstChar { it.uppercase() } ?: "Clear",
                    fontSize = 13.sp,
                    color = Color.Gray
                )
            }

            AsyncImage(
                model = iconUrl,
                contentDescription = "Weather Icon",
                modifier = Modifier.size(60.dp),
                contentScale = ContentScale.Fit
            )

            Row(
                modifier = Modifier.weight(1f),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "$maxTemp°",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Black,
                    color = Color(0xFF2D3142)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Box(modifier = Modifier
                    .width(2.dp)
                    .height(20.dp)
                    .background(Color.LightGray.copy(alpha = 0.5f)))
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "$minTemp°",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color.Gray
                )
            }
        }
    }
}
