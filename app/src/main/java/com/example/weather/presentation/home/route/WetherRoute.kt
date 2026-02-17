package com.example.weather.presentation.home.route

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.example.weather.data.datasources.remote.network.MyResult
import com.example.weather.data.models.weather.WeatherResponse
import com.example.weather.presentation.home.view.WeatherScreen
import com.example.weather.presentation.home.viewmodel.HomeViewModel

