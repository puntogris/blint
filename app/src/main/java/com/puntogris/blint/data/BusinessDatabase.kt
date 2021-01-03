package com.puntogris.blint.data

import androidx.room.Database
import androidx.room.RoomDatabase
import com.puntogris.blint.model.Business
import com.puntogris.blint.model.Product

@Database(entities = [Business::class], version = 1, exportSchema = false)
abstract class BusinessDatabase : RoomDatabase() {

    abstract fun businessDao(): BusinessDao

}