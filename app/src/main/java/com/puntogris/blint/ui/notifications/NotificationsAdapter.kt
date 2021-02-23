package com.puntogris.blint.ui.notifications

import android.view.ViewGroup
import android.widget.AbsListView
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.puntogris.blint.ui.notifications.view_holders.*
import com.puntogris.blint.utils.Constants.ADVERTISEMENT_NOTIFICATION
import com.puntogris.blint.utils.Constants.EMPLOYMENT_REQUEST_EMPLOYEE_RESPONSE_NOTIFICATION
import com.puntogris.blint.utils.Constants.EMPLOYMENT_REQUEST_OWNER_RESPONSE_NOTIFICATION
import com.puntogris.blint.utils.Constants.EMPLOYMENT_REQUEST_RECEIVED_NOTIFICATION
import com.puntogris.blint.utils.Constants.EMPLOYMENT_REQUEST_SENT_NOTIFICATION
import com.puntogris.blint.utils.NotificationType
import com.puntogris.blint.utils.NotificationsState
import kotlinx.coroutines.flow.MutableStateFlow
import javax.inject.Inject

class NotificationsAdapter @Inject constructor(private val newNotificationListener: (String) -> Unit, private val clickListener: (Any) -> Unit):  RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var list = mutableListOf<NotificationType>()
    private var isScrolling = false
    val state =
        MutableStateFlow<NotificationsState>(NotificationsState.Idle)

    fun updateList(newList:MutableList<NotificationType>){
        list = newList
        notifyDataSetChanged()
    }

    fun isListEmpty() = itemCount == 0

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (val data = list[position]) {
            is NotificationType.Advertisement ->{
                data.notification.apply {
                    (holder as AdvertisementViewHolder).bind(this, clickListener)
                    if (!wasRead) newNotificationListener.invoke(id)
                }
            }
            is NotificationType.OwnerRequestResponse ->{
                data.notification.apply {
                    (holder as EmploymentOwnerResponseNotifVH).bind(this, clickListener)
                    if (!wasRead) newNotificationListener.invoke(id)
                }
            }
            is NotificationType.SentRequest ->{
                data.notification.apply {
                    (holder as EmploymentRequestSentNotifVH).bind(this, clickListener)
                    if (!wasRead) newNotificationListener.invoke(id)
                }
            }
            is NotificationType.ReceivedRequest ->{
                data.notification.apply {
                    (holder as EmploymentRequestReceivedNotifVH).bind(this, clickListener)
                    if (!wasRead) newNotificationListener.invoke(id)
                }
            }
            is NotificationType.EmployeeRequestResponse ->{
                data.notification.apply {
                    (holder as EmploymentEmployeeResponseNotifVH).bind(this, clickListener)
                    if (!wasRead) newNotificationListener.invoke(id)
                }
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when (list[position]) {
            is NotificationType.Advertisement -> ADVERTISEMENT_NOTIFICATION
            is NotificationType.OwnerRequestResponse -> EMPLOYMENT_REQUEST_OWNER_RESPONSE_NOTIFICATION
            is NotificationType.SentRequest -> EMPLOYMENT_REQUEST_SENT_NOTIFICATION
            is NotificationType.ReceivedRequest -> EMPLOYMENT_REQUEST_RECEIVED_NOTIFICATION
            is NotificationType.EmployeeRequestResponse -> EMPLOYMENT_REQUEST_EMPLOYEE_RESPONSE_NOTIFICATION
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            ADVERTISEMENT_NOTIFICATION -> AdvertisementViewHolder.from(parent)
            EMPLOYMENT_REQUEST_OWNER_RESPONSE_NOTIFICATION -> EmploymentOwnerResponseNotifVH.from(parent)
            EMPLOYMENT_REQUEST_SENT_NOTIFICATION -> EmploymentRequestSentNotifVH.from(parent)
            EMPLOYMENT_REQUEST_RECEIVED_NOTIFICATION -> EmploymentRequestReceivedNotifVH.from(parent)
            else -> EmploymentEmployeeResponseNotifVH.from(parent)
        }
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
                    when (val data = list[this]) {
                        is NotificationType.Advertisement -> data.notification.id
                        is NotificationType.OwnerRequestResponse -> data.notification.id
                        is NotificationType.EmployeeRequestResponse -> data.notification.id
                        is NotificationType.ReceivedRequest -> data.notification.id
                        is NotificationType.SentRequest -> data.notification.id
                    }.also { state.value = NotificationsState.OnDelete(it)}
                    list.removeAt(this)
                    notifyItemRemoved(this)
                }
                if (list.isEmpty()) state.value = NotificationsState.CollectionEmpty
            }
        }.apply { ItemTouchHelper(this).attachToRecyclerView(recyclerView) }
    }
}
