package com.puntogris.blint.ui.orders.detailed_order

import android.content.Context
import android.view.ViewGroup
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.puntogris.blint.diffcallback.NewRecordItemDiffCallBack
import com.puntogris.blint.model.order.NewRecord
import com.puntogris.blint.utils.SwipeToDeleteCallback

class OrderProductsAdapter(
    private val context: Context,
    private val dataChangedListener: () -> (Unit),
    private val deleteListener: (NewRecord) -> Unit
) : ListAdapter<NewRecord, OrderItemViewHolder>(NewRecordItemDiffCallBack()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderItemViewHolder {
        return OrderItemViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: OrderItemViewHolder, position: Int) {
        holder.bind(getItem(position)!!, dataChangedListener)
    }

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)
        object : SwipeToDeleteCallback(context) {
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                viewHolder.bindingAdapterPosition.apply {
                    deleteListener(getItem(this))
                }
            }
        }.apply { ItemTouchHelper(this).attachToRecyclerView(recyclerView) }
    }
}
