package com.puntogris.blint.feature_store.domain.repository

import androidx.paging.PagingData
import com.puntogris.blint.common.utils.types.SimpleResource
import com.puntogris.blint.feature_store.domain.model.order.Record
import com.puntogris.blint.feature_store.domain.model.product.Product
import com.puntogris.blint.feature_store.domain.model.product.ProductWithDetails
import kotlinx.coroutines.flow.Flow

interface ProductRepository {

    fun getProductsPaged(query: String?): Flow<PagingData<ProductWithDetails>>

    fun getProductRecordsPaged(productId: String): Flow<PagingData<Record>>

    suspend fun getProductsWithQuery(query: String): List<Product>

    suspend fun deleteProduct(productId: String): SimpleResource

    suspend fun saveProduct(productWithDetails: ProductWithDetails): SimpleResource

    suspend fun getProductWithBarcode(barcode: String): ProductWithDetails?

    suspend fun getProducts(): List<Product>
}