package com.puntogris.blint.feature_store.presentation.orders.details

import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import com.puntogris.blint.feature_store.domain.model.order.Record
import com.puntogris.blint.feature_store.presentation.orders.RecordDiffCallBack

class OrdersRecordsAdapter : ListAdapter<Record, OrderRecordsViewHolder>(
    RecordDiffCallBack()
) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderRecordsViewHolder {
        return OrderRecordsViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: OrderRecordsViewHolder, position: Int) {
        holder.bind(getItem(position)!!)
    }
}
