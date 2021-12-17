package com.puntogris.blint.feature_store.presentation.settings

import android.widget.ArrayAdapter
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.puntogris.blint.R
import com.puntogris.blint.common.presentation.base.BaseFragment
import com.puntogris.blint.common.utils.*
import com.puntogris.blint.common.utils.types.ProgressResource
import com.puntogris.blint.databinding.FragmentSendTicketBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

@AndroidEntryPoint
class SendTicketFragment : BaseFragment<FragmentSendTicketBinding>(R.layout.fragment_send_ticket) {

    private val viewModel: TicketsViewModel by viewModels()

    override fun initializeViews() {
        binding.fragment = this
        binding.viewModel = viewModel

        UiInterface.registerUi(showAppBar = false)
        //todo             sendTicket()
        //remove maybe the ticket system and use a prepolated email
        setupTicketReasonAdapter()
    }

    private fun setupTicketReasonAdapter() {
        binding.ticketReason.setAdapter(
            ArrayAdapter(
                requireContext(),
                R.layout.dropdown_item_list,
                resources.getStringArray(R.array.ticket_reasons)
            )
        )

        binding.ticketReason.setOnItemClickListener { _, _, i, _ ->
            viewModel.updateTicketReason(if (i == 0) Constants.PROBLEM else Constants.SUGGESTION)
        }
    }

    private fun sendTicket() {
        lifecycleScope.launch {
            viewModel.sendTicket().collect {
                when (it) {
                    is ProgressResource.Error -> {
                        binding.progressBar.gone()
                        UiInterface.showSnackBar(getString(it.error))
                    }
                    is ProgressResource.InProgress -> {
                        binding.progressBar.visible()
                    }
                    is ProgressResource.Success -> {
                        findNavController().navigateUp()
                        UiInterface.showSnackBar(getString(R.string.snack_ticket_sent_success))
                    }
                }
            }

        }
    }

    fun onHideKeyboardClicked() {
        hideKeyboard()
    }
}
