package com.jventrib.formulainfo.di

import android.content.Context
import com.jventrib.formulainfo.data.RaceRepository
import com.jventrib.formulainfo.data.db.AppRoomDatabase
import com.jventrib.formulainfo.data.remote.RaceRemoteDataSource
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.android.scopes.ViewModelScoped
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {

  @Provides
  @Singleton
  fun provideRaceRepository(
    roomDb: AppRoomDatabase,
    raceRemoteDataSource: RaceRemoteDataSource,
    @ApplicationContext appContext: Context
  ): RaceRepository {
    return RaceRepository(roomDb, raceRemoteDataSource, appContext)
  }
}
