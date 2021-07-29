package com.puntogris.blint.ui.categories.manage

import android.content.Context
import android.view.ViewGroup
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.puntogris.blint.diffcallback.CategoryDiffCallBack
import com.puntogris.blint.model.Category
import com.puntogris.blint.ui.categories.CategoryViewHolder
import com.puntogris.blint.utils.SwipeToDeleteCallback

class ManageCategoriesAdapter(private val context: Context, private val deleteListener:(String) -> Unit): ListAdapter<Category, CategoryViewHolder>(
    CategoryDiffCallBack()
) {

    private var list = mutableListOf<Category>()

    fun updateList(list:List<Category>){
        this.list = list.toMutableList()
        notifyDataSetChanged()
    }

    fun addCategory(category: Category){
        list.add(category)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryViewHolder {
        return CategoryViewHolder.from(parent)
    }
    override fun onBindViewHolder(holder: CategoryViewHolder, position: Int) {
        holder.bind(list[position])
    }

    override fun getItemCount() = list.size

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)

        object : SwipeToDeleteCallback(context) {
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                viewHolder.bindingAdapterPosition.apply {
                    deleteListener.invoke(list[this].categoryName)
                    list.removeAt(this)
                    notifyItemRemoved(this)
                }
            }
        }.apply { ItemTouchHelper(this).attachToRecyclerView(recyclerView) }
    }
}

