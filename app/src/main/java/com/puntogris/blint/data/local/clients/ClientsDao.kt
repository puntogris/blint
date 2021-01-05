package com.puntogris.blint.data.local.clients

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

    @Query("SELECT * FROM client")
    suspend fun getClients(): List<Client>

    @Query("SELECT COUNT(*) FROM client")
    suspend fun getCount(): Int

    @Query("SELECT * FROM client")
    fun getAllPaged(): PagingSource<Int, Client>
}