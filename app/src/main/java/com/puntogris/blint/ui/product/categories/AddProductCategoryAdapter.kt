package com.puntogris.blint.ui.product.categories

import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import com.puntogris.blint.diffcallback.CategoryDiffCallBack
import com.puntogris.blint.model.Category

class AddProductCategoryAdapter(private val clickListener: (Category)-> Unit): ListAdapter<Category, AddProductCategoryViewHolder>(
    CategoryDiffCallBack()
) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AddProductCategoryViewHolder {
        return AddProductCategoryViewHolder.from(parent)
    }
    override fun onBindViewHolder(holder: AddProductCategoryViewHolder, position: Int) {
        holder.bind(getItem(position), clickListener)
    }
}

