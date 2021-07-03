package com.puntogris.blint.data.remote

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.QuerySnapshot
import com.puntogris.blint.model.Client
import com.puntogris.blint.model.Product
import com.puntogris.blint.model.ProductWithSuppliersCategories
import kotlinx.coroutines.tasks.await

class FirestoreClientsPagingSource(
    private val query: Query
) : PagingSource<QuerySnapshot, Client>() {

    override suspend fun load(params: LoadParams<QuerySnapshot>): LoadResult<QuerySnapshot, Client> {
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
                    data = currentPage.toObjects(Client::class.java),
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

    override fun getRefreshKey(state: PagingState<QuerySnapshot, Client>): QuerySnapshot? {
        return null
    }
}