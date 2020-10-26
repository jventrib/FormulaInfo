package com.jventrib.f1infos.race.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.jventrib.f1infos.race.model.Race
import com.jventrib.f1infos.race.model.db.Driver
import com.jventrib.f1infos.race.model.db.RaceResultWithDriver
import kotlinx.coroutines.flow.Flow

@Dao
interface DriverDao {

    @Query("SELECT * from driver")
    fun getAllDrivers(): Flow<List<Driver>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAll(races: List<Driver>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(races: Driver)

    @Query("DELETE FROM race")
    suspend fun deleteAll()
}