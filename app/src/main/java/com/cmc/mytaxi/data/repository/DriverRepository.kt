package com.cmc.mytaxi.data.repository

import com.cmc.mytaxi.data.local.dao.DriverDao
import com.cmc.mytaxi.data.local.models.Driver

class DriverRepository(private val driverDao: DriverDao) {

    suspend fun upsertDriver(driver: Driver) {
        driverDao.replaceDriver(driver)
    }

    suspend fun getDriverById(driverId: Int): Driver? {
        return driverDao.getDriverById(driverId)
    }

    suspend fun updateDriverImage(driverId: Int, imageUri: String) {
        driverDao.updateDriverImage(driverId, imageUri)
    }

    suspend fun updateDriver(driver: Driver) {
        driverDao.updateDriver(driver)
    }

    suspend fun clearDatabase() {
        driverDao.clearUserTable()
    }

    suspend fun updateDarkMode(id: Int, isDarkMode: Boolean) {
        driverDao.updateDarkMode(id, isDarkMode)
    }
}
