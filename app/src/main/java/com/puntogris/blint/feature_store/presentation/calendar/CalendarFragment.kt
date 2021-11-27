package com.puntogris.blint.feature_store.presentation.calendar

import android.view.Menu
import android.view.MenuItem
import android.widget.ArrayAdapter
import androidx.fragment.app.setFragmentResultListener
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.puntogris.blint.R
import com.puntogris.blint.common.presentation.base.BaseFragmentOptions
import com.puntogris.blint.common.utils.Constants
import com.puntogris.blint.common.utils.UiInterface
import com.puntogris.blint.common.utils.types.EventStatus
import com.puntogris.blint.databinding.FragmentCalendarBinding
import com.puntogris.blint.feature_store.domain.model.Event
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CalendarFragment : BaseFragmentOptions<FragmentCalendarBinding>(R.layout.fragment_calendar) {

    private val viewModel: CalendarViewModel by viewModels()

    override fun initializeViews() {
        UiInterface.registerUi(showFab = true) {
            findNavController().navigate(R.id.createEventFragment)
        }
        setupEventsFilter()
        setupEventsAdapter()
    }

    private fun setupEventsFilter() {
        val items = resources.getStringArray(R.array.filter_event_status)
        val adapter = ArrayAdapter(requireContext(), R.layout.dropdown_item_list, items)

        binding.calendarFilter.apply {
            setAdapter(adapter)
            setOnItemClickListener { _, _, i, _ ->
                enumValues<EventStatus>().getOrNull(i)?.let {
                    viewModel.updateEventStatusFilter(it)
                }
            }
        }
    }

    private fun setupEventsAdapter() {
        CalendarEventsAdapter(::onEventClicked).let {
            binding.recyclerView.adapter = it
            registerEventUpdatedListener(it)
            subscribeUi(it)
        }
    }

    private fun subscribeUi(adapter: CalendarEventsAdapter) {
        viewModel.eventsLiveData.observe(viewLifecycleOwner) {
            adapter.submitData(viewLifecycleOwner.lifecycle, it)
        }
    }

    private fun registerEventUpdatedListener(adapter: CalendarEventsAdapter) {
        setFragmentResultListener(Constants.EVENT_FILTER_KEY) { _, bundle ->
            val position = bundle.getInt(Constants.EVENT_POSITION_KEY)
            adapter.notifyItemChanged(position)
        }
    }

    private fun onEventClicked(event: Event, position: Int) {
        val action =
            CalendarFragmentDirections.actionCalendarFragmentToEventInfoBottomSheet(event, position)
        findNavController().navigate(action)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return if (item.itemId == R.id.newEvent) {
            findNavController().navigate(R.id.createEventFragment)
            true
        } else super.onOptionsItemSelected(item)
    }

    override fun setUpMenuOptions(menu: Menu) {
        menu.findItem(R.id.calendarFragmentMenu).isVisible = true
    }

    override fun onDestroyView() {
        binding.recyclerView.adapter = null
        super.onDestroyView()
    }
}

