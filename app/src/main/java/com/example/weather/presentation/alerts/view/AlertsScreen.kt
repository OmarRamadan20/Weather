package com.example.weather.presentation.alerts.view

import android.Manifest
import android.app.AlarmManager
import android.content.Intent
import android.content.res.Configuration
import android.net.Uri
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.weather.presentation.alerts.view.compontents.ActiveAlertItem
import com.example.weather.presentation.alerts.view.compontents.AlertConfigurationContent
import com.example.weather.presentation.alerts.view.compontents.EmptyAlertsView
import com.example.weather.presentation.alerts.viewmodel.AlertsViewModel
import android.provider.Settings
import com.example.weather.R
import androidx.annotation.RequiresApi
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import java.util.Locale

@RequiresApi(Build.VERSION_CODES.TIRAMISU)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AlertsScreen(alertsViewModel: AlertsViewModel,selectedLang: String) {
    rememberModalBottomSheetState()

    val context = LocalContext.current
    val currentLocale = if (selectedLang.contains("ar", ignoreCase = true))
        Locale("ar") else Locale("en")

    val configuration = Configuration(context.resources.configuration)
    configuration.setLocale(currentLocale)

    val localizedContext = context.createConfigurationContext(configuration)


    var showBottomSheet by remember { mutableStateOf(false) }

    val alertsList by alertsViewModel.allAlerts.collectAsState()
    val currentWindUnit by alertsViewModel.windUnit.collectAsState("metric")
    val activeAlerts = alertsList.filter { it.isEnabled }
    val inactiveAlerts = alertsList.filter { !it.isEnabled }
    val currentTempUnit by alertsViewModel.tempUnit.collectAsState()

    val launcher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) {}

    LaunchedEffect(Unit) {
        val permissions = mutableListOf<String>()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            permissions.add(Manifest.permission.POST_NOTIFICATIONS)
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val alarmManager = context.getSystemService(AlarmManager::class.java)
            if (!alarmManager.canScheduleExactAlarms()) {
                val intent = Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM).apply {
                    data = Uri.fromParts("package", context.packageName, null)
                }
                context.startActivity(intent)
            }
        }

        if (permissions.isNotEmpty()) {
            launcher.launch(permissions.toTypedArray())
        }
    }
    CompositionLocalProvider(LocalContext provides localizedContext,
        LocalLayoutDirection provides if (currentLocale.language == "ar") LayoutDirection.Rtl else LayoutDirection.Ltr) {

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showBottomSheet = true },
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary,
                shape = CircleShape,
                modifier = Modifier.padding(bottom = 70.dp, end = 10.dp)
            ) {
                Icon(Icons.Default.Add, contentDescription = stringResource(R.string.add_alert))
            }
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .padding(horizontal = 20.dp)
        ) {
            Spacer(modifier = Modifier.height(24.dp))
            Text(
                text = stringResource(R.string.weather_alerts),
                style = TextStyle(
                    fontSize = 28.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = MaterialTheme.colorScheme.onBackground
                )
            )
            Spacer(modifier = Modifier.height(30.dp))

            if (alertsList.isEmpty()) {
                EmptyAlertsView()
            } else {
                LazyColumn {
                    if (activeAlerts.isNotEmpty()) {
                        item {
                            Text(
                                text = stringResource(R.string.active_alerts),
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.padding(vertical = 8.dp)
                            )
                        }
                        items(activeAlerts) { alert ->
                            ActiveAlertItem(
                                title = alert.triggerType,
                                subTitle = stringResource(
                                    R.string.threshold_value,
                                    alert.thresholdValue,
                                    alertsViewModel.getUnitForTrigger(
                                        alert.triggerType,
                                        currentTempUnit,
                                        currentWindUnit
                                    )
                                ),
                                startDate = alert.startDate,
                                endDate = alert.endDate,
                                iconRes = when (alert.triggerType) {
                                    "Temp" -> R.drawable.ic_temp
                                    "Wind" -> R.drawable.ic_wind
                                    "Rain" -> R.drawable.ic_rain
                                    else -> R.drawable.ic_notification
                                },
                                isChecked = alert.isEnabled,
                                onCheckedChange = { isChecked ->
                                    alertsViewModel.updateAlertStatus(alert.id, isChecked)
                                },
                                onDelete = {
                                    alertsViewModel.removeAlert(alert)
                                }
                            )
                        }
                    }

                    if (inactiveAlerts.isNotEmpty()) {
                        item {
                            Text(
                                text = stringResource(R.string.inactive_alerts),
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.padding(top = 16.dp, bottom = 8.dp)
                            )
                        }
                        items(inactiveAlerts) { alert ->
                            ActiveAlertItem(
                                title = alert.triggerType,
                                subTitle = stringResource(
                                    id = R.string.threshold_value,
                                    alert.thresholdValue,
                                    alertsViewModel.getUnitForTrigger(
                                        alert.triggerType,
                                        currentTempUnit,
                                        currentWindUnit
                                    )
                                ),
                                startDate = alert.startDate,
                                endDate = alert.endDate,
                                iconRes = when (alert.triggerType) {
                                    "Rain" -> R.drawable.ic_notification
                                    "Wind" -> R.drawable.ic_wind
                                    else -> R.drawable.ic_notification
                                },
                                isChecked = alert.isEnabled,
                                onCheckedChange = { isChecked ->
                                    alertsViewModel.updateAlertStatus(alert.id, isChecked)
                                },
                                onDelete = { alertsViewModel.removeAlert(alert) }
                            )
                        }
                    }
                }
            }
        }

        if (showBottomSheet) {
            ModalBottomSheet(onDismissRequest = { showBottomSheet = false }) {
                AlertConfigurationContent(selectedLang = selectedLang , onSave = { newAlert ->
                    alertsViewModel.addAlert(newAlert)
                    showBottomSheet = false
                })
            }
        }
    }
    }
}