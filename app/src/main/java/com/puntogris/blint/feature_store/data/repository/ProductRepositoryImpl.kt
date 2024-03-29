package com.puntogris.blint.feature_store.data.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.puntogris.blint.common.utils.DispatcherProvider
import com.puntogris.blint.common.utils.UUIDGenerator
import com.puntogris.blint.common.utils.types.SimpleResource
import com.puntogris.blint.feature_store.data.data_source.local.dao.ProductsDao
import com.puntogris.blint.feature_store.data.data_source.local.dao.RecordsDao
import com.puntogris.blint.feature_store.data.data_source.local.dao.StoreDao
import com.puntogris.blint.feature_store.data.data_source.local.dao.UsersDao
import com.puntogris.blint.feature_store.data.data_source.toInitialRecord
import com.puntogris.blint.feature_store.domain.model.order.Record
import com.puntogris.blint.feature_store.domain.model.product.Product
import com.puntogris.blint.feature_store.domain.model.product.ProductWithDetails
import com.puntogris.blint.feature_store.domain.repository.ProductRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext

class ProductRepositoryImpl(
    private val usersDao: UsersDao,
    private val productsDao: ProductsDao,
    private val storeDao: StoreDao,
    private val recordsDao: RecordsDao,
    private val dispatcher: DispatcherProvider
) : ProductRepository {

    override suspend fun saveProduct(productWithDetails: ProductWithDetails): SimpleResource =
        withContext(dispatcher.io) {
            SimpleResource.build {
                val isNewProduct = productWithDetails.product.productId.isBlank()
                val currentStoreId = usersDao.getCurrentStoreId()

                if (isNewProduct) {
                    productWithDetails.product.apply {
                        productId = UUIDGenerator.randomUUID()
                        storeId = currentStoreId
                    }
                    storeDao.incrementTotalProducts()
                }

                productsDao.insertProduct(productWithDetails)

                if (productWithDetails.product.stock != 0) {
                    val record = productWithDetails.product.toInitialRecord(currentStoreId)
                    recordsDao.insert(record)
                }
            }
        }

    override fun getProductsPaged(query: String?): Flow<PagingData<ProductWithDetails>> {
        return Pager(
            PagingConfig(
                pageSize = 30,
                enablePlaceholders = true,
                maxSize = 200
            )
        ) {
            productsDao.getProductsWithQueryPaged(query ?: "")
        }.flow
    }

    override suspend fun deleteProduct(productId: String): SimpleResource =
        withContext(dispatcher.io) {
            SimpleResource.build {
                productsDao.delete(productId)
                storeDao.decrementTotalProducts()
            }
        }

    override suspend fun getProductsWithQuery(query: String): List<Product> =
        withContext(dispatcher.io) {
            productsDao.getProductsWithQuery(query)
        }

    override fun getProductRecordsPaged(productId: String): Flow<PagingData<Record>> {
        return Pager(
            PagingConfig(
                pageSize = 30,
                enablePlaceholders = true,
                maxSize = 200
            )
        ) { recordsDao.getProductRecordsPaged(productId) }.flow
    }

    override suspend fun getProductWithBarcode(barcode: String): ProductWithDetails? =
        withContext(dispatcher.io) {
            productsDao.getProductWithBarcode(barcode)
        }

    override suspend fun getProducts() = productsDao.getProducts()
}
