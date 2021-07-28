package com.puntogris.blint.ui.product.categories

import android.view.ViewGroup
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.puntogris.blint.diffcallback.CategoryDiffCallBack
import com.puntogris.blint.model.Category
import com.puntogris.blint.ui.categories.CategoryViewHolder
import com.puntogris.blint.ui.notifications.SwipeToDeleteCallback

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

