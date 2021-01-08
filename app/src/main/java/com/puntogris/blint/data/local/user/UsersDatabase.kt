package com.puntogris.blint.data.local.user

import androidx.room.Database
import androidx.room.RoomDatabase
import com.puntogris.blint.model.FirestoreUser
import com.puntogris.blint.model.RoomUser

@Database(entities = [RoomUser::class,FirestoreUser::class], version = 1, exportSchema = false)
abstract class UsersDatabase : RoomDatabase() {

    abstract fun usersDao(): UsersDao

}