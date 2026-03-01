package com.example.weather.presentation.favourite.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.weather.data.config.db.FavLocation
import com.example.weather.data.config.db.WeatherState
import com.example.weather.utils.MyResult
import com.example.weather.data.models.map.CityResponseItem
import com.example.weather.data.repo.WeatherRepository
import com.example.weather.utils.NetworkObserver
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class FavViewModel(private val repository: WeatherRepository) : ViewModel() {

    private val _citySuggestions = MutableStateFlow<MyResult<List<CityResponseItem>>>(MyResult.Success(emptyList()))
    val citySuggestions = _citySuggestions.asStateFlow()


    private val _errorFlow = MutableStateFlow<String?>(null)
    val errorFlow = _errorFlow.asStateFlow()

    private val _networkStatus = MutableStateFlow(NetworkObserver.Status.Available)
    val networkStatus = _networkStatus.asStateFlow()

    fun updateNetworkStatus(status: NetworkObserver.Status) {
        _networkStatus.value = status
    }

    fun clearError() { _errorFlow.value = null }

    val favLocations: StateFlow<List<FavLocation>> = repository.getFavourites()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    private val _selectedWeather = MutableStateFlow<WeatherState?>(null)
    val selectedWeather: StateFlow<WeatherState?> = _selectedWeather

    fun fetchWeatherForMapPoint(lat: Double, lon: Double, apiKey: String, units: String, lang: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val result = repository.getCurrentWeather(lat, lon, apiKey, units, lang)

            when (result) {
                is MyResult.Success -> {
                    val data = result.data
                    _selectedWeather.value = WeatherState(
                        cityName = data.name ?: "Unknown",
                        temp = data.main?.temp ?: 0.0,
                        humidity = data.main?.humidity?.toDouble() ?: 0.0,
                        windSpeed = data.wind?.speed ?: 0.0,
                        sunrise = data.sys?.sunrise?.toDouble()?:0.0,
                        sunset = data.sys?.sunset?.toDouble()?:0.0,
                        lat = lat,
                        lon = lon
                    )
                }
                is MyResult.Error -> {
                    _errorFlow.value = result.message ?: "Failed to fetch weather data"

                }

                MyResult.Loading -> {

                }
            }
        }
    }

    fun saveToFavourites(weather: WeatherState) {
        viewModelScope.launch(Dispatchers.IO) {
            val fav = FavLocation(
                name = weather.cityName,
                lat = weather.lat,
                lon = weather.lon,
                temp = weather.temp.toString(),
                humidity = weather.humidity,
                windSpeed = weather.windSpeed,
                sunrise = weather.sunrise,
                sunset = weather.sunset
            )
            repository.addLocationToFav(fav)
        }
    }

    fun deleteFromFav(fav: FavLocation) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.deleteLocationFromFav(fav)
        }
    }


    fun searchCities(query: String) {
        if (query.trim().length < 3) {
            _citySuggestions.value = MyResult.Success(emptyList())
            return
        }

        viewModelScope.launch {
            _citySuggestions.value = MyResult.Loading
            try {
                val results = repository.getCitySuggestions(query= query,8, apiKey = "a50b3547c713e7be1ec57c696006497f")
                _citySuggestions.value = results
            } catch (e: Exception) {
                _citySuggestions.value = MyResult.Error(e.message ?: "Unknown error")
            }
        }
    }

    fun onCitySelected(city: CityResponseItem?) {
        fetchWeatherForMapPoint(city?.lat?: 0.0, city?.lon?:0.0,"a50b3547c713e7be1ec57c696006497f",
            "metric","en")
        _citySuggestions.value = MyResult.Success(emptyList())
    }
}