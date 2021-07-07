package com.puntogris.blint.ui.notifications

import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.puntogris.blint.R
import com.puntogris.blint.databinding.FragmentNotificationsBinding
import com.puntogris.blint.model.Notification
import com.puntogris.blint.ui.base.BaseFragment
import com.puntogris.blint.utils.*
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.emitAll

@AndroidEntryPoint
class NotificationsFragment : BaseFragment<FragmentNotificationsBinding>(R.layout.fragment_notifications) {

    private val viewModel: NotificationsViewModel by viewModels()

    override fun initializeViews() {
        setUpUi()
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
                        showLongSnackBarAboveFab(getString(R.string.snack_error_connection_server_try_later))
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

        launchAndRepeatWithViewLifecycle {
            viewModel.notificationsFetchResult.emitAll(adapter.state)
        }
    }

    private fun newNotificationListener(notificationID: String){
        viewModel.changeReadStatusNotification(notificationID)
    }

    private fun notificationClickListener(notification: Notification){
        try {
            launchWebBrowserIntent(notification.navigationUri)
        }catch (e:Exception){
            showSnackBarVisibilityAppBar(getString(R.string.snack_ups_visit_blint))
        }
    }

    override fun onDestroyView() {
        binding.notificationsRv.adapter = null
        super.onDestroyView()
    }
}