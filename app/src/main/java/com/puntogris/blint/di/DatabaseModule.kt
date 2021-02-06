package com.puntogris.blint.di

import android.content.Context
import androidx.room.Room
import com.puntogris.blint.data.local.AppDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Singleton

@InstallIn(ApplicationComponent::class)
@Module
class DatabaseModule {

    @Provides
    fun providesBusinessDao(appDatabase: AppDatabase) =  appDatabase.businessDao()

    @Provides
    fun providesClientsDao(appDatabase: AppDatabase) = appDatabase.clientsDao()

    @Provides
    fun providesProductsDao(appDatabase: AppDatabase) = appDatabase.productsDao()

    @Provides
    fun providesRecordsDao(appDatabase: AppDatabase) = appDatabase.recordsDao()

    @Provides
    fun providesSuppliersDao(appDatabase: AppDatabase) = appDatabase.suppliersDao()

    @Provides
    fun providesUserDao(appDatabase: AppDatabase) = appDatabase.userDao()

    @Provides
    fun providesEventDao(appDatabase: AppDatabase) = appDatabase.eventsDao()

    @Provides
    fun providesStatisticsDao(appDatabase: AppDatabase) = appDatabase.statisticsDao()

    @Provides
    @Singleton
    fun provideUserDatabase(@ApplicationContext appContext: Context): AppDatabase {
        return Room
            .databaseBuilder(
                appContext,
                AppDatabase::class.java,
                "blint_database"
            )
            .build()
    }

}