package com.jventrib.formulainfo.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.jventrib.formulainfo.model.db.Driver
import kotlinx.coroutines.flow.Flow

@Dao
interface DriverDao {

    @Query("SELECT * from driver where driverId = :driverId")
    fun getDriver(driverId: String): Flow<Driver>

    @Query("SELECT * from driver")
    fun getAllDrivers(): Flow<List<Driver>>

    @Query("SELECT distinct driver.* from driver, race_result " +
            "where race_result.driverId = driver.driverId and race_result.season = :season")
    fun getSeasonDrivers(season: Int): Flow<List<Driver>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAll(races: List<Driver>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(driver: Driver)

    @Query("DELETE FROM driver")
    suspend fun deleteAll()
}