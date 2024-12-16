package com.cmc.mytaxi.ui.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatDelegate
import androidx.lifecycle.ViewModelProvider
import com.cmc.mytaxi.App
import com.cmc.mytaxi.data.repository.DriverRepository
import com.cmc.mytaxi.data.viewmodel.ProfileViewModel
import com.cmc.mytaxi.data.viewmodel.ProfileViewModelFactory
import com.cmc.mytaxi.databinding.GetStartedBinding
import com.cmc.mytaxi.ui.adapters.GetstartedAdapter
import com.cmc.mytaxi.utils.AnyTaxyActivity

class GetStarted : AnyTaxyActivity() {

    lateinit var binding: GetStartedBinding
    private lateinit var driverViewModel: ProfileViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = GetStartedBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val driverRepository = DriverRepository(App.database.driverDao())
        val factory = ProfileViewModelFactory(driverRepository)
        driverViewModel = ViewModelProvider(this, factory)[ProfileViewModel::class.java]

        driverViewModel.getDriverById(1).observe(this) { driver ->
            driver?.let {
                if (it.isDarkMode) {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                } else {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                }
            }
        }

        val adapter = GetstartedAdapter(this)
        binding.viewPager.adapter = adapter
        binding.indicator.setViewPager(binding.viewPager)
    }
}
