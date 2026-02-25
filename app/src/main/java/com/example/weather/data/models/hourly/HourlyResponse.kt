package com.example.weather.data.models.hourly

data class HourlyResponse(
	val city: City? = null,
	val cnt: Double? = null,
	val cod: String? = null,
	val message: Double? = null,
	val list: List<ListItem?>? = null
)

data class Clouds(
	val all: Double? = null
)

data class WeatherItem(
	val icon: String? = null,
	val description: String? = null,
	val main: String? = null,
	val id: Double? = null
)

data class ListItem(
	val dt: Double? = null,
	val pop: Double? = null,
	val visibility: Double? = null,
	val dtTxt: String? = null,
	val weather: List<WeatherItem?>? = null,
	val main: Main? = null,
	val clouds: Clouds? = null,
	val sys: Sys? = null,
	val wind: Wind? = null,
	val rain: Rain? = null
)

data class Main(
	val temp: Double? = null,
	val tempMin: Double? = null,
	val grndLevel: Double? = null,
	val tempKf: Double? = null,
	val humidity: Double? = null,
	val pressure: Double? = null,
	val seaLevel: Double? = null,
	val feelsLike: Double? = null,
	val tempMax: Double? = null
)

data class Rain(
	val jsonMember1h: Double? = null
)

data class Sys(
	val pod: String? = null
)

data class City(
	val country: String? = null,
	val coord: Coord? = null,
	val sunrise: Double? = null,
	val timezone: Double? = null,
	val sunset: Double? = null,
	val name: String? = null,
	val id: Double? = null,
	val population: Double? = null
)

data class Coord(
	val lon: Double? = null,
	val lat: Double? = null
)

data class Wind(
	val deg: Double? = null,
	val speed: Double? = null,
	val gust: Double? = null
)

