package com.puntogris.blint.data.data_source.local.dao

import androidx.room.*
import com.puntogris.blint.model.Category

@Dao
interface CategoriesDao {

    @Insert
    suspend fun insert(category: Category)

    @Update
    suspend fun update(category: Category)

    @Query("DELETE FROM category WHERE categoryName IN (SELECT categoryName FROM category INNER JOIN user ON businessId = currentBusinessId WHERE localReferenceId = '1' AND categoryName = :categoryName)")
    suspend fun deleteCategory(categoryName: String)

    @RewriteQueriesToDropUnusedColumns
    @Query("SELECT * FROM category INNER JOIN user ON businessId = currentBusinessId WHERE localReferenceId = '1'")
    suspend fun getAllCategories(): List<Category>
}