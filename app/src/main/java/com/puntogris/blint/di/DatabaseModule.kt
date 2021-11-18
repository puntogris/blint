package com.puntogris.blint.di

import android.content.Context
import androidx.room.Room
import com.puntogris.blint.data.data_source.local.AppDatabase
import com.puntogris.blint.data.data_source.remote.FirebaseClients
import com.puntogris.blint.data.data_source.remote.FirebaseUserApi
import com.puntogris.blint.data.data_source.remote.UserServerApi
import com.puntogris.blint.utils.DispatcherProvider
import com.puntogris.blint.utils.StandardDispatchers
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
class DatabaseModule {

    @Provides
    fun providesEmployeesDao(appDatabase: AppDatabase) = appDatabase.businessDao()

    @Provides
    fun providesClientsDao(appDatabase: AppDatabase) = appDatabase.clientsDao()

    @Provides
    fun providesProductsDao(appDatabase: AppDatabase) = appDatabase.productsDao()

    @Provides
    fun providesOrdersDao(appDatabase: AppDatabase) = appDatabase.ordersDao()

    @Provides
    fun providesSuppliersDao(appDatabase: AppDatabase) = appDatabase.suppliersDao()

    @Provides
    fun providesUserDao(appDatabase: AppDatabase) = appDatabase.userDao()

    @Provides
    fun providesEventDao(appDatabase: AppDatabase) = appDatabase.eventsDao()

    @Provides
    fun providesStatisticsDao(appDatabase: AppDatabase) = appDatabase.statisticsDao()

    @Provides
    fun providesCategoriesDao(appDatabase: AppDatabase) = appDatabase.categoriesDao()

    @Provides
    fun provideDebtsDao(appDatabase: AppDatabase) = appDatabase.debtDao()

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

    @Singleton
    @Provides
    fun provideDispatcherProvider(): DispatcherProvider {
        return StandardDispatchers()
    }

    @Singleton
    @Provides
    fun provideUserServerApi(firebaseClients: FirebaseClients): UserServerApi {
        return FirebaseUserApi(firebaseClients)
    }
}