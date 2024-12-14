package com.cmc.mytaxi.ui.fragment

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.IntentSender
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.cmc.mytaxi.R
import com.cmc.mytaxi.data.viewmodel.CalculatTraficViewModel
import com.cmc.mytaxi.data.viewmodel.CalculatTraficViewModelFactory
import com.cmc.mytaxi.databinding.HomePageFragmentBinding
import com.cmc.mytaxi.databinding.SettingsFragmentLayoutBinding
import com.cmc.mytaxi.ui.activity.BuildProfile
import com.cmc.mytaxi.utils.NotificationHelper
import com.cmc.mytaxi.utils.PermissionsHelper
import com.cmc.mytaxi.utils.SetupUI
import com.cmc.mytaxi.utils.StatusBarUtils
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import pub.devrel.easypermissions.EasyPermissions
import java.util.Locale

class SettingsFragment : Fragment() {
    private var _binding: SettingsFragmentLayoutBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
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



    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}