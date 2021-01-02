package com.puntogris.blint.data

import android.content.Context
import androidx.room.Room
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
            .build()
    }

}