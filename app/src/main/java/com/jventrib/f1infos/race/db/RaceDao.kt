package com.jventrib.f1infos.race.db

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.jventrib.f1infos.race.model.Race

@Dao
interface RaceDao {

    @Query("SELECT * from race ORDER BY season ASC, round ASC")
    fun getAllRaces(): LiveData<List<Race>>

    @Query("SELECT * from race WHERE season = :season ORDER BY round ASC")
    fun getSeasonRaces(season: String): LiveData<List<Race>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(race: Race)

    @Query("DELETE FROM race")
    suspend fun deleteAll()
}