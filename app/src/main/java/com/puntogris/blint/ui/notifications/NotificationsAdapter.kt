package com.puntogris.blint.ui.notifications

import android.view.ViewGroup
import android.widget.AbsListView
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.puntogris.blint.model.Notification
import com.puntogris.blint.utils.NotificationsState
import kotlinx.coroutines.flow.MutableStateFlow
import javax.inject.Inject


class NotificationsAdapter @Inject constructor(
    private val newNotificationListener: (String) -> Unit,
    private val clickListener: (Notification) -> Unit
):  RecyclerView.Adapter<NotificationViewHolder>() {

    private var list = mutableListOf<Notification>()
    private var isScrolling = false
    val state = MutableStateFlow<NotificationsState>(NotificationsState.Idle)

    fun updateList(newList:MutableList<Notification>){
        list = newList
        notifyDataSetChanged()
    }

    fun isListEmpty() = itemCount == 0

    override fun onBindViewHolder(holder: NotificationViewHolder, position: Int) {
        list[position].let {
            if (!it.wasRead) newNotificationListener.invoke(it.id)
            holder.bind(it, clickListener)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NotificationViewHolder {
        return NotificationViewHolder.from(parent)
    }

    override fun getItemCount() = list.size

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)

        object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                if (newState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL) isScrolling = true
            }

            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)

                val rv = recyclerView.layoutManager as LinearLayoutManager
                val firstItemVisible = rv.findFirstVisibleItemPosition()
                val visibleItemCount = rv.childCount
                val totalItemCount = rv.itemCount

                if (isScrolling &&
                    (firstItemVisible + visibleItemCount == totalItemCount) &&
                    state.value is NotificationsState.Idle)
                {
                    isScrolling = false
                    state.value = NotificationsState.Working.LoadMore
                }
            }
        }.apply { recyclerView.addOnScrollListener(this)}

        object : SwipeToDeleteCallback() {
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                viewHolder.bindingAdapterPosition.apply {
                    state.value = NotificationsState.OnDelete(list[this].id)
                    list.removeAt(this)
                    notifyItemRemoved(this)
                }
                if (list.isEmpty()) state.value = NotificationsState.CollectionEmpty
            }
        }.apply { ItemTouchHelper(this).attachToRecyclerView(recyclerView) }
    }
}
