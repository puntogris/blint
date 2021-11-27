package com.puntogris.blint.di

import android.content.Context
import androidx.room.Room
import com.puntogris.blint.common.data.data_source.FirebaseClients
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
    fun providesEmployeesDao(appDatabase: AppDatabase) = appDatabase.businessDao

    @Singleton
    @Provides
    fun providesClientsDao(appDatabase: AppDatabase) = appDatabase.clientsDao

    @Singleton
    @Provides
    fun providesProductsDao(appDatabase: AppDatabase) = appDatabase.productsDao

    @Singleton
    @Provides
    fun providesOrdersDao(appDatabase: AppDatabase) = appDatabase.ordersDao

    @Singleton
    @Provides
    fun providesSuppliersDao(appDatabase: AppDatabase) = appDatabase.suppliersDao

    @Singleton
    @Provides
    fun providesUserDao(appDatabase: AppDatabase) = appDatabase.usersDao

    @Singleton
    @Provides
    fun providesEventDao(appDatabase: AppDatabase) = appDatabase.eventsDao

    @Singleton
    @Provides
    fun providesStatisticsDao(appDatabase: AppDatabase) = appDatabase.statisticsDao

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
    fun provideBusinessRepository(
        businessDao: BusinessDao,
        usersDao: UsersDao,
        statisticsDao: StatisticsDao,
        sharedPreferences: SharedPreferences,
        dispatcher: DispatcherProvider,
    ): BusinessRepository {
        return BusinessRepositoryImpl(
            businessDao,
            usersDao,
            statisticsDao,
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
        clientsDao: ClientsDao,
        usersDao: UsersDao,
        statisticsDao: StatisticsDao,
        ordersDao: OrdersDao,
        dispatcher: DispatcherProvider
    ): ClientRepository {
        return ClientRepositoryImpl(
            clientsDao,
            usersDao,
            statisticsDao,
            ordersDao,
            dispatcher
        )
    }

    @Singleton
    @Provides
    fun provideDebtsRepository(
        debtsDao: DebtsDao,
        usersDao: UsersDao,
        dispatcher: DispatcherProvider
    ): DebtsRepository {
        return DebtsRepositoryImpl(debtsDao, usersDao, dispatcher)
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
        appDatabase: AppDatabase
    ): OrdersRepository {
        return OrdersRepositoryImpl(dispatcher, appDatabase)
    }

    @Singleton
    @Provides
    fun provideProductRepository(
        usersDao: UsersDao,
        productsDao: ProductsDao,
        statisticsDao: StatisticsDao,
        ordersDao: OrdersDao,
        dispatcher: DispatcherProvider
    ): ProductRepository {
        return ProductRepositoryImpl(
            usersDao,
            productsDao,
            statisticsDao,
            ordersDao,
            dispatcher
        )
    }

    @Singleton
    @Provides
    fun provideStatisticRepository(
        statisticsDao: StatisticsDao,
        clientsDao: ClientsDao,
        suppliersDao: SuppliersDao,
        dispatcher: DispatcherProvider
    ): StatisticRepository {
        return StatisticRepositoryImpl(
            statisticsDao,
            clientsDao,
            suppliersDao,
            dispatcher
        )
    }

    @Singleton
    @Provides
    fun provideSupplierRepository(
        suppliersDao: SuppliersDao,
        usersDao: UsersDao,
        statisticsDao: StatisticsDao,
        ordersDao: OrdersDao,
        dispatcher: DispatcherProvider
    ): SupplierRepository {
        return SupplierRepositoryImpl(
            suppliersDao,
            usersDao,
            statisticsDao,
            ordersDao,
            dispatcher
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
}