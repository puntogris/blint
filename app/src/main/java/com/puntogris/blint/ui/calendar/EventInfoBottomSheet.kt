package com.puntogris.blint.ui.calendar

import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.puntogris.blint.R
import com.puntogris.blint.databinding.EventInfoBottomSheetBinding
import com.puntogris.blint.ui.base.BaseBottomSheetFragment
import com.puntogris.blint.utils.showLongSnackBarAboveFab
import com.puntogris.blint.utils.showShortSnackBar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class EventInfoBottomSheet:BaseBottomSheetFragment<EventInfoBottomSheetBinding>(R.layout.event_info_bottom_sheet) {

    private val viewModel: CalendarViewModel by viewModels()
    private val args:EventInfoBottomSheetArgs by navArgs()

    override fun initializeViews() {
        binding.fragment = this
        binding.lifecycleOwner = viewLifecycleOwner
        binding.viewModel = viewModel
        binding.eventStatusText.setText(if (args.event.status == "PENDING") "Pendiente" else "Finalizado")

        viewModel.setEvent(args.event)

        val items = listOf("Pendiente", "Finalizado")
        val adapter = ArrayAdapter(requireContext(), R.layout.dropdown_item_list, items)
        (binding.eventStatus.editText as? AutoCompleteTextView)?.setAdapter(adapter)

        binding.eventStatusText.setOnItemClickListener { _, _, i, _ ->
            viewModel.updateEventStatus(i)
        }
    }

    fun onSaveButtonClicked(){
        lifecycleScope.launch {
            viewModel.saveEvent()
            findNavController().apply {
                previousBackStackEntry!!.savedStateHandle.set("dismiss_key", true)
                popBackStack()
            }
            showLongSnackBarAboveFab("Se actualizo el estado del evento correctamente.")
        }
    }
}