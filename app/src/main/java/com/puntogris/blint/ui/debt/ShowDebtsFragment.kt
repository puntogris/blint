package com.puntogris.blint.ui.debt

import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.puntogris.blint.R
import com.puntogris.blint.databinding.FragmentShowDebtsBinding
import com.puntogris.blint.model.SimpleDebt
import com.puntogris.blint.ui.base.BaseFragment
import com.puntogris.blint.utils.Constants.CLIENT_DEBT
import com.puntogris.blint.utils.registerUiInterface
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect

@AndroidEntryPoint
class ShowDebtsFragment : BaseFragment<FragmentShowDebtsBinding>(R.layout.fragment_show_debts) {

    private val args: ShowDebtsFragmentArgs by navArgs()
    private val viewModel: DebtViewModel by viewModels()

    override fun initializeViews() {
        registerUiInterface.register()

        val simpleDebtAdapter = SimpleDebtAdapter{ onDebtClicked(it) }
        binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = simpleDebtAdapter
        }

        lifecycleScope.launchWhenCreated {
            if (args.debtType == CLIENT_DEBT){
                viewModel.getAllClients().collect { simpleDebtAdapter.submitData(it) }
            }else {
                viewModel.getAllSuppliers().collect { simpleDebtAdapter.submitData(it) }
            }
        }
    }

    private fun onDebtClicked(simpleDebt: SimpleDebt){

    }

}