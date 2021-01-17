package com.puntogris.blint.data.local.products

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import androidx.room.Room
import androidx.room.RoomDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Singleton


@InstallIn(ApplicationComponent::class)
@Module
class ProductsModule {
    @Provides
    fun providesProductsDao(productsDatabase: ProductsDatabase): ProductsDao {
        return productsDatabase.productsDao()
    }

    @Provides
    @Singleton
    fun provideProductsDatabase(@ApplicationContext appContext: Context): ProductsDatabase {
        return Room
            .databaseBuilder(
                appContext,
                ProductsDatabase::class.java,
                "products_table"
            )
            //.setJournalMode(RoomDatabase.JournalMode.TRUNCATE)
            .build()
    }

}