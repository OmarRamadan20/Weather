package com.example.weather.presentation.home.view

import android.content.res.Configuration
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.weather.R
import com.example.weather.data.models.daily.DailyResponse
import com.example.weather.data.models.hourly.HourlyResponse
import com.example.weather.data.models.forecast.ListItem
import com.example.weather.data.models.weather.WeatherResponse
import com.example.weather.presentation.home.view.components.DailyForecastSection
import com.example.weather.presentation.home.view.components.HourlyForecastSection
import com.example.weather.presentation.home.view.components.MainWeatherCard
import com.example.weather.presentation.home.view.components.SunPhaseSection
import com.example.weather.utils.NetworkObserver
import com.example.weather.utils.NoInternetView
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.collections.take
import kotlin.math.sin
import kotlin.random.Random

val SoftPink = Color(0xFFFDEFF9)
val SoftBlue = Color(0xFFECF2FF)
val AzureBlue = Color(0xFF3F51B5)
val GlassWhite = Color(0xFFFFFFFF).copy(alpha = 0.9f)

@Composable
fun WeatherScreen(
    weatherData: WeatherResponse,
    hourlyResponse: HourlyResponse,
    dailyData: DailyResponse,
    isOnline: NetworkObserver.Status,
    selectedLang: String,
    onRetry: () -> Unit
) {

    val context = LocalContext.current
    val currentLocale = if (selectedLang.contains("ar", ignoreCase = true))
        Locale("ar") else Locale("en")


    val configuration = Configuration(context.resources.configuration)
    configuration.setLocale(currentLocale)

    val localizedContext = context.createConfigurationContext(configuration)

    CompositionLocalProvider(LocalContext provides localizedContext,
        LocalLayoutDirection provides if (currentLocale.language == "ar") LayoutDirection.Rtl else LayoutDirection.Ltr) {
        val forecastList = hourlyResponse.list?.filterNotNull() ?: emptyList()
        val dailyData = dailyData.list?.filterNotNull() ?: emptyList()

        if (isOnline == NetworkObserver.Status.Lost) {
            NoInternetView(onRetry = onRetry)
        }else {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Brush.verticalGradient(listOf(SoftBlue, SoftPink)))
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    MainWeatherCard(weatherData)

                    Spacer(modifier = Modifier.height(30.dp))

                    HourlyForecastSection(hourlyData = forecastList, selectedLang = selectedLang)

                    Spacer(modifier = Modifier.height(30.dp))

                    WeatherDetailGrid(weatherData)

                    Spacer(modifier = Modifier.height(30.dp))

                    SunPhaseSection(weatherData, currentLocale)

                    Spacer(modifier = Modifier.height(30.dp))

                    DailyForecastSection(
                        dailyData = dailyData,
                        selectedLang = selectedLang
                    )
                    Spacer(modifier = Modifier.height(40.dp))
                }
            }
        }
    }
}


@Composable
fun getTemperatureColors(temp: Int): List<Color> {
    return listOf(
        Color(0xFF3F51B5),
        Color(0xFF5C6BC0)
    )
}

@Composable
fun WeatherDetailGrid(weather: WeatherResponse) {

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        DetailBox(
            modifier = Modifier.weight(1f),
            title = stringResource(R.string.wind_speed),
            value = "${weather.wind?.speed}",
            unit = "km/h",
            icon = R.drawable.ic_wind,
            iconColor = Color(0xFF4FACFE),
            gradient = listOf(Color(0xFFE0F2F1), Color(0xFFFFFFFF))
        )
        DetailBox(
            modifier = Modifier.weight(1f),
            title = stringResource(R.string.humidity),
            value = "${weather.main?.humidity}",
            unit = "%",
            icon = R.drawable.ic_humidity,
            iconColor = Color(0xFF4361EE),
            gradient = listOf(Color(0xFFE8EAF6), Color(0xFFFFFFFF))
        )
    }
}

@Composable
fun DetailBox(
    modifier: Modifier,
    title: String,
    value: String,
    unit: String,
    icon: Int,
    iconColor: Color,
    gradient: List<Color>
) {

    Surface(
        modifier = modifier
            .height(140.dp)
            .shadow(15.dp, RoundedCornerShape(35.dp), ambientColor = iconColor.copy(alpha = 0.3f)),
        shape = RoundedCornerShape(35.dp),
        color = Color.White
    ) {
        Box(modifier = Modifier
            .background(Brush.verticalGradient(gradient))
            .fillMaxSize()) {
            Column(
                modifier = Modifier
                    .padding(20.dp)
                    .fillMaxSize(),
                verticalArrangement = Arrangement.SpaceBetween,
                horizontalAlignment = Alignment.Start
            ) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .background(iconColor.copy(alpha = 0.1f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        painter = painterResource(icon),
                        contentDescription = null,
                        modifier = Modifier.size(22.dp),
                        tint = iconColor
                    )
                }

                Column {
                    Row(verticalAlignment = Alignment.Bottom) {
                        Text(
                            text = value,
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Black,
                            color = Color(0xFF2D3142)
                        )
                        Text(
                            text = " $unit",
                            fontSize = 12.sp,
                            color = Color.Gray,
                            modifier = Modifier.padding(bottom = 4.dp)
                        )
                    }
                    Text(
                        text = title,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color.Gray.copy(alpha = 0.8f)
                    )
                }
            }
        }
    }
}

fun formatToHour(t: Int?, locale: Locale = Locale.getDefault()): String {
    return t?.let {
        val sdf = SimpleDateFormat("ha", locale)
        sdf.format(Date(it.toLong() * 1000))
    } ?: ""
}

fun formatTime(t: Int?, locale: Locale): String {
    return t?.let {
        SimpleDateFormat("hh:mm a", locale).format(Date(it.toLong() * 1000))
    } ?: ""
}