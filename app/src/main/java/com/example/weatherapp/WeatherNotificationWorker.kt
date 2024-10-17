package com.example.weatherapp

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.HttpException

class WeatherNotificationWorker(ctx: Context, params: WorkerParameters) : CoroutineWorker(ctx, params) {

    override suspend fun doWork(): Result = withContext(Dispatchers.IO) {
        // Hämta api_key och city från SharedPreferences
        val sharedPreferences = applicationContext.getSharedPreferences("WeatherAppPreferences", Context.MODE_PRIVATE)
        val city = sharedPreferences.getString("city", "MALMÖ, SE") ?: "MALMÖ, SE"
        val apiKey = BuildConfig.API_KEY

        try {
            val response = fetchWeatherData(apiKey, city)

            if (response != null && isItRaining(response)) {
                showNotification("Det börjar regna!", "Kolla vädret i appen för mer information.")
            }

            Result.success()
        } catch (e: HttpException) {
            Log.e("WeatherNotification", "Error fetching weather data", e)
            Result.failure()
        } catch (e: Exception) {
            Log.e("WeatherNotification", "Unexpected error", e)
            Result.failure()
        }
    }

    // Hämta väderdata med Retrofit
    private suspend fun fetchWeatherData(apiKey: String, city: String): WeatherResponse? {
        return try {
            val weatherApiService = RetrofitInstance.api
            weatherApiService.getWeather(city, apiKey, "metric")
        } catch (e: Exception) {
            Log.e("WeatherNotification", "Error fetching weather data", e)
            null
        }
    }

    // Kontrollera om det regnar
    private fun isItRaining(response: WeatherResponse): Boolean {
        return response.weather.any { it.main.equals("Rain", ignoreCase = true) }
    }

    // Visa notifikation
    private fun showNotification(title: String, message: String) {
        val notificationManager = applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val channelId = "weather_channel"
        val channelName = "Weather Notifications"

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            val channel = NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_HIGH)
            notificationManager.createNotificationChannel(channel)
        }

        // Skapa en notifikation med väderikonen
        val notification = NotificationCompat.Builder(applicationContext, channelId)
            .setSmallIcon(R.mipmap.ic_weather_icon)
            .setContentTitle(title)
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)

        notificationManager.notify(1, notification.build())
    }
}
