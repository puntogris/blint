package com.puntogris.blint.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.puntogris.blint.data.local.dao.*
import com.puntogris.blint.model.*
import com.puntogris.blint.utils.Converters

@Database(entities = [
    Business::class,
    Client::class,
    Product::class,
    Record::class,
    Supplier::class,
    RoomUser::class,
    ProductSupplierCrossRef::class,
    Event::class
                     ], version = 1, exportSchema = false)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun businessDao(): BusinessDao
    abstract fun clientsDao(): ClientsDao
    abstract fun productsDao(): ProductsDao
    abstract fun recordsDao(): RecordsDao
    abstract fun suppliersDao():SuppliersDao
    abstract fun userDao(): UsersDao
    abstract fun eventsDao(): EventsDao
}