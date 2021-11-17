package com.puntogris.blint.ui.calendar

import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.maxkeppeler.sheets.info.InfoSheet
import com.puntogris.blint.R
import com.puntogris.blint.databinding.EventInfoBottomSheetBinding
import com.puntogris.blint.ui.base.BaseBottomSheetFragment
import com.puntogris.blint.utils.*
import com.puntogris.blint.utils.Constants.DISMISS_EVENT_KEY
import com.puntogris.blint.utils.Constants.PENDING
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.util.*

@AndroidEntryPoint
class EventInfoBottomSheet:BaseBottomSheetFragment<EventInfoBottomSheetBinding>(R.layout.event_info_bottom_sheet) {

    private val viewModel: CalendarViewModel by viewModels()
    private val args: EventInfoBottomSheetArgs by navArgs()

    override fun initializeViews() {
        binding.fragment = this
        binding.lifecycleOwner = viewLifecycleOwner
        binding.viewModel = viewModel
        viewModel.setEvent(args.event)

        val items = resources.getStringArray(R.array.event_type)
        binding.eventStatusText.setText(if (args.event.status == PENDING) items[0] else items[1])
        val adapter = ArrayAdapter(requireContext(), R.layout.dropdown_item_list, items)
        (binding.eventStatus.editText as? AutoCompleteTextView)?.setAdapter(adapter)

        binding.eventStatusText.setOnItemClickListener { _, _, i, _ ->
            viewModel.updateEventStatus(i)
        }
    }

    fun onDeleteEventButtonClicked(){
        InfoSheet().show(requireParentFragment().requireContext()) {
            title(R.string.ask_delete_event_title)
            content(R.string.delete_event_warning)
            onNegative(R.string.action_cancel)
            onPositive(R.string.action_yes) { onDeleteEventConfirmed() }
        }
    }

    fun onDeleteEventConfirmed(){
        lifecycleScope.launch {
            when (viewModel.deleteEvent(args.event.eventId)) {
                SimpleResult.Failure ->
                    UiInterface.showSnackBar(getString(R.string.snack_delete_event_error))
                SimpleResult.Success -> {
                    UiInterface.showSnackBar(getString(R.string.snack_delete_event_success))
                    findNavController().navigateUp()
                }
            }
        }
    }

    fun onSaveButtonClicked(){
        lifecycleScope.launch {
            when(viewModel.updateEvent()){
                SimpleResult.Failure ->
                    UiInterface.showSnackBar(getString(R.string.snack_update_event_error))
                SimpleResult.Success -> {
                    findNavController().apply {
                        previousBackStackEntry!!.savedStateHandle.set(DISMISS_EVENT_KEY, true)
                        popBackStack()
                        UiInterface.showSnackBar(getString(R.string.snack_update_event_success))
                    }
                }
            }
        }
    }
}