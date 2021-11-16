package com.puntogris.blint.data.repository.products

import androidx.paging.PagingData
import com.puntogris.blint.model.*
import com.puntogris.blint.utils.RepoResult
import com.puntogris.blint.utils.SearchText
import com.puntogris.blint.utils.SimpleResult
import kotlinx.coroutines.flow.Flow

interface IProductRepository {
    suspend fun saveProductDatabase(product: ProductWithSuppliersCategories, imageChanged: Boolean = false): SimpleResult
    suspend fun getProductsPagingDataFlow(): Flow<PagingData<ProductWithSuppliersCategories>>
    suspend fun deleteProductDatabase(productId:String): SimpleResult
    suspend fun getProductsWithNamePagingDataFlow(search: SearchText): Flow<PagingData<ProductWithSuppliersCategories>>
    suspend fun getProductRecordsPagingDataFlow(productId: String): Flow<PagingData<Record>>
    suspend fun getProductWithBarcode(barcode:String): RepoResult<ProductWithSuppliersCategories>
}