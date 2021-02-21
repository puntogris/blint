package com.puntogris.blint.data.local.dao

import androidx.room.*
import com.puntogris.blint.model.Category
import com.puntogris.blint.model.Supplier
import kotlinx.coroutines.flow.Flow

@Dao
interface CategoriesDao {

    @Insert
    suspend fun insert(category: Category)
  //  @Query("UPDATE statistic SET totalProducts = totalProducts + 1 WHERE statisticId IN (SELECT statisticId FROM statistic INNER JOIN roomuser ON businessId = currentBusinessId WHERE id = '1') ")

    @Delete
    suspend fun deleteCategory(category: Category)

    @RewriteQueriesToDropUnusedColumns
    @Query("SELECT * FROM category INNER JOIN roomuser ON businessId = currentBusinessId WHERE id = '1'")
    suspend fun getAllCategories(): List<Category>

    @RewriteQueriesToDropUnusedColumns
    @Query("SELECT * FROM category INNER JOIN roomuser ON businessId = currentBusinessId WHERE id = '1'")
    fun getAllCategoriesFlow(): Flow<List<Category>>
}