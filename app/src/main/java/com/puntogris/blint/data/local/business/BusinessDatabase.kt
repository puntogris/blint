package com.puntogris.blint.data.local.business

import androidx.room.Database
import androidx.room.RoomDatabase
import com.puntogris.blint.data.local.business.BusinessDao
import com.puntogris.blint.model.Business

@Database(entities = [Business::class], version = 1, exportSchema = false)
abstract class BusinessDatabase : RoomDatabase() {

    abstract fun businessDao(): BusinessDao

}