package com.puntogris.blint.data.repo.imp

import androidx.paging.PagingData
import com.puntogris.blint.model.Category
import com.puntogris.blint.model.Product
import com.puntogris.blint.model.ProductWithSuppliersCategories
import com.puntogris.blint.model.Record
import com.puntogris.blint.utils.SearchText
import com.puntogris.blint.utils.SimpleResult
import kotlinx.coroutines.flow.Flow

interface IProductRepository {
    suspend fun saveProductDatabase(product: ProductWithSuppliersCategories, imageChanged: Boolean = false): SimpleResult
    suspend fun getProductsPagingDataFlow(): Flow<PagingData<ProductWithSuppliersCategories>>
    suspend fun deleteProductDatabase(productId:String): SimpleResult
    suspend fun getProductsWithNamePagingDataFlow(search: SearchText): Flow<PagingData<ProductWithSuppliersCategories>>
    suspend fun getProductRecordsPagingDataFlow(productId: String): Flow<PagingData<Record>>
    suspend fun deleteProductCategoryDatabase(categories: List<Category>): SimpleResult
    suspend fun saveProductCategoryDatabase(category: Category): SimpleResult
    suspend fun getProductCategoriesDatabase(): Flow<List<Category>>
    suspend fun updateProductCategoryDatabase(category: Category): SimpleResult
}