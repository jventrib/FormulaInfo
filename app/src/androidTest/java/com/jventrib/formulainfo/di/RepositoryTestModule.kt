package com.jventrib.formulainfo.di

import android.content.Context
import com.jventrib.formulainfo.data.RaceRepository
import com.jventrib.formulainfo.data.db.AppRoomDatabase
import com.jventrib.formulainfo.data.remote.RaceRemoteDataSource
import dagger.Module
import dagger.Provides
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import dagger.hilt.testing.TestInstallIn

@Module
@TestInstallIn(components = [SingletonComponent::class], replaces = [RepositoryModule::class])
object RepositoryTestModule {

    @Provides
    fun provideRaceRepository(
        roomDb: AppRoomDatabase,
        raceRemoteDataSource: RaceRemoteDataSource,
        @ApplicationContext appContext: Context
    ): RaceRepository {
        return RaceRepository(roomDb, raceRemoteDataSource, appContext)
    }
}
