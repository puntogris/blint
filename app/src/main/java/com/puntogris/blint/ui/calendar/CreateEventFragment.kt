package com.puntogris.blint.ui.calendar

import com.maxkeppeler.sheets.calendar.CalendarSheet
import com.maxkeppeler.sheets.calendar.SelectionMode
import com.puntogris.blint.R
import com.puntogris.blint.databinding.FragmentCreateEventBinding
import com.puntogris.blint.ui.base.BaseFragment
import com.puntogris.blint.utils.changeIconFromDrawable
import com.puntogris.blint.utils.getParentFab
import dagger.hilt.android.AndroidEntryPoint
import java.util.*

@AndroidEntryPoint
class CreateEventFragment : BaseFragment<FragmentCreateEventBinding>(R.layout.fragment_create_event) {

    override fun initializeViews() {
        binding.fragment = this

        getParentFab().apply {
            changeIconFromDrawable(R.drawable.ic_baseline_save_24)
            setOnClickListener {

            }
        }
    }

    fun onSelectDateClicked(){
        CalendarSheet().build(requireContext()){
            title("Para que fecha lo queres programar?")
            selectionMode(SelectionMode.DATE)
            onPositive { dateStart: Calendar, dateEnd: Calendar? ->
                println(dateStart.timeInMillis)
            }

        }.show(parentFragmentManager, "")
    }
}