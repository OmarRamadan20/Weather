package com.example.weather.presentation.home.view.components

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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.weather.R
import com.example.weather.data.models.weather.WeatherResponse
import com.example.weather.presentation.home.view.SoftBlue
import com.example.weather.presentation.home.view.formatTime
import java.util.Locale


@Composable
fun SunPhaseSection(weather: WeatherResponse , locale: Locale) {

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
                title = stringResource(R.string.sunrise2, formatTime(weather.sys?.sunrise, locale)),
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
                title = stringResource(R.string.sunset2, formatTime(weather.sys?.sunset, locale)),
                icon = R.drawable.ic_sunset,
                accentColor = Color(0xFFFF7043)
            )
        }
    }
}

@Composable
fun SunInfoItem(title: String, icon: Int, accentColor: Color) {
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
                style = MaterialTheme.typography.bodyMedium,
                color = Color.Gray
            )

        }
    }
}
