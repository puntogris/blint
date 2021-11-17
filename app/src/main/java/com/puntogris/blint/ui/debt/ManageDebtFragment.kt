package com.puntogris.blint.ui.debt

import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.puntogris.blint.R
import com.puntogris.blint.databinding.FragmentManageDebtBinding
import com.puntogris.blint.ui.base.BaseFragment
import com.puntogris.blint.utils.Constants.CLIENT_DEBT
import com.puntogris.blint.utils.Constants.SUPPLIER_DEBT
import com.puntogris.blint.utils.UiInterface
import com.puntogris.blint.utils.launchAndRepeatWithViewLifecycle
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect

@AndroidEntryPoint
class ManageDebtFragment : BaseFragment<FragmentManageDebtBinding>(R.layout.fragment_manage_debt) {

    private val viewModel: DebtViewModel by viewModels()

    override fun initializeViews() {
        binding.fragment = this
        binding.lifecycleOwner = this
        binding.viewModel = viewModel
        UiInterface.registerUi()

        val debtPagingAdapter = DebtPagingAdapter {}

        binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = debtPagingAdapter
        }

        launchAndRepeatWithViewLifecycle {
            viewModel.getAllDebts().collect {
                debtPagingAdapter.submitData(it)
            }
        }
    }

    fun showClientsDebts() {
        val action =
            ManageDebtFragmentDirections.actionManageDebtFragmentToShowDebtsFragment(CLIENT_DEBT)
        findNavController().navigate(action)
    }

    fun showSuppliersDebts() {
        val action =
            ManageDebtFragmentDirections.actionManageDebtFragmentToShowDebtsFragment(SUPPLIER_DEBT)
        findNavController().navigate(action)
    }
}