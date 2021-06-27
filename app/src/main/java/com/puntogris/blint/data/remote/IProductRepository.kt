package com.puntogris.blint.data.remote

import androidx.paging.PagingData
import com.google.common.base.Suppliers
import com.puntogris.blint.model.Category
import com.puntogris.blint.model.Product
import com.puntogris.blint.utils.RepoResult
import com.puntogris.blint.utils.SimpleResult
import kotlinx.coroutines.flow.Flow

interface IProductRepository {
    suspend fun saveProductDatabase(product: Product, suppliers:List<Int>, categories: List<Int>): SimpleResult
    suspend fun getProductsPagingDataFlow(): Flow<PagingData<Product>>
    suspend fun deleteProductDatabase(productId:String): SimpleResult
}