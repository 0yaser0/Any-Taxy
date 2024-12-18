package com.cmc.mytaxi.ui.activity

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ViewModelProvider
import com.cmc.mytaxi.App
import com.cmc.mytaxi.R
import com.cmc.mytaxi.data.repository.DriverRepository
import com.cmc.mytaxi.data.viewmodel.ProfileViewModel
import com.cmc.mytaxi.data.viewmodel.ProfileViewModelFactory
import com.cmc.mytaxi.databinding.ProfileLayoutBinding
import com.cmc.mytaxi.ui.fragments.profile.ProfileFragment
import com.cmc.mytaxi.utils.AnyTaxyActivity
import com.cmc.mytaxi.utils.SetupUI
import com.cmc.mytaxi.utils.StatusBarUtils

class BuildProfile : AnyTaxyActivity() {

    private lateinit var binding: ProfileLayoutBinding
    private lateinit var driverViewModel: ProfileViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ProfileLayoutBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { view, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            view.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val driverRepository = DriverRepository(App.database.driverDao())
        val factory = ProfileViewModelFactory(driverRepository)
        driverViewModel = ViewModelProvider(this, factory)[ProfileViewModel::class.java]

        StatusBarUtils.setStatusBarColor(this.window, R.color.darkGreen)
        SetupUI.setupUI(binding.root)

        driverViewModel.getDriverById(1).observe(this) { driver ->
            if (driver?.isCreated == true) {
                val intent = Intent(this, HomePage::class.java)
                startActivity(intent)
                finish()
            } else {
                supportFragmentManager.beginTransaction()
                    .replace(R.id.fragment_container, ProfileFragment())
                    .commit()
            }
        }

    }

}