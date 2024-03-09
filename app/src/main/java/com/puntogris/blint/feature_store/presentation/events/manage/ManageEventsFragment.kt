package com.puntogris.blint.feature_store.presentation.events.manage

import androidx.fragment.app.setFragmentResultListener
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.puntogris.blint.R
import com.puntogris.blint.common.presentation.base.BaseFragment
import com.puntogris.blint.common.utils.Keys
import com.puntogris.blint.common.utils.launchAndRepeatWithViewLifecycle
import com.puntogris.blint.common.utils.registerToolbarBackButton
import com.puntogris.blint.common.utils.showEmptyUiOnEmptyAdapter
import com.puntogris.blint.common.utils.types.EventStatus
import com.puntogris.blint.databinding.FragmentManageEventsBinding
import com.puntogris.blint.feature_store.domain.model.Event
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ManageEventsFragment :
    BaseFragment<FragmentManageEventsBinding>(R.layout.fragment_manage_events) {

    private val viewModel: ManageEventsViewModel by viewModels()

    override fun initializeViews() {
        setupToolbar()
        setupEventsFilter()
        setupEventsAdapter()
    }

    private fun setupToolbar() {
        with(binding.manageEventsToolbar) {
            registerToolbarBackButton(this)
            setOnMenuItemClickListener {
                if (it.itemId == R.id.action_menu_item_add) {
                    findNavController().navigate(R.id.createEventFragment)
                }
                true
            }
        }
    }

    private fun setupEventsFilter() {
        binding.manageEventsFilter.addOnButtonCheckedListener { _, checkedId, _ ->
            when (checkedId) {
                R.id.manage_events_filter_all_button -> viewModel.setFilter(EventStatus.All)
                R.id.manage_events_filter_pending_button -> viewModel.setFilter(EventStatus.Pending)
                R.id.manage_events_finished_all_button -> viewModel.setFilter(EventStatus.Finished)
            }
        }
    }

    private fun setupEventsAdapter() {
        val adapter = ManageEventsAdapter(::onEventClicked)
        binding.manageEventsRecyclerView.adapter = adapter
        registerEventUpdatedListener(adapter)
        subscribeUi(adapter)
    }

    private fun subscribeUi(adapter: ManageEventsAdapter) {
        launchAndRepeatWithViewLifecycle {
            viewModel.eventsFlow.collect {
                adapter.submitData(it)
            }
        }
        launchAndRepeatWithViewLifecycle {
            showEmptyUiOnEmptyAdapter(adapter, binding.emptyUi)
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

    override fun onDestroyView() {
        binding.manageEventsRecyclerView.adapter = null
        super.onDestroyView()
    }
}
