package com.example.weather.presentation.home.view.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.weather.data.models.weather.WeatherResponse
import com.example.weather.presentation.home.view.getTemperatureColors

@Composable
fun MainWeatherCard(weather: WeatherResponse) {
    val temp = weather.main?.temp?.toInt() ?: 0
    val condition = weather.weather?.get(0)?.main ?: "Clear"
    val dynamicColors = getTemperatureColors(temp)

    val color1 by animateColorAsState(targetValue = dynamicColors[0], animationSpec = tween(1200))
    val color2 by animateColorAsState(targetValue = dynamicColors[1], animationSpec = tween(1200))

    Card(
        shape = RoundedCornerShape(45.dp),
        modifier = Modifier
            .fillMaxWidth()
            .height(350.dp)
            .shadow(25.dp, RoundedCornerShape(45.dp), ambientColor = color1, spotColor = color1)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Brush.verticalGradient(listOf(color1, color2)))
        ) {
            when {
                condition.contains("Rain", true) -> RainAnimation()
                condition.contains("Snow", true) -> SnowAnimation()
                condition.contains("Cloud", true) -> CloudAnimation()
                condition.contains("Clear", true) -> SunnyAnimation(temp)
                else -> SunnyAnimation(temp)
            }

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(30.dp),
                verticalArrangement = Arrangement.SpaceBetween,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = weather.name ?: "Cairo",
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Black,
                    color = Color.White
                )

                Text(
                    text = "$temp°",
                    fontSize = 110.sp,
                    fontWeight = FontWeight.W100,
                    color = Color.White
                )

                Surface(
                    color = Color.White.copy(alpha = 0.2f),
                    shape = CircleShape
                ) {
                    Text(
                        text = weather.weather?.get(0)?.description?.uppercase() ?: "",
                        modifier = Modifier.padding(horizontal = 25.dp, vertical = 8.dp),
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp
                    )
                }
            }
        }
    }
}