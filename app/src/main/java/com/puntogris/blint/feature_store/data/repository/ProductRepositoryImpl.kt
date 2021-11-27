package com.puntogris.blint.feature_store.data.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.puntogris.blint.common.utils.DispatcherProvider
import com.puntogris.blint.common.utils.UUIDGenerator
import com.puntogris.blint.common.utils.types.SimpleResult
import com.puntogris.blint.feature_store.data.data_source.local.dao.OrdersDao
import com.puntogris.blint.feature_store.data.data_source.local.dao.ProductsDao
import com.puntogris.blint.feature_store.data.data_source.local.dao.StatisticsDao
import com.puntogris.blint.feature_store.data.data_source.local.dao.UsersDao
import com.puntogris.blint.feature_store.data.data_source.toRecord
import com.puntogris.blint.feature_store.domain.model.order.Record
import com.puntogris.blint.feature_store.domain.model.product.Product
import com.puntogris.blint.feature_store.domain.model.product.ProductWithDetails
import com.puntogris.blint.feature_store.domain.repository.ProductRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext

class ProductRepositoryImpl(
    private val usersDao: UsersDao,
    private val productsDao: ProductsDao,
    private val statisticsDao: StatisticsDao,
    private val ordersDao: OrdersDao,
    private val dispatcher: DispatcherProvider
) : ProductRepository {

    override suspend fun saveProduct(productWithDetails: ProductWithDetails): SimpleResult =
        withContext(dispatcher.io) {
            try {
                val isNewProduct = productWithDetails.product.productId.isBlank()
                val currentBusinessId = usersDao.getCurrentBusinessId()

                if (isNewProduct) {
                    productWithDetails.product.apply {
                        productId = UUIDGenerator.randomUUID()
                        businessId = currentBusinessId
                    }
                }

                productsDao.insertProduct(productWithDetails)

                if (isNewProduct) statisticsDao.incrementTotalProducts()

                if (productWithDetails.product.amount != 0) {
                    val record = productWithDetails.product.toRecord(currentBusinessId)
                    ordersDao.insert(record)
                }

                SimpleResult.Success
            } catch (e: Exception) {
                SimpleResult.Failure
            }
        }

    override fun getProductsPaged(): Flow<PagingData<ProductWithDetails>> {
        return Pager(
            PagingConfig(
                pageSize = 30,
                enablePlaceholders = true,
                maxSize = 200
            )
        ) { productsDao.getProductsPaged() }.flow
    }

    override suspend fun deleteProduct(productId: String): SimpleResult =
        withContext(dispatcher.io) {
            SimpleResult.build {
                productsDao.delete(productId)
                statisticsDao.decrementTotalProducts()
            }
        }

    override fun getProductsWithQueryPaged(query: String): Flow<PagingData<ProductWithDetails>> {
        return Pager(
            PagingConfig(
                pageSize = 30,
                enablePlaceholders = true,
                maxSize = 200
            )
        ) { productsDao.getProductsWithQueryPaged("%$query%") }.flow
    }

    override suspend fun getProductsWithQuery(query: String): List<Product> =
        withContext(dispatcher.io) {
            productsDao.getProductsWithQuery("%$query%")
        }

    override fun getProductRecordsPaged(productId: String): Flow<PagingData<Record>> {
        return Pager(
            PagingConfig(
                pageSize = 30,
                enablePlaceholders = true,
                maxSize = 200
            )
        ) { ordersDao.getProductRecordsPaged(productId) }.flow
    }


    override suspend fun getProductWithBarcode(barcode: String): ProductWithDetails? =
        withContext(dispatcher.io) {
            productsDao.getProductWithBarcode(barcode)
        }
}