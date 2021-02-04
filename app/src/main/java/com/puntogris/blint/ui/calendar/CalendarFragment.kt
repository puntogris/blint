package com.puntogris.blint.ui.calendar

import android.view.Menu
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.puntogris.blint.R
import com.puntogris.blint.databinding.FragmentCalendarBinding
import com.puntogris.blint.ui.base.BaseFragmentOptions
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.collectLatest

@ExperimentalCoroutinesApi
@AndroidEntryPoint
class CalendarFragment : BaseFragmentOptions<FragmentCalendarBinding>(R.layout.fragment_calendar) {

    private val viewModel: CalendarViewModel by viewModels()
    private lateinit var calendarEventsAdapter:CalendarEventsAdapter
    override fun initializeViews() {

        calendarEventsAdapter = CalendarEventsAdapter { onEventClicked(it) }
        binding.recyclerView.adapter = calendarEventsAdapter
        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())

        lifecycleScope.launchWhenStarted {
            viewModel.events.collectLatest {
                calendarEventsAdapter.submitData(it)
            }
        }

        val items = listOf("Todos", "Pendientes", "Finalizados")
        val adapter = ArrayAdapter(requireContext(), R.layout.dropdown_item_list, items)
        (binding.calendarFilter.editText as? AutoCompleteTextView)?.setAdapter(adapter)

        binding.calendarFilterText.setOnItemClickListener { _, _, i, _ ->
            when(i){
                0 -> viewModel.setAllFilter()
                1 -> viewModel.setPendingFilter()
                2 -> viewModel.setFinishedFilter()
            }
        }

        findNavController().currentBackStackEntry?.savedStateHandle?.getLiveData<Boolean>("dismiss_key")?.observe(
            viewLifecycleOwner) {
            if (it) calendarEventsAdapter.notifyDataSetChanged()
        }
    }


    private fun onEventClicked(event:com.puntogris.blint.model.Event){
        val action = CalendarFragmentDirections.actionCalendarFragmentToEventInfoBottomSheet(event)
        findNavController().navigate(action)
    }

    override fun onNewEventClicked() {
        findNavController().navigate(R.id.createEventFragment)
    }

    override fun setUpMenuOptions(menu: Menu) {
        menu.findItem(R.id.calendarFragmentMenu).isVisible = true
    }

    override fun onDestroyView() {
        binding.recyclerView.adapter = null
        super.onDestroyView()
    }

}

