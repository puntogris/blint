package com.puntogris.blint.data.local.records

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
class RecordsModule {
    @Provides
    fun providesRecordsDao(recordsDatabase: RecordsDatabase): RecordsDao {
        return recordsDatabase.recordsDao()
    }

    @Provides
    @Singleton
    fun provideRecordsDatabase(@ApplicationContext appContext: Context): RecordsDatabase {
        return Room
            .databaseBuilder(
                appContext,
                RecordsDatabase::class.java,
                "records_table"
            )
            .build()
    }
}