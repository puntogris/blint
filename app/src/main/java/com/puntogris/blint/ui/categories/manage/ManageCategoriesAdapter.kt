package com.puntogris.blint.ui.categories.manage

import android.content.Context
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.puntogris.blint.diffcallback.CategoryDiffCallBack
import com.puntogris.blint.model.Category
import com.puntogris.blint.ui.categories.CategoryViewHolder
import com.puntogris.blint.utils.SwipeToDeleteCallback

class ManageCategoriesAdapter(
    private val context: Context,
    private val deleteListener: (String) -> Unit
) : PagingDataAdapter<Category, CategoryViewHolder>(CategoryDiffCallBack()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryViewHolder {
        return CategoryViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: CategoryViewHolder, position: Int) {
        holder.bind(getItem(position) ?: return)
    }

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)

        object : SwipeToDeleteCallback(context) {
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val item = getItem(viewHolder.bindingAdapterPosition)?.categoryName
                deleteListener.invoke(item ?: return)
            }
        }.apply { ItemTouchHelper(this).attachToRecyclerView(recyclerView) }
    }
}

