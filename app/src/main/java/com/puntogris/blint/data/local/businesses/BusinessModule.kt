package com.puntogris.blint.data.local.businesses

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
class BusinessModule {
    @Provides
    fun provideBusinessDao(businessDatabase: BusinessDatabase): BusinessDao {
        return businessDatabase.businessDao()
    }

    @Provides
    @Singleton
    fun provideBusinessDatabase(@ApplicationContext appContext: Context): BusinessDatabase {
        return Room
                .databaseBuilder(
                        appContext,
                        BusinessDatabase::class.java,
                        "business_table"
                )
                .build()
    }

}