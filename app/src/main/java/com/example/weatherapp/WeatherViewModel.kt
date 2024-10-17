package com.example.weatherapp

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import retrofit2.HttpException

class WeatherViewModel : ViewModel() {

    // Hämta väderdata
    fun fetchWeather(city: String, apiKey: String, onSuccess: (WeatherResponse?) -> Unit, onError: (Throwable?) -> Unit) {
        val units = "metric"

        // Kör i en coroutine med viewModelScope
        viewModelScope.launch {
            try {
                // Anropa den suspend-funktion i en coroutine
                val response = RetrofitInstance.api.getWeather(city, apiKey, units)
                Log.d("WeatherViewModel", "API call successful: $response")
                onSuccess(response)
            } catch (e: HttpException) {
                Log.e("WeatherViewModel", "API call failed: ${e.message()}")
                onError(e)
            } catch (e: Exception) {
                Log.e("WeatherViewModel", "Unexpected error: ${e.localizedMessage}")
                onError(e)
            }
        }
    }
}
