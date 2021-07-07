package com.puntogris.blint.data.remote

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.QuerySnapshot
import com.puntogris.blint.data.remote.deserializers.ProductDeserializer
import com.puntogris.blint.data.remote.deserializers.ProductQueryTransformation
import com.puntogris.blint.model.*
import kotlinx.coroutines.tasks.await

class FirestoreProductsPagingSource(
    private val query: Query
) : PagingSource<QuerySnapshot, ProductWithSuppliersCategories>() {

    override suspend fun load(params: LoadParams<QuerySnapshot>): LoadResult<QuerySnapshot, ProductWithSuppliersCategories> {
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
                    data = ProductQueryTransformation.transform(currentPage),
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
            println(e.localizedMessage)
            LoadResult.Error(e)
        }
    }

    override fun getRefreshKey(state: PagingState<QuerySnapshot, ProductWithSuppliersCategories>): QuerySnapshot? {
        return null
    }

}