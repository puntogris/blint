package com.puntogris.blint.data.data_source.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.puntogris.blint.data.data_source.local.dao.*
import com.puntogris.blint.model.*
import com.puntogris.blint.model.order.Debt
import com.puntogris.blint.model.order.Order
import com.puntogris.blint.model.order.OrderRecordCrossRef
import com.puntogris.blint.model.order.Record
import com.puntogris.blint.model.product.Product
import com.puntogris.blint.model.product.ProductCategoryCrossRef
import com.puntogris.blint.model.product.ProductSupplierCrossRef
import com.puntogris.blint.utils.Converters

@Database(
    entities = [
        Business::class,
        Client::class,
        Product::class,
        Record::class,
        Supplier::class,
        User::class,
        ProductSupplierCrossRef::class,
        Event::class,
        Statistic::class,
        Category::class,
        ProductCategoryCrossRef::class,
        Order::class,
        Debt::class,
        OrderRecordCrossRef::class
    ], version = 2, exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun businessDao(): BusinessDao
    abstract fun clientsDao(): ClientsDao
    abstract fun productsDao(): ProductsDao
    abstract fun ordersDao(): OrdersDao
    abstract fun suppliersDao(): SuppliersDao
    abstract fun userDao(): UsersDao
    abstract fun eventsDao(): EventsDao
    abstract fun statisticsDao(): StatisticsDao
    abstract fun categoriesDao(): CategoriesDao
    abstract fun debtDao(): DebtsDao
}