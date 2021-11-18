package com.puntogris.blint.data.data_source.local.dao

import androidx.paging.PagingSource
import androidx.room.*
import com.puntogris.blint.model.Client

@Dao
interface ClientsDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(client: Client)

    @Query("DELETE FROM client WHERE clientId = :clientId")
    suspend fun delete(clientId: Int)

    @RewriteQueriesToDropUnusedColumns
    @Query("SELECT * FROM client INNER JOIN user ON businessId = currentBusinessId WHERE localReferenceId = '1'")
    fun getAllPaged(): PagingSource<Int, Client>

    @RewriteQueriesToDropUnusedColumns
    @Query("SELECT * FROM client c INNER JOIN user ON businessId = currentBusinessId WHERE localReferenceId = '1' AND c.name LIKE :name")
    fun getPagedSearch(name: String): PagingSource<Int, Client>

    @RewriteQueriesToDropUnusedColumns
    @Query("SELECT * FROM client INNER JOIN user ON businessId = currentBusinessId WHERE localReferenceId = '1'")
    suspend fun getAllClients(): List<Client>

}