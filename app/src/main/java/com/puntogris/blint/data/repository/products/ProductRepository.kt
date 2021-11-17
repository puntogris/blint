package com.puntogris.blint.data.repository.products

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.google.firebase.Timestamp
import com.puntogris.blint.data.data_source.local.dao.OrdersDao
import com.puntogris.blint.data.data_source.local.dao.ProductsDao
import com.puntogris.blint.data.data_source.local.dao.StatisticsDao
import com.puntogris.blint.data.data_source.local.dao.UsersDao
import com.puntogris.blint.data.data_source.remote.FirebaseClients
import com.puntogris.blint.model.Product
import com.puntogris.blint.model.ProductWithSuppliersCategories
import com.puntogris.blint.model.Record
import com.puntogris.blint.utils.Constants.INITIAL
import com.puntogris.blint.utils.types.RepoResult
import com.puntogris.blint.utils.types.SimpleResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import javax.inject.Inject

class ProductRepository @Inject constructor(
    private val usersDao: UsersDao,
    private val productsDao: ProductsDao,
    private val statisticsDao: StatisticsDao,
    private val ordersDao: OrdersDao,
    private val firebase: FirebaseClients
) : IProductRepository {

    override suspend fun saveProductDatabase(
        product: ProductWithSuppliersCategories,
        imageChanged: Boolean
    ): SimpleResult = withContext(Dispatchers.IO) {
        try {
            val isNewProduct = product.product.productId == 0
            val currentBusinessId = usersDao.getCurrentBusinessId()

            if (isNewProduct) {
                product.product.apply {
                    businessId = currentBusinessId
                    totalInStock = amount
                }
            }

            val record = Record(
                type = INITIAL,
                amount = product.product.amount,
                productId = product.product.productId,
                productName = product.product.name,
                timestamp = Timestamp.now(),
                author = requireNotNull(firebase.currentUser?.email),
                businessId = currentBusinessId,
                barcode = product.product.barcode,
                totalInStock = product.product.amount,
                sku = product.product.sku,
            )

            productsDao.insertProduct(product)
            if (isNewProduct) statisticsDao.incrementTotalProducts()
            if (product.product.amount != 0) ordersDao.insert(record)

            SimpleResult.Success
        } catch (e: Exception) {
            SimpleResult.Failure
        }
    }

    override fun getProductsWithSuppliersCategoriesPaged(): Flow<PagingData<ProductWithSuppliersCategories>> {
        return Pager(
            PagingConfig(
                pageSize = 30,
                enablePlaceholders = true,
                maxSize = 200
            )
        ) { productsDao.getAllPaged() }.flow
    }

    override suspend fun deleteProductDatabase(productId: Int): SimpleResult =
        withContext(Dispatchers.IO) {
            SimpleResult.build {
                productsDao.delete(productId)
                statisticsDao.decrementTotalProducts()
            }
        }

    override fun getProductsSupplierCategoryWithQueryFlow(query: String): Flow<PagingData<ProductWithSuppliersCategories>> {
        return Pager(
            PagingConfig(
                pageSize = 30,
                enablePlaceholders = true,
                maxSize = 200
            )
        ) {
            productsDao.getProductsWithQuery("%$query%")
        }.flow
    }

    override suspend fun getProductsWithQuery(query: String): List<Product> = withContext(Dispatchers.IO){
        productsDao.getProductsSimpleWithQuery("%$query%")
    }

    override suspend fun getProductRecordsPagingDataFlow(productId: Int) =
        withContext(Dispatchers.IO) {
            Pager(
                PagingConfig(
                    pageSize = 30,
                    enablePlaceholders = true,
                    maxSize = 200
                )
            ) { ordersDao.getProductRecordsPaged(productId) }.flow
        }


    override suspend fun getProductWithBarcode(barcode: String): RepoResult<ProductWithSuppliersCategories> =
        withContext(Dispatchers.IO) {
            try {
                val product = productsDao.getProductWithBarcode(barcode)
                RepoResult.Success(requireNotNull(product))
            } catch (e: Exception) {
                RepoResult.Error(e)
            }
        }
}