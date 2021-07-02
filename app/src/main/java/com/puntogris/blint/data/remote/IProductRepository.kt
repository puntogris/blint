package com.puntogris.blint.data.remote

import androidx.paging.PagingData
import com.puntogris.blint.model.Category
import com.puntogris.blint.model.Product
import com.puntogris.blint.model.ProductWithSuppliersCategories
import com.puntogris.blint.model.Record
import com.puntogris.blint.utils.RepoResult
import com.puntogris.blint.utils.SimpleResult
import kotlinx.coroutines.flow.Flow

interface IProductRepository {
    suspend fun saveProductDatabase(product: Product, suppliers:List<String>, categories: List<String>): SimpleResult
    suspend fun getProductsPagingDataFlow(): Flow<PagingData<ProductWithSuppliersCategories>>
    suspend fun deleteProductDatabase(productId:String): SimpleResult
    suspend fun getProductsWithNamePagingDataFlow(name:String): Flow<PagingData<ProductWithSuppliersCategories>>
    suspend fun getProductRecordsPagingDataFlow(productId: String): Flow<PagingData<Record>>
    suspend fun deleteProductCategoryDatabase(categories: List<Category>): SimpleResult
    suspend fun saveProductCategoryDatabase(category: Category): SimpleResult
    suspend fun getProductCategoriesDatabase(): Flow<List<Category>>
    suspend fun updateProductCategoryDatabase(category: Category): SimpleResult
}