package com.puntogris.blint.data.local.product

import androidx.room.Database
import androidx.room.RoomDatabase
import com.puntogris.blint.model.Product

@Database(entities = [Product::class], version = 1, exportSchema = false)
abstract class ProductsDatabase : RoomDatabase() {

    abstract fun productsDao(): ProductsDao

}