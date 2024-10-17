package com.example.weatherapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupActionBarWithNavController
import com.example.weatherapp.databinding.ActivityMainBinding
import androidx.work.WorkManager
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.workDataOf

class MainActivity : AppCompatActivity() {

    // View Binding-variabel
    private lateinit var binding: ActivityMainBinding

    // ViewModel-variabel
    private val viewModel: WeatherViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialisera View Binding
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Sätt Toolbar som ActionBar
        setSupportActionBar(binding.toolbar)

        // Setup NavHostFragment och NavController
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val navController = navHostFragment.navController

        // Set ActionBar med NavController
        setupActionBarWithNavController(navController)

        // Hämta sparad stad från SharedPreferences
        val sharedPreferences = getSharedPreferences("WeatherAppPreferences", MODE_PRIVATE)
        val city = sharedPreferences.getString("city", "MALMÖ, SE") ?: "MALMÖ, SE"
        val apiKey = BuildConfig.API_KEY

        // Starta väderhämtning
        binding.loader.visibility = View.VISIBLE
        viewModel.fetchWeather(city, apiKey, onSuccess = { response ->
            if (response != null) {
                populateUI(response)
            } else {
                showError()
            }
        }, onError = {
            showError()
        })

        // Registrera Worker
        val workRequest = OneTimeWorkRequestBuilder<WeatherNotificationWorker>()
            .setInputData(workDataOf(
                "api_key" to apiKey,
                "city" to city
            ))
            .build()

        // Registrera workern
        WorkManager.getInstance(this).enqueue(workRequest)
    }

    private fun populateUI(response: WeatherResponse) {
        binding.address.text = response.cityName
        binding.temp.text = getString(R.string.temperature, response.main.temperature.toString())
        binding.tempMin.text = getString(R.string.min_temp, response.main.minTemp.toString())
        binding.tempMax.text = getString(R.string.max_temp, response.main.maxTemp.toString())
        binding.pressure.text = getString(R.string.pressure, response.main.pressure.toString())
        binding.humidity.text = getString(R.string.humidity, response.main.humidity.toString())

        // Dölja loader och visa mainContainer
        binding.loader.visibility = View.GONE
        binding.mainContainer.visibility = View.VISIBLE
    }

    override fun onSupportNavigateUp(): Boolean {
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val navController = navHostFragment.navController
        return navController.navigateUp() || super.onSupportNavigateUp()
    }

    private fun showError() {
        binding.loader.visibility = View.GONE
        binding.errorText.visibility = View.VISIBLE
    }
}
