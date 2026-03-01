package com.example.weather.presentation.alerts.view.compontents

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.res.stringResource
import androidx.compose.material3.MaterialTheme
import com.example.weather.R


@Composable
fun TriggerSelector(
    selectedTrigger: String,
    onTriggerSelected: (String) -> Unit
) {
    val triggers = listOf(
        "Rain" to R.drawable.ic_rain,
        "Wind" to R.drawable.ic_wind,
        "Temp" to R.drawable.ic_temp,
        "Storm" to R.drawable.ic_humidity
    )

    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        triggers.forEach { (name, icon) ->
            val isSelected = selectedTrigger == name

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.weight(1f).clickable { onTriggerSelected(name) }
            ) {
                Surface(
                    modifier = Modifier
                        .size(54.dp)
                        .border(
                            width = 1.dp,
                            color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outlineVariant,
                            shape = RoundedCornerShape(14.dp)
                        ),
                    shape = RoundedCornerShape(14.dp),

                    color = if (isSelected) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f) else MaterialTheme.colorScheme.surface
                ) {
                    Icon(
                        painter = painterResource(id = icon),
                        contentDescription = null,
                        modifier = Modifier.padding(14.dp),
                        tint = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = when(name) {
                        "Rain" -> stringResource(R.string.rain)
                        "Wind" -> stringResource(R.string.wind)
                        "Temp" -> stringResource(R.string.temp)
                        "Storm" -> stringResource(R.string.storm)
                        else -> name
                    },
                    fontSize = 11.sp,
                    color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}