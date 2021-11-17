package com.puntogris.blint.ui.product.categories

import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import com.puntogris.blint.diffcallback.CategoryDiffCallBack
import com.puntogris.blint.model.Category

class RemoveProductCategoryAdapter(private val clickListener: (Category) -> Unit) :
    ListAdapter<Category, RemoveProductCategoryViewHolder>(
        CategoryDiffCallBack()
    ) {

    private var categories = mutableListOf<Category>()

    init {
        submitList(categories)
    }

    fun initialCategories(list: MutableList<Category>) {
        categories = list
    }

    fun getFinalCategories() = categories

    override fun getItemCount() = categories.size

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): RemoveProductCategoryViewHolder {
        return RemoveProductCategoryViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: RemoveProductCategoryViewHolder, position: Int) {
        holder.bind(categories[position], clickListener)
    }

    fun removeCategory(category: Category) {
        categories.remove(category)
        notifyDataSetChanged()
    }

    fun addCategory(category: Category) {
        if (!categories.contains(category)) categories.add(category)
        notifyDataSetChanged()
    }
}

