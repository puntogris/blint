package com.puntogris.blint.feature_store.presentation.calendar.new_event

import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.maxkeppeler.sheets.calendar.CalendarSheet
import com.maxkeppeler.sheets.calendar.SelectionMode
import com.puntogris.blint.R
import com.puntogris.blint.common.presentation.base.BaseFragment
import com.puntogris.blint.common.utils.*
import com.puntogris.blint.common.utils.types.SimpleResult
import com.puntogris.blint.databinding.FragmentCreateEventBinding
import com.puntogris.blint.feature_store.presentation.calendar.CalendarViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.util.*

@AndroidEntryPoint
class CreateEventFragment :
    BaseFragment<FragmentCreateEventBinding>(R.layout.fragment_create_event) {

    private val viewModel: CreateEventViewModel by viewModels()

    override fun initializeViews() {
        binding.fragment = this
        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner

        UiInterface.registerUi(showFab = true, fabIcon = R.drawable.ic_baseline_save_24) {
            lifecycleScope.launch {
                when (viewModel.createEvent()) {
                    SimpleResult.Failure ->
                        UiInterface.showSnackBar(getString(R.string.snack_create_event_error))
                    SimpleResult.Success -> {
                        UiInterface.showSnackBar(getString(R.string.snack_create_event_success))
                        findNavController().navigateUp()
                    }
                }
            }
        }
    }

    fun onSelectDateClicked() {
        hideKeyboard()
        CalendarSheet().show(requireParentFragment().requireContext()) {
            title(this@CreateEventFragment.getString(R.string.ask_event_date))
            selectionMode(SelectionMode.DATE)
            onNegative(R.string.action_cancel)
            onPositive { dateStart: Calendar, _ ->
                viewModel.setEventDate(dateStart)
            }
        }
    }

    fun onHideKeyboardClicked() {
        hideKeyboard()
    }
}