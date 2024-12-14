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

class HomePageFragment : Fragment(), OnMapReadyCallback {
    private var _binding: HomePageFragmentBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModel: CalculatTraficViewModel
    private lateinit var notificationHelper: NotificationHelper
    private lateinit var map: GoogleMap
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    private var isRideActive: Boolean = false
    private val handler = Handler(Looper.getMainLooper())
    private val updateInterval: Long = 5000

    private companion object {
        const val LOCATION_PERMISSION_REQUEST_CODE = 1
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = HomePageFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { view, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            view.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val factory = CalculatTraficViewModelFactory(requireContext())
        viewModel = ViewModelProvider(this, factory)[CalculatTraficViewModel::class.java]
        notificationHelper = NotificationHelper(requireContext())

        StatusBarUtils.setStatusBarColor(requireActivity().window, R.color.white)
        SetupUI.setupUI(binding.root)

        setupLocationServices()

        setupRideToggleButton()

        observeRideData()

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireContext())

        setupMap()

        setupProfileImageClick()
    }

    private fun setupLocationServices() {
        PermissionsHelper.checkAndPromptLocationServices(
            requireActivity(),
            onLocationSettingsSatisfied = {
                Toast.makeText(requireContext(), "Location services are already enabled.", Toast.LENGTH_SHORT).show()
            },
            onResolutionRequired = { exception ->
                try {
                    exception.startResolutionForResult(
                        requireActivity(),
                        PermissionsHelper.LOCATION_REQUEST_CODE
                    )
                } catch (sendEx: IntentSender.SendIntentException) {
                    Toast.makeText(
                        requireContext(),
                        "Unable to start resolution: ${sendEx.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            },
            onFailure = { exception ->
                Toast.makeText(
                    requireContext(),
                    "Failed to check location settings: ${exception.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
        )
    }

    private fun setupRideToggleButton() {
        binding.btnToggleRide.setOnClickListener {
            if (!PermissionsHelper.hasLocationPermission(requireContext())) {
                PermissionsHelper.requestLocationPermission(requireActivity())
                return@setOnClickListener
            }

            if (!isRideActive) {
                startRide()
                binding.btnToggleRide.text = getString(R.string.end_ride)
                isRideActive = true
            } else {
                endRide()
                binding.btnToggleRide.text = getString(R.string.start_ride)
                isRideActive = false
            }
        }
    }

    private fun observeRideData() {
        viewModel.rideData.observe(viewLifecycleOwner) { ride ->
            val distance = String.format(Locale.ROOT, "%.2f", ride.distance)
            val fare = String.format(Locale.ROOT, "%.2f", ride.totalFare)

            binding.tvDistance.text = getString(R.string.distance_format, distance)
            binding.tvTimeElapsed.text = getString(R.string.time_elapsed_format, ride.timeElapsed)
            binding.tvTotalFare.text = getString(R.string.total_fare_format, fare)
        }
    }

    private fun setupMap() {
        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    private fun setupProfileImageClick() {
        binding.profileImage.setOnClickListener {
            val intent = Intent(requireContext(), BuildProfile::class.java).apply {
                putExtra("MainActivity", "editProfile")
            }
            startActivity(intent)
        }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap
        enableLocation()
    }

    private fun enableLocation() {
        if (ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            map.isMyLocationEnabled = true
            getCurrentLocation()
        } else {
            requestPermissions(
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                LOCATION_PERMISSION_REQUEST_CODE
            )
        }
    }

    @SuppressLint("MissingPermission")
    @Suppress("DEPRECATION")
    private fun getCurrentLocation() {
        fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
            location?.let { updateMapLocation(it) }
        }

        fusedLocationClient.requestLocationUpdates(
            com.google.android.gms.location.LocationRequest.create().apply {
                interval = 5000
                fastestInterval = 2000
                priority = com.google.android.gms.location.LocationRequest.PRIORITY_HIGH_ACCURACY
            },
            object : com.google.android.gms.location.LocationCallback() {
                override fun onLocationResult(locationResult: com.google.android.gms.location.LocationResult) {
                    locationResult.lastLocation?.let { updateMapLocation(it) }
                }
            },
            null
        )
    }

    private fun updateMapLocation(location: Location) {
        val currentLatLng = LatLng(location.latitude, location.longitude)
        map.clear()
        map.addMarker(MarkerOptions().position(currentLatLng).title("Ma position actuelle"))
        map.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 15f))
    }

    private fun startRide() {
        viewModel.startRide()
        handler.postDelayed(updateRideTask, updateInterval)
    }

    private fun endRide() {
        handler.removeCallbacks(updateRideTask)
        viewModel.rideData.value?.let { ride ->
            val distance = String.format(Locale.ROOT, "%.2f", ride.distance)
            val fare = String.format(Locale.ROOT, "%.2f", ride.totalFare)
            notificationHelper.sendFareNotification(
                fare, distance, ride.timeElapsed
            )
        }
        viewModel.endRide()
    }

    private val updateRideTask = object : Runnable {
        override fun run() {
            if (isRideActive) {
                viewModel.updateLocation()
                handler.postDelayed(this, updateInterval)
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                enableLocation()
            }
        }

        when {
            EasyPermissions.somePermissionPermanentlyDenied(this, permissions.toList()) -> {
                PermissionsHelper.showSettingsDialog(requireActivity())
            }
            PermissionsHelper.hasLocationPermission(requireContext()) -> {
                Toast.makeText(
                    requireContext(),
                    "Permission granted. You can now start the ride.",
                    Toast.LENGTH_SHORT
                ).show()
            }
            else -> {
                Toast.makeText(
                    requireContext(),
                    "Permission denied. Cannot start the ride.",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        handler.removeCallbacks(updateRideTask)
    }
}