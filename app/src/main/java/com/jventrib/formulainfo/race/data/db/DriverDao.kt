package com.jventrib.formulainfo.race.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.jventrib.formulainfo.race.model.db.Driver
import kotlinx.coroutines.flow.Flow

@Dao
interface DriverDao {

    @Query("SELECT * from driver")
    fun getAllDrivers(): Flow<List<Driver>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAll(races: List<Driver>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(driver: Driver)

    @Query("DELETE FROM driver")
    suspend fun deleteAll()
}