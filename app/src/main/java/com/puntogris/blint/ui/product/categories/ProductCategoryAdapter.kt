package com.puntogris.blint.ui.product.categories

import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import com.puntogris.blint.diffcallback.CategoryDiffCallBack
import com.puntogris.blint.model.Category

class ProductCategoryAdapter(private val clickListener: (Category) -> Unit) :
    PagingDataAdapter<Category, ProductCategoryViewHolder>(
        CategoryDiffCallBack()
    ) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductCategoryViewHolder {
        return ProductCategoryViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: ProductCategoryViewHolder, position: Int) {
        holder.bind(getItem(position)!!, clickListener)
    }
}

