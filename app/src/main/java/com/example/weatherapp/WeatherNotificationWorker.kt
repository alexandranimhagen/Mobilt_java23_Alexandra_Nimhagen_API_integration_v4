package com.example.weatherapp

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.work.Worker
import androidx.work.WorkerParameters
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL

class WeatherNotificationWorker(ctx: Context, params: WorkerParameters) : Worker(ctx, params) {

    override fun doWork(): Result {
        // Hämta api_key och city från inputData
        val apiKey = inputData.getString("api_key") ?: "YOUR_API_KEY"
        val city = inputData.getString("city") ?: "dhaka,bd"

        // Hämta väderdata
        return try {
            val response = fetchWeatherData(apiKey, city)

            // Kontrollera om det börjar regna
            if (isItRaining(response)) {
                // Logik för att visa notifikation
                Log.d("WeatherNotification", "Det börjar regna!")
                showNotification("Det börjar regna!", "Kolla vädret i appen för mer information.")
            }

            Result.success() // Returnera resultatet
        } catch (e: Exception) {
            Log.e("WeatherNotification", "Fel vid hämtning av väderdata", e)
            Result.failure() // Returnera fel om det inträffar ett undantag
        }
    }

    private fun fetchWeatherData(apiKey: String, city: String): String {
        val url = "https://api.openweathermap.org/data/2.5/weather?q=$city&appid=$apiKey"
        val connection = URL(url).openConnection() as HttpURLConnection
        connection.requestMethod = "GET"

        return connection.inputStream.bufferedReader().readText()
    }

    private fun isItRaining(response: String): Boolean {
        val jsonObj = JSONObject(response)
        val weatherArray = jsonObj.getJSONArray("weather")
        for (i in 0 until weatherArray.length()) {
            val weather = weatherArray.getJSONObject(i)
            if (weather.getString("main").equals("Rain", ignoreCase = true)) {
                return true // Returnera true om det finns regn
            }
        }
        return false // Returnera false om det inte regnar
    }

    private fun showNotification(title: String, message: String) {
        val notificationManager = applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val channelId = "weather_channel"
        val channelName = "Weather Notifications"


        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            val channel = NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_HIGH)
            notificationManager.createNotificationChannel(channel)
        }

        // Skapa en notifikation
        val notification = NotificationCompat.Builder(applicationContext, channelId)
            .setSmallIcon(R.drawable.ic_launcher_background)
            .setContentTitle(title)
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)

        // Visa notifikationen
        notificationManager.notify(1, notification.build())
    }
}
