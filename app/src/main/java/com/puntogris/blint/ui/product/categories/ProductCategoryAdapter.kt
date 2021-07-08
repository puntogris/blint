package com.puntogris.blint.ui.product.categories

import android.view.ViewGroup
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.puntogris.blint.diffcallback.ProductCategoryDiffCallBack
import com.puntogris.blint.model.FirestoreCategory
import com.puntogris.blint.ui.categories.CategoryViewHolder
import com.puntogris.blint.ui.notifications.SwipeToDeleteCallback

class ProductCategoryAdapter(): ListAdapter<FirestoreCategory, ProductCategoryViewHolder>(
    ProductCategoryDiffCallBack()
) {

    private var categories = mutableListOf<FirestoreCategory>()

    init {
        submitList(categories)
    }

    fun initialCategories(list:MutableList<FirestoreCategory>){
        categories = list
    }

    fun getFinalCategories() = categories

    override fun getItemCount() = categories.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductCategoryViewHolder {
        return ProductCategoryViewHolder.from(parent)
    }
    override fun onBindViewHolder(holder: ProductCategoryViewHolder, position: Int) {
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

    fun addCategory(category: FirestoreCategory){
        if (!categories.contains(category)) categories.add(category)
        notifyDataSetChanged()
    }
}

