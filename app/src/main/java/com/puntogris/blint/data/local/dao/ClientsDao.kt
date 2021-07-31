package com.puntogris.blint.data.local.dao

import androidx.paging.PagingSource
import androidx.room.*
import com.puntogris.blint.model.Client

@Dao
interface ClientsDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(client: Client)

    @Query("DELETE FROM client WHERE clientId = :clientId")
    suspend fun delete(clientId: String)

    @RewriteQueriesToDropUnusedColumns
    @Query("SELECT * FROM client INNER JOIN user ON businessId = currentBusinessId WHERE userId = '1'")
    fun getAllPaged(): PagingSource<Int, Client>

    @RewriteQueriesToDropUnusedColumns
    @Query("SELECT * FROM client INNER JOIN user ON businessId = currentBusinessId WHERE userId = '1' AND name LIKE :name")
    fun getPagedSearch(name :String): PagingSource<Int, Client>

    @RewriteQueriesToDropUnusedColumns
    @Query("SELECT * FROM client INNER JOIN user ON businessId = currentBusinessId WHERE userId = '1'")
    suspend fun getAllClients(): List<Client>

}