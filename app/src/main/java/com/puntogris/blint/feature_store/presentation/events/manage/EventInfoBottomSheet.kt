package com.puntogris.blint.feature_store.presentation.events.manage

import android.widget.ArrayAdapter
import androidx.core.os.bundleOf
import androidx.fragment.app.setFragmentResult
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.maxkeppeler.sheets.info.InfoSheet
import com.puntogris.blint.R
import com.puntogris.blint.common.presentation.base.BaseBottomSheetFragment
import com.puntogris.blint.common.utils.Keys
import com.puntogris.blint.common.utils.UiInterface
import com.puntogris.blint.common.utils.types.EventStatus
import com.puntogris.blint.common.utils.types.Resource
import com.puntogris.blint.databinding.EventInfoBottomSheetBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class EventInfoBottomSheet :
    BaseBottomSheetFragment<EventInfoBottomSheetBinding>(R.layout.event_info_bottom_sheet) {

    private val viewModel: ManageEventsViewModel by viewModels()
    private val args: EventInfoBottomSheetArgs by navArgs()

    override fun initializeViews() {
        binding.fragment = this
        binding.lifecycleOwner = viewLifecycleOwner
        binding.viewModel = viewModel

        setupEventStatusAdapter()
    }

    private fun setupEventStatusAdapter() {
        val eventStatus = resources.getStringArray(R.array.event_status)
        val adapter = ArrayAdapter(requireContext(), R.layout.dropdown_item_list, eventStatus)

        binding.eventStatus.apply {
            setText(if (viewModel.event.value.status == EventStatus.Pending.value) eventStatus[0] else eventStatus[1])
            setAdapter(adapter)
            setOnItemClickListener { _, _, i, _ ->
                viewModel.updateEventStatus(i)
            }
        }
    }

    fun onDeleteEventButtonClicked() {
        InfoSheet().show(requireParentFragment().requireContext()) {
            title(R.string.ask_delete_event_title)
            content(R.string.delete_event_warning)
            onNegative(R.string.action_cancel)
            onPositive(R.string.action_yes) { onDeleteEventConfirmed() }
        }
    }

    private fun onDeleteEventConfirmed() {
        lifecycleScope.launch {
            when (viewModel.deleteEvent()) {
                is Resource.Error ->
                    UiInterface.showSnackBar(getString(R.string.snack_delete_event_error))
                is Resource.Success -> {
                    UiInterface.showSnackBar(getString(R.string.snack_delete_event_success))
                    findNavController().navigateUp()
                }
            }
        }
    }

    fun onSaveButtonClicked() {
        lifecycleScope.launch {
            when (viewModel.updateEvent()) {
                is Resource.Error ->
                    UiInterface.showSnackBar(getString(R.string.snack_update_event_error))
                is Resource.Success -> {
                    setFragmentResult(
                        Keys.EVENT_FILTER_KEY,
                        bundleOf(Keys.EVENT_POSITION_KEY to args.position)
                    )
                    findNavController().navigateUp()
                }
            }
        }
    }
}