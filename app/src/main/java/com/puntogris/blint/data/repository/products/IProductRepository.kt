package com.puntogris.blint.data.repository.products

import androidx.paging.PagingData
import com.puntogris.blint.model.order.Record
import com.puntogris.blint.model.product.Product
import com.puntogris.blint.model.product.ProductWithDetails
import com.puntogris.blint.utils.types.SimpleResult
import kotlinx.coroutines.flow.Flow

interface IProductRepository {

    fun getProductsPaged(): Flow<PagingData<ProductWithDetails>>

    fun getProductsWithQueryPaged(query: String): Flow<PagingData<ProductWithDetails>>

    fun getProductRecordsPaged(productId: String): Flow<PagingData<Record>>

    suspend fun getProductsWithQuery(query: String): List<Product>

    suspend fun deleteProduct(productId: String): SimpleResult

    suspend fun saveProduct(productWithDetails: ProductWithDetails): SimpleResult

    suspend fun getProductWithBarcode(barcode: String): ProductWithDetails?
}