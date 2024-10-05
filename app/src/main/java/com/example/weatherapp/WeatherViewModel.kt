package com.example.weatherapp

import androidx.lifecycle.ViewModel
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class WeatherViewModel : ViewModel() {

    // Hämta väderdata
    fun fetchWeather(city: String, apiKey: String, onSuccess: (WeatherResponse?) -> Unit, onError: () -> Unit) {
        val units = "metric"
        RetrofitInstance.api.getWeather(city, apiKey, units).enqueue(object : Callback<WeatherResponse> {
            override fun onResponse(call: Call<WeatherResponse>, response: Response<WeatherResponse>) {
                if (response.isSuccessful) {
                    onSuccess(response.body())
                } else {
                    onError()
                }
            }

            override fun onFailure(call: Call<WeatherResponse>, t: Throwable) {
                onError()
            }
        })
    }

}
