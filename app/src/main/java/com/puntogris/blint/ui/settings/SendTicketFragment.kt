package com.puntogris.blint.ui.settings

import android.widget.ArrayAdapter
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.puntogris.blint.R
import com.puntogris.blint.databinding.FragmentSendTicketBinding
import com.puntogris.blint.model.Employee
import com.puntogris.blint.ui.base.BaseFragment
import com.puntogris.blint.utils.*
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class SendTicketFragment: BaseFragment<FragmentSendTicketBinding>(R.layout.fragment_send_ticket) {

    private val viewModel: TicketsViewModel by viewModels()

    override fun initializeViews() {
        setUpUi(showFab = true, fabIcon = R.drawable.ic_baseline_send_24, showAppBar = false){
            if (
                binding.messageText.getString().isNotBlank() &&
                !binding.businessText.text.isNullOrBlank()
            ){
                lifecycleScope.launch {
                    when(viewModel.sendTicket(binding.messageText.getString())){
                        SimpleResult.Failure ->
                            showSnackBarVisibilityAppBar(getString(R.string.snack_error_connection_server_try_later))
                        SimpleResult.Success -> {
                            findNavController().navigateUp()
                            showSnackBarVisibilityAppBar(getString(R.string.snack_ticket_sent_success))
                        }
                    }
                }
            }else showSnackBarVisibilityAppBar(getString(R.string.snack_fill_all_data_ticket))
        }
        var businesses = listOf<Employee>()
        launchAndRepeatWithViewLifecycle {
            businesses = viewModel.getEmployeeList()
            val items = businesses.map { it.businessName }
            binding.businessText.setAdapter(ArrayAdapter(requireContext(),R.layout.dropdown_item_list, items))
        }

        binding.businessText.setOnItemClickListener { _, _, i, _ ->
            viewModel.updateTicketBusiness(businesses[i])
        }

        binding.textView186.setOnClickListener {
            hideKeyboard()
        }

    }
}