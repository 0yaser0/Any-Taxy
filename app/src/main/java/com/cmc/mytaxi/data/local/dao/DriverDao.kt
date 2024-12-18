package com.cmc.mytaxi.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.cmc.mytaxi.data.local.models.Driver

@Dao
interface DriverDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun replaceDriver(driver: Driver)

    @Query("SELECT * FROM drivers WHERE driverId = :driverId")
    suspend fun getDriverById(driverId: Int): Driver?

    @Query("UPDATE drivers SET imageUri = :imageUri WHERE driverId = :driverId")
    suspend fun updateDriverImage(driverId: Int, imageUri: String)

    @Update
    suspend fun updateDriver(driver: Driver)

    @Query("DELETE FROM drivers")
    suspend fun clearUserTable()

    @Query("UPDATE drivers SET isDarkMode = :isDarkMode WHERE driverId = :id")
    suspend fun updateDarkMode(id: Int, isDarkMode: Boolean)

}