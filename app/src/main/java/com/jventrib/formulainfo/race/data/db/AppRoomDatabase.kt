package com.jventrib.formulainfo.race.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.jventrib.formulainfo.common.utils.Converters
import com.jventrib.formulainfo.race.model.db.*

@Database(
    entities = [
        Race::class,
        Circuit::class,
        RaceResult::class,
        Driver::class,
        Constructor::class
    ],
    version = 2,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppRoomDatabase : RoomDatabase() {

    abstract fun raceDao(): RaceDao
    abstract fun circuitDao(): CircuitDao
    abstract fun raceResultDao(): RaceResultDao
    abstract fun driverDao(): DriverDao
    abstract fun constructorDao(): ConstructorDao
}