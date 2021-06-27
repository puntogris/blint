package com.puntogris.blint.data.local.dao

import androidx.lifecycle.LiveData
import androidx.paging.PagingSource
import androidx.room.*
import com.puntogris.blint.model.Client
import com.puntogris.blint.model.Product

@Dao
interface ClientsDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(client: Client)

    @Update
    suspend fun update(client: Client)

    @Query("DELETE FROM client WHERE clientId = :id")
    suspend fun delete(id: String)

    @RewriteQueriesToDropUnusedColumns
    @Query("SELECT * FROM client WHERE clientId = :id")
    suspend fun getClient(id:String):Client

    @Query("SELECT COUNT(*) FROM client INNER JOIN roomuser ON businessId = currentBusinessId WHERE userId = '1'")
    fun getCount(): LiveData<Int>

    @RewriteQueriesToDropUnusedColumns
    @Query("SELECT * FROM client INNER JOIN roomuser ON businessId = currentBusinessId WHERE userId = '1'")
    fun getAllPaged(): PagingSource<Int, Client>

    @RewriteQueriesToDropUnusedColumns
    @Query("SELECT * FROM client INNER JOIN roomuser ON businessId = currentBusinessId WHERE userId = '1' AND name LIKE :name")
    fun getPagedSearch(name :String): PagingSource<Int, Client>

    @RewriteQueriesToDropUnusedColumns
    @Query("SELECT * FROM client INNER JOIN roomuser ON businessId = currentBusinessId WHERE userId = '1'")
    suspend fun getAllClients(): List<Client>

    @RewriteQueriesToDropUnusedColumns
    @Query("SELECT * FROM client INNER JOIN roomuser ON businessId = currentBusinessId WHERE userId = '1' AND name LIKE :name LIMIT 30")
    suspend fun getClientWithName(name: String): List<Client>

    @Query("UPDATE client SET debt = debt + :amount WHERE clientId = :clientId")
    suspend fun updateClientDebt(clientId: String, amount: Float)


}