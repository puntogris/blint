package com.puntogris.blint.data.repository.products

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.puntogris.blint.data.data_source.local.dao.OrdersDao
import com.puntogris.blint.data.data_source.local.dao.ProductsDao
import com.puntogris.blint.data.data_source.local.dao.StatisticsDao
import com.puntogris.blint.data.data_source.local.dao.UsersDao
import com.puntogris.blint.model.ProductWithSuppliersCategories
import com.puntogris.blint.model.Record
import com.puntogris.blint.utils.Constants.INITIAL
import com.puntogris.blint.utils.RepoResult
import com.puntogris.blint.utils.SearchText
import com.puntogris.blint.utils.SimpleResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import javax.inject.Inject

class ProductRepository @Inject constructor(
    private val usersDao: UsersDao,
    private val productsDao: ProductsDao,
    private val statisticsDao: StatisticsDao,
    private val ordersDao: OrdersDao
) : IProductRepository {

    private val auth = FirebaseAuth.getInstance()

    private suspend fun currentBusiness() = usersDao.getCurrentBusinessFromUser()
    private fun getCurrentUid() = auth.currentUser

    override suspend fun saveProductDatabase(
        product: ProductWithSuppliersCategories,
        imageChanged: Boolean
    ): SimpleResult = withContext(Dispatchers.IO) {
        try {
            val isNewProduct = product.product.productId == 0
            val user = currentBusiness()

            if (isNewProduct) {
                product.product.apply {
                    businessId = user.businessId
                    totalInStock = amount
                }
            }

            val record = Record(
                type = INITIAL,
                amount = product.product.amount,
                productId = product.product.productId,
                productName = product.product.name,
                timestamp = Timestamp.now(),
                author = getCurrentUid()?.email.toString(),
                businessId = user.businessId,
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

    override suspend fun getProductsPagingDataFlow(): Flow<PagingData<ProductWithSuppliersCategories>> =
        withContext(Dispatchers.IO) {
            Pager(
                PagingConfig(
                    pageSize = 30,
                    enablePlaceholders = true,
                    maxSize = 200
                )
            ) {
                productsDao.getAllPaged()
            }.flow
        }

    override suspend fun deleteProductDatabase(productId: Int): SimpleResult =
        withContext(Dispatchers.IO) {
            try {

                productsDao.delete(productId)
                statisticsDao.decrementTotalProducts()

                SimpleResult.Success
            } catch (e: Exception) {
                SimpleResult.Failure
            }
        }

    override suspend fun getProductsWithNamePagingDataFlow(search: SearchText) =
        withContext(Dispatchers.IO) {
            if (search is SearchText.Category) {
                val ids = productsDao.getProductIdWithCategory(search.text)
                Pager(
                    PagingConfig(
                        pageSize = 30,
                        enablePlaceholders = true,
                        maxSize = 200
                    )
                ) { productsDao.getPagedProductsWithCategory(ids) }.flow
            } else {
                Pager(
                    PagingConfig(
                        pageSize = 30,
                        enablePlaceholders = true,
                        maxSize = 200
                    )
                ) {
                    productsDao.getPagedSearch("%${search.getData()}%")
                }.flow
            }
        }

    override suspend fun getProductRecordsPagingDataFlow(productId: Int) =
        withContext(Dispatchers.IO) {
            Pager(
                PagingConfig(
                    pageSize = 30,
                    enablePlaceholders = true,
                    maxSize = 200
                )
            ) {
                ordersDao.getProductRecordsPaged(productId)
            }.flow
        }

    override suspend fun getProductWithBarcode(barcode: String): RepoResult<ProductWithSuppliersCategories> =
        withContext(Dispatchers.IO) {
            try {
                val product: ProductWithSuppliersCategories? =
                    productsDao.getProductWithBarcode(barcode)

                if (product != null) RepoResult.Success(product)
                else RepoResult.Error(Exception())
            } catch (e: Exception) {
                RepoResult.Error(e)
            }
        }
}