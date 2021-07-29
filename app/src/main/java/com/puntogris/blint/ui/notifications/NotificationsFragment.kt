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
        UiInterface.register()
        val adapter = NotificationsAdapter(
            requireContext(),
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
                    is NotificationsState.Success -> {
                        adapter.updateList(it.result)
                        binding.progressBar2.gone()
                    }
                    is NotificationsState.Error -> {
                        binding.progressBar2.gone()
                        UiInterface.showSnackBar(getString(R.string.snack_error_connection_server_try_later))
                    }
                    NotificationsState.Working.LoadFirstBatch ->
                        viewModel.getFirstBatchNotifications()
                    NotificationsState.Working.LoadMore ->
                        viewModel.getMoreNotifications()
                    NotificationsState.CollectionEmpty -> {
                        binding.progressBar2.gone()
                        if (adapter.isListEmpty()) {
                            binding.noNotificationsStub.viewStub?.inflate()
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
        launchWebBrowserIntent(notification.navigationUri)
    }

    override fun onDestroyView() {
        binding.notificationsRv.adapter = null
        super.onDestroyView()
    }
}