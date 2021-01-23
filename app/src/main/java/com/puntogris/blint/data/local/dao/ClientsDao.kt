package com.puntogris.blint.data.local.dao

import androidx.paging.PagingSource
import androidx.room.*
import com.puntogris.blint.model.Client

@Dao
interface ClientsDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(client: Client)

    @Update
    suspend fun update(client: Client)

    @Query("DELETE FROM client WHERE id = :id")
    suspend fun delete(id: Int)

    @Query("SELECT * FROM client WHERE id = :id")
    suspend fun getClient(id:Int):Client

    @Query("SELECT COUNT(*) FROM client")
    suspend fun getCount(): Int

    @Query("SELECT * FROM client")
    fun getAllPaged(): PagingSource<Int, Client>

    @Query("SELECT * FROM client WHERE name LIKE :name")
    fun getPagedSearch(name :String): PagingSource<Int, Client>

    @Query("SELECT * FROM client")
    suspend fun getAllClients(): List<Client>

}