package com.puntogris.blint.data.remote

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.QuerySnapshot
import com.puntogris.blint.data.remote.deserializers.OrderDeserializer
import com.puntogris.blint.model.Event
import com.puntogris.blint.model.Order
import com.puntogris.blint.model.OrderWithRecords
import kotlinx.coroutines.tasks.await

class FirestoreOrdersPagingSource(
    private val query: Query
) : PagingSource<QuerySnapshot, OrderWithRecords>() {

    override suspend fun load(params: LoadParams<QuerySnapshot>): LoadResult<QuerySnapshot, OrderWithRecords> {
        return try {
            val currentPage = params.key ?: query
                .get()
                .await()

            if (currentPage.size() != 0) {
                val lastDocumentSnapshot = currentPage.documents[currentPage.size() - 1]

                val nextPage = query.startAfter(lastDocumentSnapshot)
                    .get()
                    .await()

                LoadResult.Page(
                    data = currentPage.documents.map { OrderDeserializer.deserialize(it) },
                    prevKey = null,
                    nextKey = nextPage
                )
            } else {
                LoadResult.Page(
                    data = emptyList(),
                    prevKey = null,
                    nextKey = null
                )
            }

        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }

    override fun getRefreshKey(state: PagingState<QuerySnapshot, OrderWithRecords>): QuerySnapshot? {
        return null
    }
}