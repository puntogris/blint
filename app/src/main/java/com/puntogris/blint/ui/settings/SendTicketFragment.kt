package com.puntogris.blint.ui.settings

import android.widget.ArrayAdapter
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.puntogris.blint.R
import com.puntogris.blint.databinding.FragmentSendTicketBinding
import com.puntogris.blint.ui.base.BaseFragment
import com.puntogris.blint.utils.*
import com.puntogris.blint.utils.types.RepoResult
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

@AndroidEntryPoint
class SendTicketFragment : BaseFragment<FragmentSendTicketBinding>(R.layout.fragment_send_ticket) {

    private val viewModel: TicketsViewModel by viewModels()

    override fun initializeViews() {
        binding.fragment = this
        UiInterface.registerUi(
            showFab = true,
            fabIcon = R.drawable.ic_baseline_send_24,
            showAppBar = false
        ) {
            sendTicket()
        }
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
                    is RepoResult.Error -> {
                        binding.progressBar.gone()
                        UiInterface.showSnackBar(getString(it.error))
                    }
                    is RepoResult.InProgress -> {
                        binding.progressBar.visible()
                    }
                    is RepoResult.Success -> {
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
