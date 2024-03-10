package com.puntogris.blint.feature_store.presentation.events.manage

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.ArrayAdapter
import androidx.core.os.bundleOf
import androidx.core.view.WindowInsetsControllerCompat
import androidx.fragment.app.setFragmentResult
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.maxkeppeler.sheets.info.InfoSheet
import com.puntogris.blint.R
import com.puntogris.blint.common.utils.Keys
import com.puntogris.blint.common.utils.UiInterface
import com.puntogris.blint.common.utils.isDarkThemeOn
import com.puntogris.blint.common.utils.launchAndRepeatWithViewLifecycle
import com.puntogris.blint.common.utils.setDateFromTimestamp
import com.puntogris.blint.common.utils.types.EventStatus
import com.puntogris.blint.common.utils.types.Resource
import com.puntogris.blint.common.utils.viewBinding
import com.puntogris.blint.databinding.EventInfoBottomSheetBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class EventInfoBottomSheet : BottomSheetDialogFragment() {

    private val binding by viewBinding(EventInfoBottomSheetBinding::bind)

    private val viewModel: ManageEventsViewModel by viewModels()

    private val args: EventInfoBottomSheetArgs by navArgs()

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return (super.onCreateDialog(savedInstanceState) as BottomSheetDialog).apply {
            window?.let {
                it.clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND)
                WindowInsetsControllerCompat(it, it.decorView).isAppearanceLightStatusBars =
                    !isDarkThemeOn()
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.event_info_bottom_sheet, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupEventStatusAdapter()
        setupListeners()
        setupObservers()
    }

    private fun setupObservers() {
        launchAndRepeatWithViewLifecycle {
            viewModel.event.collectLatest {
                binding.textViewEventMessage.text = it.content
                setDateFromTimestamp(binding.textViewEventDate, it.timestamp)
            }
        }
    }

    private fun setupListeners() {
        binding.imageViewDeleteEvent.setOnClickListener {
            onDeleteEventButtonClicked()
        }
        binding.buttonSaveEvent.setOnClickListener {
            onSaveButtonClicked()
        }
    }

    private fun setupEventStatusAdapter() {
        val eventStatus = resources.getStringArray(R.array.event_status)
        val adapter = ArrayAdapter(requireContext(), R.layout.dropdown_item_list, eventStatus)
        val eventStatusText = if (viewModel.event.value.status == EventStatus.Pending.value) {
            eventStatus[0]
        } else {
            eventStatus[1]
        }
        with(binding.editTextEventStatus) {
            setText(eventStatusText)
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
                is Resource.Error -> {
                    UiInterface.showSnackBar(getString(R.string.snack_update_event_error))
                }

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
