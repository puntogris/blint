package com.puntogris.blint.data.local.user

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
class UsersModule {
    @Provides
    fun providesUsersDao(usersDatabase: UsersDatabase): UsersDao {
        return usersDatabase.usersDao()
    }

    @Provides
    @Singleton
    fun providesUsersDatabase(@ApplicationContext appContext: Context): UsersDatabase {
        return Room
            .databaseBuilder(
                appContext,
                UsersDatabase::class.java,
                "users_table"
            )
            .build()
    }
}