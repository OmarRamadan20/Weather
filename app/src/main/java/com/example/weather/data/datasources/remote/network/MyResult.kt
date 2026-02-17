package com.example.weather.data.datasources.remote.network

sealed class MyResult<out T> {
    object Loading : MyResult<Nothing>()
    data class Success<T>(val data: T) : MyResult<T>()
    data class Error(val message: String) : MyResult<Nothing>()
}
