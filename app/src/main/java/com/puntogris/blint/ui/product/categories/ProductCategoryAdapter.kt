package com.puntogris.blint.ui.product.categories

import android.view.ViewGroup
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.puntogris.blint.diffcallback.CategoryDiffCallBack
import com.puntogris.blint.model.Category
import com.puntogris.blint.ui.categories.CategoryViewHolder
import com.puntogris.blint.ui.notifications.SwipeToDeleteCallback

class ProductCategoryAdapter: ListAdapter<Category, CategoryViewHolder>(
    CategoryDiffCallBack()
) {

    private var categories = mutableListOf<Category>()

    init {
        submitList(categories)
    }

    fun initialCategories(list:MutableList<Category>){
        categories = list
    }

    fun getFinalCategories() = categories

    override fun getItemCount() = categories.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryViewHolder {
        return CategoryViewHolder.from(parent)
    }
    override fun onBindViewHolder(holder: CategoryViewHolder, position: Int) {
        holder.bind(categories[position])
    }

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)
        object : SwipeToDeleteCallback() {
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                viewHolder.bindingAdapterPosition.apply {
                    categories.removeAt(this)
                    notifyItemRemoved(this)
                }
            }
        }.apply { ItemTouchHelper(this).attachToRecyclerView(recyclerView) }
    }

    fun addCategory(category: Category){
        if (!categories.contains(category)) categories.add(category)
        notifyDataSetChanged()
    }
}

