package com.puntogris.blint.data.local.suppliers

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
class SuppliersModule {
    @Provides
    fun provideSuppliersDao(suppliersDatabase: SuppliersDatabase): SuppliersDao {
        return suppliersDatabase.suppliersDao()
    }

    @Provides
    @Singleton
    fun provideSuppliersDatabase(@ApplicationContext appContext: Context): SuppliersDatabase {
        return Room
            .databaseBuilder(
                appContext,
                SuppliersDatabase::class.java,
                "supplier_table"
            )
            .build()
    }
}