package com.puntogris.blint.data.remote

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.QuerySnapshot
import com.puntogris.blint.model.Product
import com.puntogris.blint.model.Record
import kotlinx.coroutines.tasks.await

class FirestoreRecordsPagingSource(
    private val query: Query
) : PagingSource<QuerySnapshot, Record>() {

    override suspend fun load(params: LoadParams<QuerySnapshot>): LoadResult<QuerySnapshot, Record> {
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
                data = currentPage.toObjects(Record::class.java),
                prevKey = null,
                nextKey = nextPage
            )
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }

    override fun getRefreshKey(state: PagingState<QuerySnapshot, Record>): QuerySnapshot? {
        return null
    }
}