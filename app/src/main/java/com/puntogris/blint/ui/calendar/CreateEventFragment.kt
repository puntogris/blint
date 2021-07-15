package com.puntogris.blint.ui.calendar

import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.maxkeppeler.sheets.calendar.CalendarSheet
import com.maxkeppeler.sheets.calendar.SelectionMode
import com.puntogris.blint.R
import com.puntogris.blint.databinding.FragmentCreateEventBinding
import com.puntogris.blint.ui.base.BaseFragment
import com.puntogris.blint.utils.*
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.util.*

@AndroidEntryPoint
class CreateEventFragment : BaseFragment<FragmentCreateEventBinding>(R.layout.fragment_create_event) {

    private val viewModel:CalendarViewModel by viewModels()

    override fun initializeViews() {
        binding.fragment = this

        getParentFab().apply {
            changeIconFromDrawable(R.drawable.ic_baseline_save_24)
            setOnClickListener {
                lifecycleScope.launch {
                    when(viewModel.createEvent(
                        title = binding.eventTitleText.getString(),
                        message = binding.eventMessageText.getString())){
                        SimpleResult.Failure ->
                            showLongSnackBarAboveFab(context.getString(R.string.snack_create_event_error))
                        SimpleResult.Success -> {
                            showLongSnackBarAboveFab(context.getString(R.string.snack_create_event_success))
                            findNavController().navigateUp()
                        }
                    }
                }
            }
        }
    }

    fun onSelectDateClicked(){
        CalendarSheet().build(requireContext()){
            title(this@CreateEventFragment.getString(R.string.ask_event_date))
            selectionMode(SelectionMode.DATE)
            onPositive { dateStart: Calendar, _: Calendar? ->
                viewModel.updateEventDate(dateStart.timeInMillis)
                binding.eventDate.text = dateStart.time.getDateFormattedString()
            }

        }.show(parentFragmentManager, "")
    }
}