package com.puntogris.blint.data.repository.products

import androidx.paging.PagingData
import com.puntogris.blint.model.ProductWithSuppliersCategories
import com.puntogris.blint.model.Record
import com.puntogris.blint.utils.types.RepoResult
import com.puntogris.blint.utils.types.SearchText
import com.puntogris.blint.utils.types.SimpleResult
import kotlinx.coroutines.flow.Flow

interface IProductRepository {
    suspend fun saveProductDatabase(
        product: ProductWithSuppliersCategories,
        imageChanged: Boolean = false
    ): SimpleResult

    suspend fun getProductsPagingDataFlow(): Flow<PagingData<ProductWithSuppliersCategories>>

    suspend fun deleteProductDatabase(productId: Int): SimpleResult

    suspend fun getProductsWithNamePagingDataFlow(search: SearchText): Flow<PagingData<ProductWithSuppliersCategories>>

    suspend fun getProductRecordsPagingDataFlow(productId: Int): Flow<PagingData<Record>>

    suspend fun getProductWithBarcode(barcode: String): RepoResult<ProductWithSuppliersCategories>
}