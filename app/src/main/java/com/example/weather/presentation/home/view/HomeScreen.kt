package com.example.weather.presentation.home.view

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.weather.R
import com.example.weather.data.models.forecast.ForecastResponse
import com.example.weather.data.models.forecast.ListItem
import com.example.weather.data.models.weather.WeatherResponse
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.random.Random

// ألوان الدلع
val SoftPink = Color(0xFFFDEFF9)
val SoftBlue = Color(0xFFECF2FF)
val AzureBlue = Color(0xFF3F51B5)
val GlassWhite = Color(0xFFFFFFFF).copy(alpha = 0.9f)

@Composable
fun WeatherScreen(weatherData: WeatherResponse, forecastData: ForecastResponse) {
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

            HourlyForecastSection(forecastData.list ?: emptyList())

            Spacer(modifier = Modifier.height(30.dp))

            WeatherDetailGrid(weatherData)

            Spacer(modifier = Modifier.height(30.dp))

            SunPhaseSection(weatherData)

            Spacer(modifier = Modifier.height(40.dp))
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
            // تشغيل الـ Animation بناءً على الحالة
            when {
                condition.contains("Rain", true) -> RainAnimation()
                condition.contains("Snow", true) -> SnowAnimation()
                condition.contains("Cloud", true) -> CloudAnimation()
                condition.contains("Clear", true) -> SunnyAnimation()
                else -> SunnyAnimation()
            }

            // باقي محتويات الكارد (المدينة، الحرارة، إلخ)
            Column(
                modifier = Modifier.fillMaxSize().padding(30.dp),
                verticalArrangement = Arrangement.SpaceBetween,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // (الكود القديم بتاع المدينة والحرارة هنا)
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
fun HourlyForecastSection(hourlyData: List<ListItem?>) {
    Column {
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 10.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                "Today's Schedule",
                fontSize = 20.sp,
                fontWeight = FontWeight.Black,
                color = Color(0xFF2D3142)
            )
            Text(
                "Next 24h",
                fontSize = 12.sp,
                color = AzureBlue,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        LazyRow(
            contentPadding = PaddingValues(horizontal = 4.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(hourlyData.take(12)) { item -> // هناخد أول 12 ساعة بس عشان الزحمة
                val isNow = hourlyData.indexOf(item) == 0 // تمييز أول عنصر كأنه "الآن"

                Surface(
                    modifier = Modifier
                        .width(75.dp)
                        .height(140.dp)
                        .shadow(
                            elevation = if (isNow) 15.dp else 4.dp,
                            shape = RoundedCornerShape(35.dp),
                            spotColor = if (isNow) AzureBlue else Color.Black.copy(0.1f)
                        ),
                    shape = RoundedCornerShape(35.dp),
                    color = if (isNow) AzureBlue else Color.White
                ) {
                    Column(
                        modifier = Modifier.fillMaxSize().padding(vertical = 16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.SpaceBetween
                    ) {
                        // الوقت
                        Text(
                            text = if (isNow) "Now" else formatToHour(item?.dt),
                            fontSize = 12.sp,
                            color = if (isNow) Color.White.copy(0.8f) else Color.Gray,
                            fontWeight = FontWeight.Medium
                        )

                        // الأيقونة
                        AsyncImage(
                            model = "https://openweathermap.org/img/wn/${item?.weather?.get(0)?.icon}@2x.png",
                            contentDescription = null,
                            modifier = Modifier.size(45.dp)
                        )

                        // درجة الحرارة
                        Text(
                            text = "${item?.main?.temp?.toInt()}°",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Black,
                            color = if (isNow) Color.White else Color(0xFF2D3142)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun WeatherDetailGrid(weather: WeatherResponse) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // كارت الرياح - واخد تاتش أزرق
        DetailBox(
            modifier = Modifier.weight(1f),
            title = "Wind Speed",
            value = "${weather.wind?.speed}",
            unit = "km/h",
            icon = R.drawable.ic_wind,
            iconColor = Color(0xFF4FACFE),
            gradient = listOf(Color(0xFFE0F2F1), Color(0xFFFFFFFF))
        )
        // كارت الرطوبة - واخد تاتش لبني/بنفسجي
        DetailBox(
            modifier = Modifier.weight(1f),
            title = "Humidity",
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
            .height(140.dp) // زودنا الطول عشان التفاصيل تبان
            .shadow(15.dp, RoundedCornerShape(35.dp), ambientColor = iconColor.copy(alpha = 0.3f)),
        shape = RoundedCornerShape(35.dp),
        color = Color.White
    ) {
        // خلفية ملونة خفيفة جداً داخل الكارت
        Box(modifier = Modifier.background(Brush.verticalGradient(gradient)).fillMaxSize()) {
            Column(
                modifier = Modifier.padding(20.dp).fillMaxSize(),
                verticalArrangement = Arrangement.SpaceBetween,
                horizontalAlignment = Alignment.Start // المحاذاة للشمال أشيك في الكروت الصغيرة
            ) {
                // الأيقونة ملونة وشيك
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
        modifier = Modifier.fillMaxWidth().height(100.dp),
        shape = RoundedCornerShape(35.dp),
        color = Color.White
    ) {
        Row(modifier = Modifier.fillMaxSize(), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceAround) {
            SunInfoItem("Sunrise", formatTime(weather.sys?.sunrise), R.drawable.ic_sunrise)
            Box(Modifier.width(1.dp).height(40.dp).background(SoftBlue))
            SunInfoItem("Sunset", formatTime(weather.sys?.sunset), R.drawable.ic_sunset)
        }
    }
}

@Composable
fun SunInfoItem(title: String, time: String, icon: Int) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(painterResource(icon), null, modifier = Modifier.size(30.dp), tint = Color(0xFFFFB74D))
        Spacer(Modifier.width(10.dp))
        Column {
            Text(title, fontSize = 11.sp, color = Color.Gray)
            Text(time, fontWeight = FontWeight.Bold)
        }
    }
}

fun formatDate(t: Int?): String = t?.let { SimpleDateFormat("EEEE, d MMMM", Locale.ENGLISH).format(Date(it.toLong() * 1000)) } ?: ""
fun formatToHour(t: Int?): String = t?.let { SimpleDateFormat("ha", Locale.ENGLISH).format(Date(it.toLong() * 1000)) } ?: ""
fun formatTime(t: Int?): String = t?.let { SimpleDateFormat("hh:mm a", Locale.ENGLISH).format(Date(it.toLong() * 1000)) } ?: ""

@Composable
fun getTemperatureColors(temp: Int): List<Color> {
    return when {
        temp < 15 -> listOf(Color(0xFF61A3CC), Color(0xFFA6C1EE)) // أزرق رمادي ثلجي (Cold)
        temp in 15..25 -> listOf(Color(0xFF72C2D1), Color(0xFFC3E5AE)) // فيروزي هادي (Pleasant)
        else -> listOf(Color(0xFFE89E90), Color(0xFFF8D29D)) // مرجاني دافئ مطفي (Hot)
    }
}


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

// الحل الأول: استخدام الـ ArrayList الصريحة
    val rainDrops = remember {
        ArrayList<Triple<Float, Float, Float>>().apply {
            repeat(70) {
                add(
                    Triple(
                        Random.nextInt(0, 1000).toFloat(), // مكان X عشوائي
                        Random.nextInt(0, 1000).toFloat(), // إزاحة Y عشوائية
                        Random.nextFloat() * (1.5f - 0.5f) + 0.5f // سرعة عشوائية بين 0.5 و 1.5
                    )
                )
            }
        }
    }

    Canvas(modifier = Modifier.fillMaxSize().alpha(0.45f)) {
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
    val offsetX by transition.animateFloat(
        initialValue = -100f,
        targetValue = 100f,
        animationSpec = infiniteRepeatable(
            animation = tween(5000, easing = LinearOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ), label = ""
    )

    Box(modifier = Modifier.fillMaxSize().alpha(0.2f)) {
        Box(
            modifier = Modifier
                .size(200.dp)
                .offset(x = offsetX.dp, y = 20.dp)
                .background(Color.White, CircleShape)
                .blur(60.dp)
        )
        Box(
            modifier = Modifier
                .size(150.dp)
                .align(Alignment.BottomEnd)
                .offset(x = (-offsetX).dp, y = (-20).dp)
                .background(Color.White, CircleShape)
                .blur(50.dp)
        )
    }
}

@Composable
fun SunnyAnimation() {
    val infiniteTransition = rememberInfiniteTransition(label = "sun")
    val scale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.2f,
        animationSpec = infiniteRepeatable(
            animation = tween(3000, easing = EaseInOutSine),
            repeatMode = RepeatMode.Reverse
        ), label = "sun_glow"
    )

    Box(modifier = Modifier.fillMaxSize()) {
        // دائرة نور في الركن فوق يمين
        Box(
            modifier = Modifier
                .size(200.dp)
                .align(Alignment.TopEnd)
                .offset(x = 40.dp, y = (-40).dp)
                .graphicsLayer(scaleX = scale, scaleY = scale) // النور بيكبر ويصغر
                .background(
                    Brush.radialGradient(
                        listOf(Color.White.copy(alpha = 0.4f), Color.Transparent)
                    ),
                    CircleShape
                )
                .blur(40.dp)
        )
    }
}


@Composable
fun SnowAnimation() {
    val infiniteTransition = rememberInfiniteTransition(label = "snow")

    // حركة النزول (Vertical)
    val snowY by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1000f,
        animationSpec = infiniteRepeatable(
            animation = tween(5000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ), label = "snow_fall"
    )

    // حركة التمايل (Horizontal)
    val wobble by infiniteTransition.animateFloat(
        initialValue = -20f,
        targetValue = 20f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = EaseInOutSine),
            repeatMode = RepeatMode.Reverse
        ), label = "snow_wobble"
    )

    Canvas(modifier = Modifier.fillMaxSize().alpha(0.6f)) {
        for (i in 0..40) {
            val startX = (i * 30f + wobble) % size.width
            val startY = (snowY + (i * 150f)) % size.height

            drawCircle(
                color = Color.White,
                radius = 6f, // كرات ثلج مدورة
                center = androidx.compose.ui.geometry.Offset(startX, startY)
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

    val mockForecast = ForecastResponse(list = mockForecastList)

    WeatherScreen(weatherData = mockWeather, forecastData = mockForecast)
}

@Preview(showBackground = true)
@Composable
fun IndividualDetailPreview() {
    Box(modifier = Modifier.padding(20.dp).background(SoftBlue)) {
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