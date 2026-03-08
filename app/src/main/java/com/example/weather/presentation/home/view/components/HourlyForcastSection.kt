package com.example.weather.presentation.home.view.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.weather.R
import com.example.weather.presentation.home.view.formatToHour
import com.example.weather.presentation.home.view.getTemperatureColors
import java.util.Locale

@Composable
fun HourlyForecastSection(hourlyData: List<com.example.weather.data.models.hourly.ListItem>,
                          selectedLang: String) {

    val currentLocale = if (selectedLang.contains("ar", ignoreCase = true) || selectedLang == "Arabic") {
        Locale("ar")
    } else {
        Locale("en")
    }




    Column {
        Text(
            text = stringResource(id = R.string.hourly_forecast),
            fontSize = 20.sp,
            fontWeight = FontWeight.Black,
            color = Color(0xFF2D3142),
        )
        Spacer(modifier = Modifier.height(18.dp))

        LazyRow(
            contentPadding = PaddingValues(horizontal = 20.dp, vertical = 15.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(hourlyData) { item ->
                val temp = item.main?.temp?.toInt() ?: 0
                val itemColors = getTemperatureColors(temp)

                val infiniteTransition = rememberInfiniteTransition(label = "glow")
                val alphaAnim by infiniteTransition.animateFloat(
                    initialValue = 0.7f,
                    targetValue = 1f,
                    animationSpec = infiniteRepeatable(
                        animation = tween(2500, easing = FastOutSlowInEasing),
                        repeatMode = RepeatMode.Reverse
                    ), label = ""
                )

                Box(
                    modifier = Modifier
                        .width(85.dp)
                        .height(160.dp)
                        .shadow(
                            elevation = 12.dp,
                            shape = RoundedCornerShape(32.dp),
                            spotColor = itemColors[0].copy(alpha = 0.4f)
                        )
                        .background(
                            brush = Brush.verticalGradient(
                                colors = listOf(
                                    itemColors[0].copy(alpha = alphaAnim),
                                    itemColors[1].copy(alpha = alphaAnim)
                                )
                            ),
                            shape = RoundedCornerShape(32.dp)
                        )
                        .border(
                            width = 1.5.dp,
                            color = Color.White.copy(alpha = 0.5f),
                            shape = RoundedCornerShape(32.dp)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                Brush.linearGradient(
                                    listOf(Color.White.copy(alpha = 0.2f), Color.Transparent),
                                    start = Offset.Zero,
                                    end = Offset(100f, 100f)
                                )
                            )
                    )

                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.SpaceEvenly,
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(vertical = 12.dp)
                    ) {
                        Text(
                            text = formatToHour(item.dt?.toInt(), currentLocale),
                            fontSize = 13.sp,
                            color = Color.White,
                            fontWeight = FontWeight.Bold
                        )

                        AsyncImage(
                            model = "https://openweathermap.org/img/wn/${item.weather?.getOrNull(0)?.icon}@4x.png",
                            contentDescription = null,
                            modifier = Modifier
                                .size(55.dp)
                                .graphicsLayer {
                                    translationY = (alphaAnim - 0.85f) * 20f
                                }
                        )

                        Text(
                            text = "$temp°",
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Black,
                            color = Color.White,
                            style = TextStyle(
                                shadow = Shadow(
                                    color = Color.Black.copy(alpha = 0.15f),
                                    offset = Offset(0f, 4f),
                                    blurRadius = 8f
                                )
                            )
                        )
                    }
                }
            }
        }
    }
}