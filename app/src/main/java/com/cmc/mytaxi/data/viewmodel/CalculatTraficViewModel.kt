package com.cmc.mytaxi.data.viewmodel


import android.content.Context
import android.location.Location
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.cmc.mytaxi.data.local.models.RideModel
import com.cmc.mytaxi.data.repository.FareRepository
import com.cmc.mytaxi.utils.LocationHelper
import java.util.Locale
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

class CalculatTraficViewModel(context: Context) : ViewModel() {
    private val fareRepository = FareRepository()
    private val locationHelper = LocationHelper(context)

    private val _rideData = MutableLiveData<RideModel>()
    val rideData: LiveData<RideModel> get() = _rideData

    private var startTime: Long = 0
    private var lastLocation: Location? = null
    private var totalDistance: Double = 0.0

    init {
        _rideData.value = RideModel()
    }

    fun startRide() {
        startTime = System.currentTimeMillis()
        totalDistance = 0.0
        lastLocation = null

        locationHelper.getCurrentLocation { location ->
            if (location != null) {
                lastLocation = location
                updateRideData()
            } else {
                locationHelper.startLocationUpdates()
            }
        }
    }

    fun updateLocation() {
        locationHelper.getCurrentLocation { location ->
            if (location != null) {
                processLocation(location)
            } else {
                locationHelper.startLocationUpdates()
            }
        }
    }

    fun endRide() {
        locationHelper.stopLocationUpdates()
    }

    private fun processLocation(location: Location) {
        if (lastLocation != null) {
            val distance = calculateDistance(lastLocation!!, location)
            totalDistance += distance
        }
        lastLocation = location

        updateRideData()
    }

    private fun updateRideData() {
        val elapsedTime = ((System.currentTimeMillis() - startTime) / 1000) / 60
        val fare = fareRepository.calculateFare(totalDistance, elapsedTime)

        _rideData.value = RideModel(totalDistance, elapsedTime, fare)
    }


    private fun calculateDistance(start: Location, end: Location): Double {
        val earthRadius = 6371
        val dLat = Math.toRadians(end.latitude - start.latitude)
        val dLon = Math.toRadians(end.longitude - start.longitude)

        val a = sin(dLat / 2) * sin(dLat / 2) +
                cos(Math.toRadians(start.latitude)) * cos(Math.toRadians(end.latitude)) *
                sin(dLon / 2) * sin(dLon / 2)
        val c = 2 * atan2(sqrt(a), sqrt(1 - a))

        return earthRadius * c
    }
}