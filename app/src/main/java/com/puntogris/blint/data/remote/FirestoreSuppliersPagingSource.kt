package com.puntogris.blint.data.remote

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.QuerySnapshot
import com.puntogris.blint.model.Product
import com.puntogris.blint.model.Supplier
import kotlinx.coroutines.tasks.await

class FirestoreSuppliersPagingSource(
    private val query: Query
) : PagingSource<QuerySnapshot, Supplier>() {

    override suspend fun load(params: LoadParams<QuerySnapshot>): LoadResult<QuerySnapshot, Supplier> {
        return try {
            // Step 1
            val currentPage = params.key ?: query
                .get()
                .await()

            // Step 2
            val lastDocumentSnapshot = currentPage.documents[currentPage.size() - 1]

            // Step 3
            val nextPage = query.startAfter(lastDocumentSnapshot)
                .get()
                .await()

            // Step 4
            LoadResult.Page(
                data = currentPage.toObjects(Supplier::class.java),
                prevKey = null,
                nextKey = nextPage
            )
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }

    override fun getRefreshKey(state: PagingState<QuerySnapshot, Supplier>): QuerySnapshot? {
        return null
    }
}