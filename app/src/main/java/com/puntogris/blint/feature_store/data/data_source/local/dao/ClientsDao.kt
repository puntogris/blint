package com.puntogris.blint.feature_store.data.data_source.local.dao

import androidx.paging.PagingSource
import androidx.room.*
import com.puntogris.blint.feature_store.domain.model.Client

@Dao
interface ClientsDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(client: Client)

    @Query("DELETE FROM client WHERE clientId = :clientId")
    suspend fun delete(clientId: String)

    @Query("SELECT * FROM client WHERE clientId = :clientId")
    suspend fun getClient(clientId: String): Client

    @RewriteQueriesToDropUnusedColumns
    @Query("SELECT * FROM client INNER JOIN user ON businessId = currentBusinessId WHERE localReferenceId = '1'")
    suspend fun getClients(): List<Client>

    @RewriteQueriesToDropUnusedColumns
    @Query("SELECT * FROM client INNER JOIN user ON businessId = currentBusinessId WHERE localReferenceId = '1'")
    fun getClientsPaged(): PagingSource<Int, Client>

    @RewriteQueriesToDropUnusedColumns
    @Query("SELECT * FROM client c INNER JOIN user ON businessId = currentBusinessId WHERE localReferenceId = '1' AND c.name LIKE ('%'|| :query ||'%')")
    fun getClientsSearchPaged(query: String): PagingSource<Int, Client>

    @Query("UPDATE client SET debt = debt + :amount WHERE clientId = :clientId")
    suspend fun updateClientDebt(clientId: String, amount: Float)
}