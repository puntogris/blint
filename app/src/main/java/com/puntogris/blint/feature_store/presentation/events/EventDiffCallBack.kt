package com.puntogris.blint.feature_store.presentation.events

import androidx.recyclerview.widget.DiffUtil
import com.puntogris.blint.common.utils.types.EventUi

class EventDiffCallBack : DiffUtil.ItemCallback<EventUi>() {
    override fun areItemsTheSame(oldItem: EventUi, newItem: EventUi): Boolean {
        return (oldItem is EventUi.EventItem && newItem is EventUi.EventItem &&
                oldItem.event.eventId == newItem.event.eventId) ||
                (oldItem is EventUi.SeparatorItem && newItem is EventUi.SeparatorItem &&
                        oldItem.date == newItem.date)
    }

    override fun areContentsTheSame(oldItem: EventUi, newItem: EventUi): Boolean {
        return oldItem == newItem
    }
}
