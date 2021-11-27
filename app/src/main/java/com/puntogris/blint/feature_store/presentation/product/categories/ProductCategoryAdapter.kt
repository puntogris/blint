package com.puntogris.blint.feature_store.presentation.product.categories

import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import com.puntogris.blint.feature_store.domain.model.CheckableCategory
import com.puntogris.blint.feature_store.presentation.product.CheckableCategoryDiffCallBack

class ProductCategoryAdapter(private val clickListener: (CheckableCategory) -> Unit) :
    PagingDataAdapter<CheckableCategory, ProductCategoryViewHolder>(
        CheckableCategoryDiffCallBack()
    ) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductCategoryViewHolder {
        return ProductCategoryViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: ProductCategoryViewHolder, position: Int) {
        holder.bind(getItem(position)!!, clickListener)
    }
}

