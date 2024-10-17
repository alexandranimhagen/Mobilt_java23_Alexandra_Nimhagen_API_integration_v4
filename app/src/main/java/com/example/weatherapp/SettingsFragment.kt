package com.example.weatherapp

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.weatherapp.databinding.FragmentSettingsBinding

class SettingsFragment : Fragment() {

    private var _binding: FragmentSettingsBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSettingsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Ladda sparad stad från SharedPreferences
        val sharedPreferences = requireActivity().getSharedPreferences("WeatherAppPreferences", Context.MODE_PRIVATE)
        val savedCity = sharedPreferences.getString("city", "")
        binding.editTextCity.setText(savedCity)

        // Spara stad när användaren klickar på knappen
        binding.buttonSaveSettings.setOnClickListener {
            val city = binding.editTextCity.text.toString()
            if (city.isNotEmpty()) {
                sharedPreferences.edit().putString("city", city).apply()
                Toast.makeText(requireContext(), R.string.city_saved, Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(requireContext(), R.string.enter_city, Toast.LENGTH_SHORT).show()
            }
        }

        // Lägg till lyssnare för "Go Home"-knappen
        binding.buttonGoHomeSettings.setOnClickListener {
            findNavController().popBackStack(R.id.homeFragment, false)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
