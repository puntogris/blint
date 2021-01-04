package com.puntogris.blint.data.local.suppliers

import androidx.room.Database
import androidx.room.RoomDatabase
import com.puntogris.blint.model.Supplier

@Database(entities = [Supplier::class], version = 1, exportSchema = false)
abstract class SuppliersDatabase : RoomDatabase() {

    abstract fun suppliersDao(): SuppliersDao

}