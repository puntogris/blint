package com.puntogris.blint.ui.categories

import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import com.puntogris.blint.diffcallback.CategoryDiffCallBack
import com.puntogris.blint.model.Category

class CategoriesAdapter(private val clickListener: () -> Unit): ListAdapter<Category, CategoryViewHolder>(
    CategoryDiffCallBack()
) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryViewHolder {
        return CategoryViewHolder.from(parent)
    }
    override fun onBindViewHolder(holder: CategoryViewHolder, position: Int) {
        holder.bind(getItem(position), clickListener)
    }

    fun clearSelected(){
        currentList.forEach {
            it.selected = false
        }
        notifyDataSetChanged()
    }

    fun getAmountChecked() = currentList.count { it.selected }

    fun getSelectedCategories() = currentList.filter { it.selected }

}

