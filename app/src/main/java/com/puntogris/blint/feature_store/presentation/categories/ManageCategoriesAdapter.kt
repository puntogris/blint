package com.puntogris.blint.feature_store.presentation.categories

import android.content.Context
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.puntogris.blint.common.utils.SwipeToDeleteCallback
import com.puntogris.blint.feature_store.domain.model.Category

class ManageCategoriesAdapter(
    private val context: Context,
    private val deleteListener: (String) -> Unit
) : PagingDataAdapter<Category, ManageCategoriesViewHolder>(CategoryDiffCallBack()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ManageCategoriesViewHolder {
        return ManageCategoriesViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: ManageCategoriesViewHolder, position: Int) {
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
