package com.puntogris.blint.ui.notifications

import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.puntogris.blint.R
import com.puntogris.blint.databinding.FragmentNotificationsBinding
import com.puntogris.blint.model.notifications.EmploymentResponseOwnerNotif
import com.puntogris.blint.model.notifications.AdvertisementNotification
import com.puntogris.blint.model.notifications.EmploymentRequestReceivedNotif
import com.puntogris.blint.model.notifications.EmploymentResponseEmployeeNotif
import com.puntogris.blint.ui.base.BaseFragment
import com.puntogris.blint.utils.NotificationsState
import com.puntogris.blint.utils.launchWebBrowserIntent
import com.puntogris.blint.utils.showLongSnackBarAboveFab
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.emitAll

@AndroidEntryPoint
class NotificationsFragment : BaseFragment<FragmentNotificationsBinding>(R.layout.fragment_notifications) {

    private val viewModel: NotificationsViewModel by viewModels()


    override fun initializeViews() {
        val adapter = NotificationsAdapter(
            clickListener = { notificationClickListener(it)},
            newNotificationListener = { newNotificationListener(it)})

        binding.notificationsRv.apply {
            this.adapter = adapter
            layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        }
        lifecycleScope.launchWhenCreated {
            viewModel.notificationsFetchResult.collect {
                when(it){
                    is NotificationsState.Success ->
                        adapter.updateList(it.result)
                    is NotificationsState.Error ->
                        showLongSnackBarAboveFab("Ocurrio un error buscando las notificaciones. Intente nuevamente.")
                    NotificationsState.Working.LoadFirstBatch ->
                        viewModel.getFirstBatchNotifications()
                    NotificationsState.Working.LoadMore ->
                        viewModel.getMoreNotifications()
                    NotificationsState.CollectionEmpty -> {
                        if (adapter.isListEmpty()) {
                            binding.notificationsMessage.visible()
                            binding.notificationsImage.visible()
                        }
                    }
                    is NotificationsState.OnDelete ->
                        viewModel.deleteNotification(it.id)
                    NotificationsState.Idle -> {}
                }
            }
        }

        lifecycleScope.launchWhenStarted {
            viewModel.notificationsFetchResult.emitAll(adapter.state)
        }
    }

    private fun newNotificationListener(notificationID: String){
        viewModel.changeReadStatusNotification(notificationID)
    }

    private fun notificationClickListener(notification: Any){
        when(notification){
            is AdvertisementNotification -> {
                launchWebBrowserIntent(notification.uriToShow)
            }
            is EmploymentRequestReceivedNotif -> {
            }
            is EmploymentResponseOwnerNotif -> {}
            is EmploymentResponseEmployeeNotif -> {}
        }
    }

    override fun onDestroyView() {
        binding.notificationsRv.adapter = null
        super.onDestroyView()
    }
}