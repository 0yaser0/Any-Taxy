package com.cmc.mytaxi.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.cmc.mytaxi.data.local.models.Driver

@Dao
interface DriverDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun replaceDriver(driver : Driver)

    @Query("SELECT * FROM drivers WHERE driverId = :driverId")
    suspend fun getDriverById(driverId: Int): Driver?

    @Query("UPDATE drivers SET imageUri = :imageUri WHERE driverId = :driverId")
    suspend fun updateDriverImage(driverId: Int, imageUri: String)

}