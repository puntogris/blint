package com.puntogris.blint.data.local.dao

import androidx.room.*
import com.puntogris.blint.model.Category
import com.puntogris.blint.model.FirestoreCategory
import com.puntogris.blint.model.Product
import com.puntogris.blint.model.Supplier
import kotlinx.coroutines.flow.Flow

@Dao
interface CategoriesDao {

    @Insert
    suspend fun insert(category: Category)

    @Update
    suspend fun update(category: Category)

    @Delete
    suspend fun deleteCategory(category: Category)

    @Delete
    suspend fun deleteCategory(categories: List<Category>)

    @RewriteQueriesToDropUnusedColumns
    @Query("SELECT * FROM category INNER JOIN roomuser ON businessId = currentBusinessId WHERE userId = '1'")
    suspend fun getAllCategories(): List<Category>

    @RewriteQueriesToDropUnusedColumns
    @Query("SELECT * FROM category INNER JOIN roomuser ON businessId = currentBusinessId WHERE userId = '1'")
    fun getAllCategoriesFlow(): Flow<List<Category>>

    @RewriteQueriesToDropUnusedColumns
    @Query("SELECT * FROM category INNER JOIN roomuser ON businessId = currentBusinessId WHERE userId = '1' AND name LIKE :name LIMIT 5")
    suspend fun getCategoriesWithName(name: String): List<FirestoreCategory>

}