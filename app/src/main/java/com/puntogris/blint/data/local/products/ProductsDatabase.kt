package com.puntogris.blint.data.local.products

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import com.puntogris.blint.model.Product
import com.puntogris.blint.utils.Converters

@Database(entities = [Product::class], version = 1, exportSchema = false)
@TypeConverters(Converters::class)
abstract class ProductsDatabase : RoomDatabase() {

    abstract fun productsDao(): ProductsDao

}