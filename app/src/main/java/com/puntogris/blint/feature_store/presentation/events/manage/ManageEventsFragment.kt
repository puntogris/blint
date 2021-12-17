package com.puntogris.blint.feature_store.presentation.events.manage

import android.view.Menu
import android.view.MenuItem
import androidx.fragment.app.setFragmentResultListener
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.puntogris.blint.R
import com.puntogris.blint.common.presentation.base.BaseFragmentOptions
import com.puntogris.blint.common.utils.Keys
import com.puntogris.blint.common.utils.UiInterface
import com.puntogris.blint.common.utils.launchAndRepeatWithViewLifecycle
import com.puntogris.blint.common.utils.types.EventStatus
import com.puntogris.blint.databinding.FragmentManageEventsBinding
import com.puntogris.blint.feature_store.domain.model.Event
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect

@AndroidEntryPoint
class ManageEventsFragment :
    BaseFragmentOptions<FragmentManageEventsBinding>(R.layout.fragment_manage_events) {

    private val viewModel: ManageEventsViewModel by viewModels()

    override fun initializeViews() {
        UiInterface.registerUi()
        setupEventsFilter()
        setupEventsAdapter()
    }

    private fun setupEventsFilter() {
        binding.eventsFilter.addOnButtonCheckedListener { _, checkedId, _ ->
            when (checkedId) {
                R.id.filterAllEventsButton -> viewModel.setFilter(EventStatus.All)
                R.id.filterPendingEventsButton -> viewModel.setFilter(EventStatus.Pending)
                R.id.filterFinishedEventsButton -> viewModel.setFilter(EventStatus.Finished)
            }
        }
    }

    private fun setupEventsAdapter() {
        ManageEventsAdapter(::onEventClicked).let {
            binding.recyclerView.adapter = it
            registerEventUpdatedListener(it)
            subscribeUi(it)
        }
    }

    private fun subscribeUi(adapter: ManageEventsAdapter) {
        launchAndRepeatWithViewLifecycle {
            viewModel.eventsFlow.collect {
                adapter.submitData(it)
            }
        }
    }

    private fun registerEventUpdatedListener(adapter: ManageEventsAdapter) {
        setFragmentResultListener(Keys.EVENT_FILTER_KEY) { _, bundle ->
            val position = bundle.getInt(Keys.EVENT_POSITION_KEY)
            adapter.notifyItemChanged(position)
        }
    }

    private fun onEventClicked(event: Event, position: Int) {
        val action =
            ManageEventsFragmentDirections.actionCalendarFragmentToEventInfoBottomSheet(
                event,
                position
            )
        findNavController().navigate(action)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return if (item.itemId == R.id.action_menu_add) {
            findNavController().navigate(R.id.createEventFragment)
            true
        } else super.onOptionsItemSelected(item)
    }

    override fun setUpMenuOptions(menu: Menu) {
        menu.findItem(R.id.action_menu_add).isVisible = true
    }

    override fun onDestroyView() {
        binding.recyclerView.adapter = null
        super.onDestroyView()
    }
}

