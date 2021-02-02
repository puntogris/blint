package com.puntogris.blint.ui.calendar

import android.graphics.Color
import android.view.Menu
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.github.sundeepk.compactcalendarview.CompactCalendarView
import com.github.sundeepk.compactcalendarview.domain.Event
import com.google.firebase.Timestamp
import com.maxkeppeler.sheets.core.views.SheetInputEditText
import com.maxkeppeler.sheets.input.InputSheet
import com.maxkeppeler.sheets.input.type.InputEditText
import com.puntogris.blint.R
import com.puntogris.blint.databinding.FragmentCalendarBinding
import com.puntogris.blint.ui.base.BaseFragment
import com.puntogris.blint.ui.base.BaseFragmentOptions
import com.puntogris.blint.ui.product.ManageProductsAdapter
import com.puntogris.blint.utils.getMonthWithYear
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import java.util.*
import kotlin.time.milliseconds

@AndroidEntryPoint
class CalendarFragment : BaseFragmentOptions<FragmentCalendarBinding>(R.layout.fragment_calendar) {

    private val viewModel:CalendarViewModel by viewModels()

    override fun initializeViews() {
        binding.calendarMonth.text = Date().getMonthWithYear()
        binding.calendarView.addEvent(Event(Color.RED, 1612478445000))

        val manageProductsAdapter = CalendarEventsAdapter { onEventClicked(it) }
        binding.recyclerView.adapter = manageProductsAdapter
        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())

        lifecycleScope.launchWhenStarted {
            viewModel.getEvents().collect {
                manageProductsAdapter.submitData(it)
            }
        }


        binding.calendarView.setListener(object :CompactCalendarView.CompactCalendarViewListener{
            override fun onDayClick(dateClicked: Date?) {

            }

            override fun onMonthScroll(firstDayOfNewMonth: Date?) {
                binding.calendarMonth.text = firstDayOfNewMonth?.getMonthWithYear()
            }
        })
    }

    private fun onEventClicked(event:com.puntogris.blint.model.Event){

    }

    override fun onNewEventClicked() {
        findNavController().navigate(R.id.createEventFragment)
    }

    override fun setUpMenuOptions(menu: Menu) {
        menu.findItem(R.id.calendarFragmentMenu).isVisible = true
    }

}
