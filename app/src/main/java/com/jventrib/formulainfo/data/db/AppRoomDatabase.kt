package com.jventrib.formulainfo.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.jventrib.formulainfo.utils.Converters
import com.jventrib.formulainfo.model.db.*

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