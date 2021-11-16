package com.puntogris.blint.di

import android.content.Context
import androidx.room.Room
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.puntogris.blint.data.data_source.local.AppDatabase
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
    fun providesEmployeesDao(appDatabase: AppDatabase) =  appDatabase.employeesDao()

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
        val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("ALTER TABLE product ADD minStock INTEGER default 0 NOT NULL")
            }
        }

        return Room
            .databaseBuilder(
                appContext,
                AppDatabase::class.java,
                "blint_database"
            )
            .addMigrations(MIGRATION_1_2)
            .build()
    }

}