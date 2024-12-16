package com.cmc.mytaxi.ui.fragments.settings


import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.cmc.mytaxi.App
import com.cmc.mytaxi.R
import com.cmc.mytaxi.data.repository.DriverRepository
import com.cmc.mytaxi.data.viewmodel.ProfileViewModel
import com.cmc.mytaxi.data.viewmodel.ProfileViewModelFactory
import com.cmc.mytaxi.databinding.SettingsFragmentLayoutBinding
import com.cmc.mytaxi.ui.activity.BuildProfile
import com.cmc.mytaxi.utils.AnyTaxyFragments
import kotlinx.coroutines.launch
import java.util.Locale

class SettingsFragment : AnyTaxyFragments() {
    private var _binding: SettingsFragmentLayoutBinding? = null
    private val binding get() = _binding!!
    private lateinit var driverViewModel: ProfileViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = SettingsFragmentLayoutBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { view, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            view.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val driverRepository = DriverRepository(App.database.driverDao())
        val factory = ProfileViewModelFactory(driverRepository)
        driverViewModel = ViewModelProvider(this, factory)[ProfileViewModel::class.java]

        driverViewModel.getDriverById(1).observe(viewLifecycleOwner) { driver ->
            driver?.let {
                val imageUri = it.imageUri
                if (!imageUri.isNullOrEmpty()) {
                    val uri = Uri.parse(imageUri)
                    Glide.with(this@SettingsFragment).load(uri).placeholder(R.drawable.ic_camera)
                        .error(R.drawable.ic_error).into(binding.driverProfile)
                } else {
                    binding.driverProfile.setImageResource(R.drawable.first_name)
                }

                binding.fnameTextView.text = driver.firstName
                binding.lnameTextView.text = driver.lastName
            }
        }

        binding.buttonLogOut.setOnClickListener {
            lifecycleScope.launch {
                driverViewModel.clearDatabase()
                val intent = Intent(
                    requireContext(), BuildProfile::class.java
                )
                startActivity(intent)
                requireActivity().finish()
            }
        }

//        val languages = resources.getStringArray(R.array.languages)
//        val defaultLanguage = Locale.getDefault().language
//        val savedLanguage = requireActivity().getPreferences(Context.MODE_PRIVATE)
//            .getString("LANGUAGE_CODE", defaultLanguage)
//
//        binding.langue?.setOnClickListener {
//            context?.let { context ->
//                val builder = AlertDialog.Builder(context)
//                builder.setTitle(getString(R.string.language))
//                builder.setItems(languages) { _, which ->
//                    when (which) {
//                        0 -> setLocale("en")
//                        1 -> setLocale("fr")
//                        2 -> setLocale("ar")
//                        3 -> setLocale("ber")
//                        4 -> setLocale("es")
//                    }
//                }
//                builder.show()
//            }
//            setLocale(savedLanguage)
//        }
    }

    private fun setLocale(languageCode: String?) {
        if (languageCode == null) return

        val locale = Locale(languageCode)
        Locale.setDefault(locale)
        val config = requireActivity().resources.configuration
        config.setLocale(locale)

        requireActivity().createConfigurationContext(config)

        val editor = requireActivity().getPreferences(Context.MODE_PRIVATE).edit()
        editor.putString("LANGUAGE_CODE", languageCode)
        editor.apply()

        requireActivity().recreate()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}