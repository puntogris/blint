package com.puntogris.blint.feature_store.presentation.events.new_event

import android.text.InputType
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.maxkeppeler.sheets.core.SheetStyle
import com.maxkeppeler.sheets.input.InputSheet
import com.maxkeppeler.sheets.input.type.InputEditText
import com.puntogris.blint.R
import com.puntogris.blint.common.presentation.base.BaseFragment
import com.puntogris.blint.common.utils.UiInterface
import com.puntogris.blint.common.utils.hideKeyboard
import com.puntogris.blint.common.utils.registerToolbarBackButton
import com.puntogris.blint.common.utils.types.Resource
import com.puntogris.blint.databinding.FragmentCreateEventBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class CreateEventFragment :
    BaseFragment<FragmentCreateEventBinding>(R.layout.fragment_create_event) {

    private val viewModel: CreateEventViewModel by viewModels()

    override fun initializeViews() {
        binding.fragment = this
        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner

        setupCalendarListener()
        registerToolbarBackButton(binding.createEventToolbar)
    }

    private fun setupCalendarListener() {
        binding.createEventCalendar.setOnDateChangeListener { _, year, month, day ->
            viewModel.setEventDate(year, month, day)
        }
    }

    fun onAddEventContentClicked() {
        InputSheet().show(requireParentFragment().requireContext()) {
            style(SheetStyle.DIALOG)
            title(R.string.event_content)
            with(InputEditText {
                defaultValue(viewModel.event.value.content)
                content(R.string.create_event_content_hint)
                required(true)
                hint(R.string.message_hint)
                inputType(InputType.TYPE_TEXT_FLAG_CAP_SENTENCES)
                onNegative(R.string.action_cancel)
                onPositive(R.string.action_accept) {
                    viewModel.setEventContent(it["0"].toString())
                    hideKeyboard()
                }
            })
        }
    }

    fun onSaveEventClicked() {
        lifecycleScope.launch {
            when (viewModel.createEvent()) {
                is Resource.Error ->
                    UiInterface.showSnackBar(getString(R.string.snack_create_event_error))
                is Resource.Success -> {
                    UiInterface.showSnackBar(getString(R.string.snack_create_event_success))
                    findNavController().navigateUp()
                }
            }
        }
    }

}