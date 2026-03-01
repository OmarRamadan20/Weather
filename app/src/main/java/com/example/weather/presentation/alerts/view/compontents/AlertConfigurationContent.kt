package com.example.weather.presentation.alerts.view.compontents

import android.content.res.Configuration
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.weatherapp.data.models.Alerts
import com.example.weatherapp.presentation.alerts.view.components.DeliveryToggle
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import com.example.weather.R


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AlertConfigurationContent(onSave: (Alerts) -> Unit,selectedLang: String) {


    var threshold by remember { mutableStateOf(20f) }
    var selectedDelivery by remember { mutableStateOf("Notification") }
    var selectedTrigger by remember { mutableStateOf("Rain") }

    var showStartDatePicker by remember { mutableStateOf(false) }
    var showStartTimePicker by remember { mutableStateOf(false) }
    var showEndDatePicker by remember { mutableStateOf(false) }
    var showEndTimePicker by remember { mutableStateOf(false) }

    val startCal = remember { mutableStateOf(Calendar.getInstance().apply { add(Calendar.HOUR, 1) }) }
    val endCal = remember { mutableStateOf(Calendar.getInstance().apply { add(Calendar.HOUR, 2) }) }

    val dateTimeFormatter = remember {
        SimpleDateFormat("dd/MM HH:mm", Locale.getDefault())
    }

    val startDatePickerState = rememberDatePickerState(initialSelectedDateMillis = startCal.value.timeInMillis)
    val endDatePickerState = rememberDatePickerState(initialSelectedDateMillis = endCal.value.timeInMillis)

    val startTimeState = rememberTimePickerState(
        initialHour = startCal.value.get(Calendar.HOUR_OF_DAY),
        initialMinute = startCal.value.get(Calendar.MINUTE)
    )
    val endTimeState = rememberTimePickerState(
        initialHour = endCal.value.get(Calendar.HOUR_OF_DAY),
        initialMinute = endCal.value.get(Calendar.MINUTE)
    )

    val unit = when (selectedTrigger) {
        "Rain" -> "mm"
        "Temp" -> "°C"
        "Wind" -> "m/s"
        else -> ""
    }
    val valueRange = when (selectedTrigger) {
        "Temp" -> -20f..50f
        else -> 0f..100f
    }
    val context = LocalContext.current
    val currentLocale = if (selectedLang.contains("ar", ignoreCase = true))
        Locale("ar") else Locale("en")

    val configuration = Configuration(context.resources.configuration)
    configuration.setLocale(currentLocale)

    val localizedContext = context.createConfigurationContext(configuration)

    CompositionLocalProvider(LocalContext provides localizedContext,
        LocalLayoutDirection provides if (currentLocale.language == "ar") LayoutDirection.Rtl else LayoutDirection.Ltr) {


        if (showStartDatePicker) {
            DatePickerDialog(
                onDismissRequest = { showStartDatePicker = false },
                confirmButton = {
                    TextButton(onClick = {
                        startDatePickerState.selectedDateMillis?.let {
                            startCal.value = Calendar.getInstance().apply { timeInMillis = it }
                        }
                        showStartDatePicker = false
                        showStartTimePicker = true
                    }) { Text(stringResource(R.string.next)) }
                },
                dismissButton = {
                    TextButton(onClick = {
                        showStartDatePicker = false
                    }) { Text(stringResource(R.string.cancel)) }
                }
            ) { DatePicker(state = startDatePickerState) }
        }

        if (showStartTimePicker) {
            AlertDialog(
                onDismissRequest = { showStartTimePicker = false },
                confirmButton = {
                    TextButton(onClick = {
                        startCal.value.set(Calendar.HOUR_OF_DAY, startTimeState.hour)
                        startCal.value.set(Calendar.MINUTE, startTimeState.minute)
                        startCal.value.set(Calendar.SECOND, 0)
                        showStartTimePicker = false
                    }) { Text(stringResource(R.string.ok)) }
                },
                dismissButton = {
                    TextButton(onClick = {
                        showStartTimePicker = false
                    }) { Text(stringResource(R.string.cancel)) }
                },
                text = { TimePicker(state = startTimeState) }
            )
        }

        if (showEndDatePicker) {
            DatePickerDialog(
                onDismissRequest = { showEndDatePicker = false },
                confirmButton = {
                    TextButton(onClick = {
                        endDatePickerState.selectedDateMillis?.let {
                            endCal.value = Calendar.getInstance().apply { timeInMillis = it }
                        }
                        showEndDatePicker = false
                        showEndTimePicker = true
                    }) { Text(stringResource(R.string.next)) }
                },
                dismissButton = {
                    TextButton(onClick = {
                        showEndDatePicker = false
                    }) { Text(stringResource(R.string.cancel)) }
                }
            ) { DatePicker(state = endDatePickerState) }
        }

        if (showEndTimePicker) {
            AlertDialog(
                onDismissRequest = { showEndTimePicker = false },
                confirmButton = {
                    TextButton(onClick = {
                        endCal.value.set(Calendar.HOUR_OF_DAY, endTimeState.hour)
                        endCal.value.set(Calendar.MINUTE, endTimeState.minute)
                        endCal.value.set(Calendar.SECOND, 0)
                        showEndTimePicker = false
                    }) { Text(stringResource(R.string.ok)) }
                },
                dismissButton = {
                    TextButton(onClick = {
                        showEndTimePicker = false
                    }) { Text(stringResource(R.string.cancel)) }
                },
                text = { TimePicker(state = endTimeState) }
            )
        }

        Column(
            modifier = Modifier
                .padding(horizontal = 24.dp)
                .padding(bottom = 40.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Text(
                text = stringResource(R.string.configure_new_alert),
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(25.dp))

            TriggerSelector(selectedTrigger) { selectedTrigger = it }
            Spacer(modifier = Modifier.height(25.dp))

            if (selectedTrigger != "Storm") {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = stringResource(R.string.threshold_level),
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "${threshold.toInt()} $unit",
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
                Slider(
                    value = threshold,
                    onValueChange = { threshold = it },
                    valueRange = valueRange,
                    colors = SliderDefaults.colors(
                        thumbColor = MaterialTheme.colorScheme.primary,
                        activeTrackColor = MaterialTheme.colorScheme.primary
                    )
                )
            } else {
                Text(
                    text = stringResource(R.string.triggers_on_storm),
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontSize = 13.sp
                )
            }

            Spacer(modifier = Modifier.height(20.dp))
            DeliveryToggle(selectedDelivery) { selectedDelivery = it }
            Spacer(modifier = Modifier.height(25.dp))

            Text(
                text = stringResource(R.string.active_duration),
                fontSize = 11.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedButton(
                    onClick = { showStartDatePicker = true },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(
                        Icons.Default.DateRange,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "${stringResource(R.string.start)}\n${
                            dateTimeFormatter.format(
                                Date(
                                    startCal.value.timeInMillis
                                )
                            )
                        }", fontSize = 11.sp
                    )
                }
                OutlinedButton(
                    onClick = { showEndDatePicker = true },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(
                        Icons.Default.DateRange,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "${stringResource(R.string.end)}\n${
                            dateTimeFormatter.format(
                                Date(
                                    endCal.value.timeInMillis
                                )
                            )
                        }", fontSize = 11.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(35.dp))

            Button(
                onClick = {
                    if (endCal.value.timeInMillis <= startCal.value.timeInMillis) {
                        return@Button
                    }

                    val newAlert = Alerts(
                        triggerType = selectedTrigger,
                        thresholdValue = threshold.toInt(),
                        deliveryType = selectedDelivery,
                        startDate = startCal.value.timeInMillis,
                        endDate = endCal.value.timeInMillis,
                        isEnabled = true
                    )
                    onSave(newAlert)
                },
                modifier = Modifier.fillMaxWidth().height(56.dp),
                shape = RoundedCornerShape(16.dp)
            ) {
                Text(stringResource(R.string.save_alert))
            }
        }
    }
}