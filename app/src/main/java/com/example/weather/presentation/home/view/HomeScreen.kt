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
import com.example.weather.data.models.forecast.DailyItem
import com.example.weather.data.models.forecast.ListItem
import com.example.weather.data.models.weather.WeatherResponse
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
    selectedLang: String
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

                SunPhaseSection(weatherData)

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
                    text = day.weather?.getOrNull(0)?.main ?: "Clear",
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
@Composable
fun getTemperatureColors(temp: Int): List<Color> {
    return when {
        temp <= 5 -> listOf(Color(0xFF1A237E), Color(0xFF5C6BC0))
        temp in 6..15 -> listOf(Color(0xFF61A3CC), Color(0xFFA6C1EE))
        temp in 16..27 -> listOf(Color(0xFFF6D365), Color(0xFFFDA085))
        else -> listOf(Color(0xFFFF5F6D), Color(0xFFFFC371))
    }
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

@Composable
fun SunPhaseSection(weather: WeatherResponse) {

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .height(110.dp)
            .shadow(15.dp, RoundedCornerShape(35.dp), ambientColor = Color.Gray.copy(alpha = 0.2f)),
        shape = RoundedCornerShape(35.dp),
        color = Color.White.copy(alpha = 0.9f)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            SunInfoItem(
                title = "Sunrise",
                time = formatTime(weather.sys?.sunrise),
                icon = R.drawable.ic_sunrise,
                accentColor = Color(0xFFFFB74D)
            )

            Box(
                modifier = Modifier
                    .width(1.5.dp)
                    .height(50.dp)
                    .clip(CircleShape)
                    .background(
                        Brush.verticalGradient(
                            listOf(Color.Transparent, SoftBlue, Color.Transparent)
                        )
                    )
            )

            SunInfoItem(
                title = "Sunset",
                time = formatTime(weather.sys?.sunset),
                icon = R.drawable.ic_sunset,
                accentColor = Color(0xFFFF7043)
            )
        }
    }
}

@Composable
fun SunInfoItem(title: String, time: String, icon: Int, accentColor: Color) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(8.dp)
    ) {
        Box(
            modifier = Modifier
                .size(45.dp)
                .background(accentColor.copy(alpha = 0.15f), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                painter = painterResource(icon),
                contentDescription = null,
                modifier = Modifier.size(28.dp),
                tint = accentColor
            )
        }

        Spacer(Modifier.width(12.dp))

        Column {
            Text(
                text = title,
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium,
                color = Color.Gray
            )
            Text(
                text = time,
                fontSize = 16.sp,
                fontWeight = FontWeight.Black,
                color = Color(0xFF2D3142)
            )
        }
    }
}

fun formatDate(t: Int?): String = t?.let { SimpleDateFormat("EEEE, d MMMM", Locale.ENGLISH).format(Date(it.toLong() * 1000)) } ?: ""
fun formatToHour(t: Int?, locale: Locale = Locale.getDefault()): String {
    return t?.let {
        val sdf = SimpleDateFormat("ha", locale)
        sdf.format(Date(it.toLong() * 1000))
    } ?: ""
}
fun formatTime(t: Int?): String = t?.let { SimpleDateFormat("hh:mm a", Locale.ENGLISH).format(Date(it.toLong() * 1000)) } ?: ""




