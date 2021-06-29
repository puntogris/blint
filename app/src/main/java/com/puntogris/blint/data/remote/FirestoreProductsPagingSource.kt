package com.puntogris.blint.data.remote

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.QuerySnapshot
import com.puntogris.blint.model.Product
import com.puntogris.blint.model.ProductWithSuppliersCategories
import kotlinx.coroutines.tasks.await

class FirestoreProductsPagingSource(
    private val query: Query
) : PagingSource<QuerySnapshot, ProductWithSuppliersCategories>() {

    override suspend fun load(params: LoadParams<QuerySnapshot>): LoadResult<QuerySnapshot, ProductWithSuppliersCategories> {
        return try {
            val currentPage = params.key ?: query
                .get()
                .await()

            val lastDocumentSnapshot = currentPage.documents[currentPage.size() - 1]

            val nextPage = query.startAfter(lastDocumentSnapshot)
                .get()
                .await()

            LoadResult.Page(
                data = currentPage.toObjects(Product::class.java)
                    .map { ProductWithSuppliersCategories(product = it) },
                prevKey = null,
                nextKey = nextPage
            )

        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }

    override fun getRefreshKey(state: PagingState<QuerySnapshot, ProductWithSuppliersCategories>): QuerySnapshot? {
        return null
    }
}