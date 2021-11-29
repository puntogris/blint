package com.puntogris.blint.feature_store.data.data_source.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.puntogris.blint.common.utils.Converters
import com.puntogris.blint.feature_store.data.data_source.local.dao.*
import com.puntogris.blint.feature_store.domain.model.*
import com.puntogris.blint.feature_store.domain.model.order.Debt
import com.puntogris.blint.feature_store.domain.model.order.Order
import com.puntogris.blint.feature_store.domain.model.order.OrderRecordCrossRef
import com.puntogris.blint.feature_store.domain.model.order.Record
import com.puntogris.blint.feature_store.domain.model.product.Product
import com.puntogris.blint.feature_store.domain.model.product.ProductCategoryCrossRef
import com.puntogris.blint.feature_store.domain.model.product.ProductSupplierCrossRef

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
        Category::class,
        ProductCategoryCrossRef::class,
        Order::class,
        Debt::class,
        OrderRecordCrossRef::class
    ], version = 2, exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract val businessDao: BusinessDao
    abstract val clientsDao: ClientsDao
    abstract val productsDao: ProductsDao
    abstract val ordersDao: OrdersDao
    abstract val suppliersDao: SuppliersDao
    abstract val usersDao: UsersDao
    abstract val eventsDao: EventsDao
    abstract val categoriesDao: CategoriesDao
    abstract val debtsDao: DebtsDao
    abstract val recordsDao: RecordsDao
}