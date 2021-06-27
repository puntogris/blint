package com.puntogris.blint.data.remote

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.QuerySnapshot
import com.puntogris.blint.model.Event
import kotlinx.coroutines.tasks.await

class FirestoreEventsPagingSource(
    private val query: Query
    ) : PagingSource<QuerySnapshot, Event>() {

    override suspend fun load(params: LoadParams<QuerySnapshot>): LoadResult<QuerySnapshot, Event> {
        return try {
            val currentPage = params.key ?: query
                .get()
                .await()

            val lastDocumentSnapshot = currentPage.documents[currentPage.size() - 1]

            val nextPage = query.startAfter(lastDocumentSnapshot)
                .get()
                .await()

                LoadResult.Page(
                    data = currentPage.toObjects(Event::class.java),
                    prevKey = null,
                    nextKey = nextPage
                )

        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }

    override fun getRefreshKey(state: PagingState<QuerySnapshot, Event>): QuerySnapshot? {
        return null
    }

}