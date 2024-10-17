package com.example.weatherapp

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.example.weatherapp.databinding.FragmentWeatherDetailsBinding
import java.text.SimpleDateFormat
import java.util.*

class WeatherDetailsFragment : Fragment() {

    private var _binding: FragmentWeatherDetailsBinding? = null
    private val binding get() = _binding!!
    private val viewModel: WeatherViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentWeatherDetailsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Använd default_city från strings.xml
        val city = getString(R.string.default_city)
        val apiKey = "06c921750b9a82d8f5d1294e1586276f"

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

        // Lyssnare för "Go Home"-knappen
        binding.buttonGoHomeWeatherDetails.setOnClickListener {
            findNavController().popBackStack(R.id.homeFragment, false)
        }
    }

    private fun populateUI(weatherResponse: WeatherResponse) {
        val updatedAtText = getString(R.string.updated_at, SimpleDateFormat("dd/MM/yyyy hh:mm a", Locale.ENGLISH).format(Date()))

        // Använd View Binding för att uppdatera UI med väderdata
        binding.address.text = getString(R.string.address_placeholder, weatherResponse.cityName, weatherResponse.sys.country)
        binding.updatedAt.text = updatedAtText
        binding.status.text = weatherResponse.weather[0].description.replaceFirstChar { it.uppercase() }

        // Ladda väderikonen från API:et med Glide
        val iconUrl = "https://openweathermap.org/img/wn/${weatherResponse.weather[0].icon}@2x.png"
        Glide.with(this)
            .load(iconUrl)
            .into(binding.weatherIcon)

        // Andra väderdata
        binding.temp.text = getString(R.string.temperature_placeholder, String.format(Locale.US, "%.1f", weatherResponse.main.temperature))
        binding.tempMin.text = getString(R.string.min_temp_placeholder, String.format(Locale.US, "%.1f", weatherResponse.main.minTemp))
        binding.tempMax.text = getString(R.string.max_temp_placeholder, String.format(Locale.US, "%.1f", weatherResponse.main.maxTemp))
        binding.sunrise.text = SimpleDateFormat("hh:mm a", Locale.ENGLISH).format(Date(weatherResponse.sys.sunrise * 1000))
        binding.sunset.text = SimpleDateFormat("hh:mm a", Locale.ENGLISH).format(Date(weatherResponse.sys.sunset * 1000))
        binding.wind.text = getString(R.string.wind_placeholder, weatherResponse.wind.speed.toString())
        binding.pressure.text = getString(R.string.pressure_placeholder, weatherResponse.main.pressure.toString())
        binding.humidity.text = getString(R.string.humidity_placeholder, weatherResponse.main.humidity.toString())

        // Dölja loader och visa mainContainer
        binding.loader.visibility = View.GONE
        binding.mainContainer.visibility = View.VISIBLE
    }

    private fun showError() {
        binding.loader.visibility = View.GONE
        binding.errorText.visibility = View.VISIBLE
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
