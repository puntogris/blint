package com.puntogris.blint.data.local.clients

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
class ClientsModule {
    @Provides
    fun providesClientDao(clientsDatabase: ClientsDatabase): ClientsDao {
        return clientsDatabase.clientsDao()
    }

    @Provides
    @Singleton
    fun providesClientsDatabase(@ApplicationContext appContext: Context): ClientsDatabase {
        return Room
            .databaseBuilder(
                appContext,
                ClientsDatabase::class.java,
                "clients_table"
            )
            .build()
    }
}