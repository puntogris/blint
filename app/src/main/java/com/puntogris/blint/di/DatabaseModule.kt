package com.puntogris.blint.di

import android.content.Context
import androidx.room.Room
import com.puntogris.blint.common.data.data_source.FirebaseClients
import com.puntogris.blint.common.framework.ExcelDrawer
import com.puntogris.blint.common.framework.PDFCreator
import com.puntogris.blint.common.utils.DispatcherProvider
import com.puntogris.blint.common.utils.StandardDispatchers
import com.puntogris.blint.feature_store.data.data_source.local.AppDatabase
import com.puntogris.blint.feature_store.data.data_source.local.SharedPreferences
import com.puntogris.blint.feature_store.data.data_source.local.dao.*
import com.puntogris.blint.feature_store.data.data_source.remote.FirebaseUserApi
import com.puntogris.blint.feature_store.data.data_source.remote.UserServerApi
import com.puntogris.blint.feature_store.data.repository.*
import com.puntogris.blint.feature_store.domain.repository.*
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
class DatabaseModule {

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
    fun providesEmployeesDao(appDatabase: AppDatabase) = appDatabase.storeDao

    @Singleton
    @Provides
    fun providesClientsDao(appDatabase: AppDatabase) = appDatabase.tradersDao

    @Singleton
    @Provides
    fun providesProductsDao(appDatabase: AppDatabase) = appDatabase.productsDao

    @Singleton
    @Provides
    fun providesOrdersDao(appDatabase: AppDatabase) = appDatabase.ordersDao

    @Singleton
    @Provides
    fun providesRecordsDao(appDatabase: AppDatabase) = appDatabase.recordsDao


    @Singleton
    @Provides
    fun providesUserDao(appDatabase: AppDatabase) = appDatabase.usersDao

    @Singleton
    @Provides
    fun providesEventDao(appDatabase: AppDatabase) = appDatabase.eventsDao

    @Singleton
    @Provides
    fun providesCategoriesDao(appDatabase: AppDatabase) = appDatabase.categoriesDao

    @Singleton
    @Provides
    fun provideDebtsDao(appDatabase: AppDatabase) = appDatabase.debtsDao

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

    @Singleton
    @Provides
    fun provideStoreRepository(
        storeDao: StoreDao,
        usersDao: UsersDao,
        sharedPreferences: SharedPreferences,
        dispatcher: DispatcherProvider,
    ): StoreRepository {
        return StoreRepositoryImpl(
            storeDao,
            usersDao,
            sharedPreferences,
            dispatcher
        )
    }

    @Singleton
    @Provides
    fun provideCategoryRepository(
        usersDao: UsersDao,
        categoriesDao: CategoriesDao,
        dispatcher: DispatcherProvider
    ): CategoriesRepository {
        return CategoriesRepositoryImpl(usersDao, categoriesDao, dispatcher)
    }

    @Singleton
    @Provides
    fun provideClientRepository(
        tradersDao: TradersDao,
        usersDao: UsersDao,
        recordsDao: RecordsDao,
        storeDao: StoreDao,
        dispatcher: DispatcherProvider
    ): TraderRepository {
        return TraderRepositoryImpl(
            tradersDao,
            usersDao,
            recordsDao,
            storeDao,
            dispatcher
        )
    }

    @Singleton
    @Provides
    fun provideDebtsRepository(
        debtsDao: DebtsDao,
        usersDao: UsersDao,
        dispatcher: DispatcherProvider
    ): DebtRepository {
        return DebtRepositoryImpl(debtsDao, usersDao, dispatcher)
    }

    @Singleton
    @Provides
    fun provideEventRepository(
        eventsDao: EventsDao,
        usersDao: UsersDao,
        dispatcher: DispatcherProvider
    ): EventRepository {
        return EventRepositoryImpl(eventsDao, usersDao, dispatcher)
    }

    @Singleton
    @Provides
    fun provideOrdersRepository(
        dispatcher: DispatcherProvider,
        appDatabase: AppDatabase,
        pdfCreator: PDFCreator
    ): OrderRepository {
        return OrderRepositoryImpl(dispatcher, appDatabase, pdfCreator)
    }

    @Singleton
    @Provides
    fun provideProductRepository(
        usersDao: UsersDao,
        productsDao: ProductsDao,
        storeDao: StoreDao,
        recordsDao: RecordsDao,
        dispatcher: DispatcherProvider
    ): ProductRepository {
        return ProductRepositoryImpl(
            usersDao,
            productsDao,
            storeDao,
            recordsDao,
            dispatcher
        )
    }

    @Singleton
    @Provides
    fun provideStatisticRepository(
        tradersDao: TradersDao,
        dispatcher: DispatcherProvider,
        excelDrawer: ExcelDrawer,
        recordsDao: RecordsDao,
        productsDao: ProductsDao
    ): ReportsRepository {
        return ReportsRepositoryImpl(
            dispatcher,
            excelDrawer,
            tradersDao,
            recordsDao,
            productsDao
        )
    }


    @Singleton
    @Provides
    fun provideUserRepository(
        @ApplicationContext context: Context,
        appDatabase: AppDatabase,
        sharedPreferences: SharedPreferences,
        dispatcher: DispatcherProvider,
        userServerApi: UserServerApi
    ): UserRepository {
        return UserRepositoryImpl(
            context,
            appDatabase,
            sharedPreferences,
            dispatcher,
            userServerApi
        )
    }

    @Singleton
    @Provides
    fun provideExcelDrawer(
        @ApplicationContext context: Context,
        workbook: XSSFWorkbook
    ): ExcelDrawer {
        return ExcelDrawer(context, workbook)
    }

    @Singleton
    @Provides
    fun provideExcelWorkBook() = XSSFWorkbook()
}