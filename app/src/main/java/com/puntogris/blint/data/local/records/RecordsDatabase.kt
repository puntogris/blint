package com.puntogris.blint.data.local.records

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.puntogris.blint.model.Record
import com.puntogris.blint.utils.Converters

@Database(entities = [Record::class], version = 1, exportSchema = false)
@TypeConverters(Converters::class)
abstract class RecordsDatabase : RoomDatabase() {

    abstract fun recordsDao(): RecordsDao

}