package com.jventrib.formulainfo.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.jventrib.formulainfo.model.db.*
import com.jventrib.formulainfo.utils.Converters

@Database(
    entities = [
        Race::class,
        Circuit::class,
        Result::class,
        Driver::class,
        Constructor::class,
        LapTime::class
    ],
    version = 5,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppRoomDatabase : RoomDatabase() {

    abstract fun raceDao(): RaceDao
    abstract fun circuitDao(): CircuitDao
    abstract fun resultDao(): ResultDao
    abstract fun driverDao(): DriverDao
    abstract fun constructorDao(): ConstructorDao
    abstract fun lapTimeDao(): LapTimeDao
}