package com.jventrib.formulainfo.di

import android.app.Application
import androidx.room.Room
import com.jventrib.formulainfo.R
import com.jventrib.formulainfo.data.db.AppRoomDatabase
import com.jventrib.formulainfo.data.db.CircuitDao
import com.jventrib.formulainfo.data.db.ConstructorDao
import com.jventrib.formulainfo.data.db.DriverDao
import com.jventrib.formulainfo.data.db.RaceDao
import com.jventrib.formulainfo.data.db.ResultDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object PersistenceModule {

    @Provides
    @Singleton
    fun provideAppDatabase(application: Application): AppRoomDatabase {
        return Room
            .databaseBuilder(
                application,
                AppRoomDatabase::class.java,
                application.getString(R.string.database)
            )
            .createFromAsset("f1_database.db")
            .fallbackToDestructiveMigration()
            .build()
    }

    @Provides
    @Singleton
    fun provideRaceDao(appDatabase: AppRoomDatabase): RaceDao {
        return appDatabase.raceDao()
    }

    @Provides
    @Singleton
    fun provideCircuitDao(appDatabase: AppRoomDatabase): CircuitDao {
        return appDatabase.circuitDao()
    }

    @Provides
    @Singleton
    fun provideResultDao(appDatabase: AppRoomDatabase): ResultDao {
        return appDatabase.resultDao()
    }

    @Provides
    @Singleton
    fun provideDriverDao(appDatabase: AppRoomDatabase): DriverDao {
        return appDatabase.driverDao()
    }

    @Provides
    @Singleton
    fun provideConstructorDao(appDatabase: AppRoomDatabase): ConstructorDao {
        return appDatabase.constructorDao()
    }
}
