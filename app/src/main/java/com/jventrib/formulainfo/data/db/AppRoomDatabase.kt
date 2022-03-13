package com.jventrib.formulainfo.data.db

import androidx.room.AutoMigration
import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.jventrib.formulainfo.model.db.Circuit
import com.jventrib.formulainfo.model.db.Constructor
import com.jventrib.formulainfo.model.db.Driver
import com.jventrib.formulainfo.model.db.Lap
import com.jventrib.formulainfo.model.db.RaceInfo
import com.jventrib.formulainfo.model.db.ResultInfo
import com.jventrib.formulainfo.utils.Converters

@Database(
    entities = [
        RaceInfo::class,
        Circuit::class,
        ResultInfo::class,
        Driver::class,
        Constructor::class,
        Lap::class
    ],
    version = 11,
    // exportSchema = false,
    autoMigrations = [
        AutoMigration(from = 10, to = 11)
    ]
)
@TypeConverters(Converters::class)
abstract class AppRoomDatabase : RoomDatabase() {

    abstract fun raceDao(): RaceDao
    abstract fun circuitDao(): CircuitDao
    abstract fun resultDao(): ResultDao
    abstract fun driverDao(): DriverDao
    abstract fun constructorDao(): ConstructorDao
    abstract fun lapTimeDao(): LapDao
}