@Composable
fun RainAnimation() {
    val infiniteTransition = rememberInfiniteTransition(label = "random_rain")

    val progress by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ), label = "rain_progress"
    )

    val rainDrops = remember {
        ArrayList<Triple<Float, Float, Float>>().apply {
            repeat(70) {
                add(
                    Triple(
                        Random.nextInt(0, 1000).toFloat(),
                        Random.nextInt(0, 1000).toFloat(),
                        Random.nextFloat() * (1.5f - 0.5f) + 0.5f
                    )
                )
            }
        }
    }

    Canvas(modifier = Modifier
        .fillMaxSize()
        .alpha(0.45f)) {
        rainDrops.forEach { (xRatio, yOffsetRatio, speedMultiplier) ->
            val x = (xRatio / 1000f) * size.width
            val currentProgress = (progress * speedMultiplier + yOffsetRatio / 1000f) % 1f
            val y = currentProgress * size.height

            drawLine(
                color = Color.White,
                start = Offset(x, y),
                end = Offset(x, y + 45f),
                strokeWidth = 5f,
                cap = StrokeCap.Round
            )
        }
    }
}
@Composable
fun CloudAnimation() {
    val transition = rememberInfiniteTransition(label = "clouds")

    val fastOffset by transition.animateFloat(
        initialValue = -200f,
        targetValue = 600f,
        animationSpec = infiniteRepeatable(
            animation = tween(18000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ), label = "fastCloud"
    )

    val cloudPulse by transition.animateFloat(
        initialValue = 0.9f,
        targetValue = 1.1f,
        animationSpec = infiniteRepeatable(
            animation = tween(4000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ), label = "pulse"
    )

    Box(modifier = Modifier.fillMaxSize()) {

        CloudShapePro(
            size = 280.dp,
            modifier = Modifier
                .align(Alignment.CenterStart)
                .offset(x = fastOffset.dp, y = (-120).dp)
                .scale(cloudPulse)
                .alpha(0.7f) // وضوح عالي
        )

        CloudShapePro(
            size = 180.dp,
            modifier = Modifier
                .align(Alignment.TopEnd)
                .offset(x = (-fastOffset / 2).dp, y = 80.dp)
                .scale(cloudPulse * 0.8f)
                .alpha(0.5f)
        )
    }
}

@Composable
fun CloudShapePro(size: Dp, modifier: Modifier = Modifier) {
    Box(modifier = modifier.size(size), contentAlignment = Alignment.Center) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.radialGradient(
                        colors = listOf(Color.White.copy(alpha = 0.6f), Color.Transparent)
                    ), CircleShape
                )
                .blur(40.dp)
        )
        Box(
            modifier = Modifier
                .fillMaxSize(0.6f)
                .background(
                    Brush.radialGradient(
                        colors = listOf(Color.White.copy(alpha = 0.9f), Color.Transparent)
                    ), CircleShape
                )
                .blur(20.dp)
        )
    }
}
@Composable
fun SunnyAnimation(temp: Int) {
    val infiniteTransition = rememberInfiniteTransition(label = "pro_sun")

    val isExtremelyHot = temp > 28
    val rotationDuration = if (isExtremelyHot) 10000 else 20000
    val glowIntensity = if (isExtremelyHot) 0.8f else 0.4f

    val rotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(rotationDuration, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ), label = ""
    )

    val scale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = if (isExtremelyHot) 1.5f else 1.2f,
        animationSpec = infiniteRepeatable(
            animation = tween(if (isExtremelyHot) 2000 else 4000, easing = EaseInOutSine),
            repeatMode = RepeatMode.Reverse
        ), label = ""
    )

    Box(modifier = Modifier.fillMaxSize()) {
        Canvas(
            modifier = Modifier
                .size(if (isExtremelyHot) 500.dp else 400.dp)
                .align(Alignment.TopEnd)
                .offset(x = 100.dp, y = (-100).dp)
                .graphicsLayer(rotationZ = rotation, alpha = if (isExtremelyHot) 0.3f else 0.15f)
        ) {
            val rayCount = if (isExtremelyHot) 12 else 8
            val angleStep = 360f / rayCount
            for (i in 0 until rayCount) {
                drawArc(
                    color = Color.White,
                    startAngle = i * angleStep,
                    sweepAngle = if (isExtremelyHot) 20f else 15f,
                    useCenter = true,
                    size = size
                )
            }
        }

        Box(
            modifier = Modifier
                .size(if (isExtremelyHot) 300.dp else 250.dp)
                .align(Alignment.TopEnd)
                .offset(x = 50.dp, y = (-50).dp)
                .graphicsLayer(scaleX = scale, scaleY = scale)
                .background(
                    Brush.radialGradient(
                        0.0f to Color.White.copy(alpha = glowIntensity),
                        0.6f to (if (isExtremelyHot) Color(0xFFFF7043) else Color(0xFFFFE082)).copy(
                            alpha = 0.3f
                        ),
                        1.0f to Color.Transparent
                    ),
                    CircleShape
                )
                .blur(if (isExtremelyHot) 80.dp else 60.dp)
        )
    }
}


@Composable
fun SnowAnimation() {
    val infiniteTransition = rememberInfiniteTransition(label = "random_snow")

    val progress by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(8000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ), label = "snow_progress"
    )

    val wobble by infiniteTransition.animateFloat(
        initialValue = -25f,
        targetValue = 15f,
        animationSpec = infiniteRepeatable(
            animation = tween(2500, easing = EaseInOutSine),
            repeatMode = RepeatMode.Reverse
        ), label = "snow_wobble"
    )

    val snowFlakes = remember {
        ArrayList<List<Float>>().apply {
            repeat(50) {
                add(
                    listOf(
                        Random.nextFloat(),
                        Random.nextFloat(),
                        Random.nextFloat() * 0.3f + 0.2f,
                        Random.nextFloat() * 7f + 5f
                    )
                )
            }
        }
    }

    Canvas(modifier = Modifier
        .fillMaxSize()
        .alpha(0.8f)) {
        val width = size.width
        val height = size.height

        snowFlakes.forEach { flake ->
            val x = (flake[0] * width) + wobble
            val y = ((progress * flake[2] + flake[1]) % 1f) * height

            drawCircle(
                color = Color.White,
                radius = flake[3],
                center = Offset(x, y)
            )
        }
    }
}



@Preview(showBackground = true, device = "spec:width=411dp,height=891dp", showSystemUi = true)
@Composable
fun LuxuryWeatherPreview() {
    val mockWeather = WeatherResponse(
        name = "Dubai",
        main = com.example.weather.data.models.weather.Main(
            temp = 24.0,
            feelsLike = 26.0,
            humidity = 35,
            pressure = 1012
        ),
        weather = listOf(com.example.weather.data.models.weather.WeatherItem(icon = "01d", description = "Sunny Day")),
        wind = com.example.weather.data.models.weather.Wind(speed = 5.4),
        sys = com.example.weather.data.models.weather.Sys(country = "UAE", sunrise = 1708572000, sunset = 1708615200),
        dt = 1708593600
    )

    val mockForecastList = List(10) { index ->
        ListItem(
            dt = 1708593600 + (index * 3600),
            main = com.example.weather.data.models.weather.Main(temp = 22.0 + index),
            weather = listOf(com.example.weather.data.models.weather.WeatherItem(icon = "01d"))
        )
    }


}

@Preview(showBackground = true)
@Composable
fun IndividualDetailPreview() {
    Box(modifier = Modifier
        .padding(20.dp)
        .background(SoftBlue)) {
        DetailBox(
            modifier = Modifier.width(160.dp),
            title = "Wind Speed",
            value = "12.5",
            unit = "km/h",
            icon = R.drawable.ic_wind,
            iconColor = Color(0xFF4FACFE),
            gradient = listOf(Color(0xFFE0F2F1), Color(0xFFFFFFFF))
        )
    }
}


val SineWaveEasing = Easing { fraction ->

    sin(fraction * Math.PI).toFloat()
}