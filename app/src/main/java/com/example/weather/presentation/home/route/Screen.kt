package com.example.weather.presentation.home.route

sealed class Screen(val route: String) {
    object Home : Screen("home")
    object Favourite : Screen("fav")
    object Settings : Screen("settings")
    object Alerts : Screen("alerts")
}