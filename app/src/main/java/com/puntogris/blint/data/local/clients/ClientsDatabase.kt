package com.puntogris.blint.data.local.clients

import androidx.room.Database
import androidx.room.RoomDatabase
import com.puntogris.blint.model.Client

@Database(entities = [Client::class], version = 1, exportSchema = false)
abstract class ClientsDatabase : RoomDatabase() {

    abstract fun clientsDao(): ClientsDao

}