package com.example.weather.presentation.home.view

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import com.example.weather.R
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.weather.data.models.forecast.ForecastResponse
import com.example.weather.data.models.forecast.ListItem
import com.example.weather.data.models.weather.Main
import com.example.weather.data.models.weather.Sys
import com.example.weather.data.models.weather.WeatherResponse
import com.example.weather.data.models.weather.Wind

@Composable
fun WeatherScreen(weatherData: WeatherResponse,forecastData: ForecastResponse) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF2F5F8))
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        MainWeatherCard(weatherData)

        Spacer(modifier = Modifier.height(24.dp))

        HourlyWeatherList(hourlyData = forecastData.list?: emptyList())

        Spacer(modifier = Modifier.height(24.dp))

        WeatherDetailGrid(weatherData)

        Spacer(modifier = Modifier.height(24.dp))

        SunAndHumidityRow(weatherData)
    }
}

@Composable
fun MainWeatherCard(weather: WeatherResponse) {
    Card(
        shape = RoundedCornerShape(24.dp),
        modifier = Modifier.fillMaxWidth().height(200.dp)
    ) {
        Box {
            Image(
                painter = painterResource(id = R.drawable.ic_sunset),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )

            Column(
                modifier = Modifier.fillMaxSize().padding(16.dp),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Row(modifier = Modifier.fillMaxWidth()
                    , horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("09-02-2026", color = Color.White)
                    Text("08:00 AM", color = Color.White)
                }

                Column(horizontalAlignment = Alignment.CenterHorizontally
                    , modifier = Modifier.fillMaxWidth()) {
                    Text("${weather.main?.temp?.toInt() ?: 0}"
                        , fontSize = 64.sp
                        , color = Color.White
                        , fontWeight = FontWeight.Bold)
                    Text("(Celsius)", color = Color.White)
                }

                Text("${weather.name}, ${weather.sys?.country}", color = Color.White)
            }
        }
    }
}

@Composable
fun WeatherDetailSection(title: String, icon: Painter, labels: List<String>, values: List<String>) {
    Row(modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)
        , verticalAlignment = Alignment.CenterVertically) {
        Icon(icon, contentDescription = null, modifier = Modifier.size(32.dp))
        Spacer(modifier = Modifier.width(16.dp))
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
            labels.forEachIndexed { index, label ->
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(values[index], fontSize = 20.sp, fontWeight = FontWeight.Bold)
                    Text(label, fontSize = 12.sp, color = Color.Gray)
                }
            }
        }
    }
}


@Composable
fun WeatherDetailGrid(weather: WeatherResponse) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White, RoundedCornerShape(16.dp))
            .padding(16.dp)
    ) {
        WeatherDetailSection(
            title = "Temp",
            icon = painterResource(id = R.drawable.ic_temp),
            labels = listOf("Min", "Temp", "Max"),
            values = listOf(
                "${weather.main?.tempMin?.toInt() ?: 0}°",
                "${weather.main?.temp?.toInt() ?: 0}°",
                "${weather.main?.tempMax?.toInt() ?: 0}°"
            )
        )

        WeatherDetailSection(
            title = "Pressure",
            icon = painterResource(id = R.drawable.ic_pressure),
            labels = listOf("Sea Level", "Feels Like", "Ground"),
            values = listOf(
                "${weather.main?.seaLevel ?: 0}",
                "${weather.main?.feelsLike?.toInt() ?: 0}°",
                "${weather.main?.grndLevel ?: 0}"
            )
        )

        WeatherDetailSection(
            title = "Wind",
            icon = painterResource(id = R.drawable.ic_wind),
            labels = listOf("Speed", "Gust", "Degree"),
            values = listOf(
                "${weather.wind?.speed ?: 0}",
                "${weather.wind?.gust ?: 0}",
                "${weather.wind?.deg ?: 0}°"
            )
        )
    }
}

@Composable
fun HourlyWeatherItem(time: String, temp: String, iconCode: String) {
    Card(
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier
            .width(80.dp)
            .padding(horizontal = 4.dp),
        colors = androidx.compose.material3.CardDefaults.cardColors(
            containerColor = Color.White
        )
    ) {
        Column(
            modifier = Modifier.padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(text = time, fontSize = 12.sp, color = Color.Gray)
            Spacer(modifier = Modifier.height(8.dp))

            AsyncImage(
                model = "https://openweathermap.org/img/wn/$iconCode@2x.png",
                contentDescription = null,
                modifier = Modifier.size(40.dp)
            )

            Spacer(modifier = Modifier.height(8.dp))
            Text(text = "$temp°", fontWeight = FontWeight.Bold, fontSize = 16.sp)
        }
    }
}


@Composable
fun SunAndHumidityRow(weather: WeatherResponse) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp),
        horizontalArrangement = Arrangement.SpaceAround,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(painterResource(id = R.drawable.ic_sunrise)
                , contentDescription = null
                , modifier = Modifier.size(30.dp))
            Text("Sunrise", fontSize = 12.sp)
            Text(formatTime(weather.sys?.sunrise)
                , fontWeight = FontWeight.Bold)
        }

        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text("${weather.main?.humidity}%"
                , fontSize = 28.sp
                , fontWeight = FontWeight.Bold)
            Text("Humidity", fontSize = 12.sp)
        }

        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(painterResource(id = R.drawable.ic_sunset), contentDescription = null, modifier = Modifier.size(30.dp))
            Text("Sunset", fontSize = 12.sp)
            Text(formatTime(weather.dt), fontWeight = FontWeight.Bold)
        }
    }
}

fun formatTime(timestamp: Int?): String {
    if (timestamp == null) return "00:00 AM"
    val sdf = java.text.SimpleDateFormat("hh:mm a", java.util.Locale.ENGLISH)
    val date = java.util.Date(timestamp.toLong() * 1000)
    return sdf.format(date)
}


@androidx.compose.ui.tooling.preview.Preview(showBackground = true, showSystemUi = true)
@Composable
fun WeatherScreenPreview() {
    val mockData = WeatherResponse(
        name = "Zagazig",
        main = Main(
            temp = 22.5,
            tempMin = 18.0,
            tempMax = 26.0,
            pressure = 1012,
            humidity = 55,
            seaLevel = 1012,
            grndLevel = 1000,
            feelsLike = 21.0
        ),
        sys = Sys(
            country = "EG",
            sunrise = 1707452400,
            sunset = 1707495600
        ),
        wind = Wind(
            speed = 5.5,
            deg = 120.0,
            gust = 7.2
        ),
        dt = 1707474000
    )

    WeatherScreen(weatherData = mockData, forecastData = ForecastResponse())
}

@androidx.compose.ui.tooling.preview.Preview(showBackground = true)
@Composable
fun MainCardPreview() {
    val mockData = WeatherResponse(
        name = "Berlin",
        main = Main(temp = 12.0),
        sys = Sys(country = "DE")
    )
    MainWeatherCard(weather = mockData)
}

@Composable
fun HourlyWeatherList(hourlyData: List<ListItem?>) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        Text(
            text = "Hourly Forecast",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(start = 8.dp, bottom = 12.dp),
            color = Color.Black
        )

        androidx.compose.foundation.lazy.LazyRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 4.dp)
        ) {
            items(hourlyData) { hourItem ->
                HourlyWeatherItem(
                    time = formatTime(hourItem?.dt),
                    temp = hourItem?.main?.temp?.toInt().toString(),
                    iconCode = hourItem?.weather?.get(0)?.icon ?: "01d"
                )
            }
        }
    }
}