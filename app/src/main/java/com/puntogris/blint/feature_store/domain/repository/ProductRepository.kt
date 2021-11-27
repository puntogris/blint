package com.puntogris.blint.feature_store.domain.repository

import androidx.paging.PagingData
import com.puntogris.blint.common.utils.types.SimpleResult
import com.puntogris.blint.feature_store.domain.model.order.Record
import com.puntogris.blint.feature_store.domain.model.product.Product
import com.puntogris.blint.feature_store.domain.model.product.ProductWithDetails
import kotlinx.coroutines.flow.Flow

interface ProductRepository {

    fun getProductsPaged(): Flow<PagingData<ProductWithDetails>>

    fun getProductsWithQueryPaged(query: String): Flow<PagingData<ProductWithDetails>>

    fun getProductRecordsPaged(productId: String): Flow<PagingData<Record>>

    suspend fun getProductsWithQuery(query: String): List<Product>

    suspend fun deleteProduct(productId: String): SimpleResult

    suspend fun saveProduct(productWithDetails: ProductWithDetails): SimpleResult

    suspend fun getProductWithBarcode(barcode: String): ProductWithDetails?
}