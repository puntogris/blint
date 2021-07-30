package com.puntogris.blint.ui.debt

import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.puntogris.blint.R
import com.puntogris.blint.databinding.FragmentDebtStatusBinding
import com.puntogris.blint.model.Debt
import com.puntogris.blint.ui.base.BaseFragment
import com.puntogris.blint.utils.*
import com.puntogris.blint.utils.Constants.CLIENT
import com.puntogris.blint.utils.Constants.SUPPLIER
import com.puntogris.blint.utils.Constants.UPDATE_DEBT_KEY
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DebtStatusFragment : BaseFragment<FragmentDebtStatusBinding>(R.layout.fragment_debt_status) {

    private val viewModel: DebtViewModel by viewModels()
    private val args: DebtStatusFragmentArgs by navArgs()
    private lateinit var debtsAdapter: DebtsAdapter

    override fun initializeViews() {
        binding.fragment = this
        UiInterface.register(showFab = true){
            onModifyDebtButtonClicked()
        }

        launchAndRepeatWithViewLifecycle {
            if (args.client != null){
                args.client?.let {
                    it.debt.let {

                    }
                    binding.textView184.setDebtColor(it.debt)
                    when(val debts = viewModel.getLastDebts(it.clientId)){
                        is RepoResult.Error -> {}
                        RepoResult.InProgress -> {}
                        is RepoResult.Success -> {
                            setUpDebtsRecyclerView(debts.data)
                        }
                    }
                }
            }else {
                args.supplier?.let {
                    binding.textView184.setDebtColor(it.debt)
                    when(val debts = viewModel.getLastDebts(it.supplierId)){
                        is RepoResult.Error -> {}
                        RepoResult.InProgress -> {}
                        is RepoResult.Success -> {
                            setUpDebtsRecyclerView(debts.data)
                        }
                    }
                }
            }
        }

        onBackStackLiveData<Float>(UPDATE_DEBT_KEY){ deb->
            if (args.client != null){
                args.client?.let { client ->
                    binding.textView184.setDebtColor(client.debt + deb)
                    client.debt += deb
                }
            }else{
                args.supplier?.let { sup ->
                    binding.textView184.setDebtColor(sup.debt + deb)
                    sup.debt += deb
                }
            }
        }

    }

    private fun setUpDebtsRecyclerView(debts: List<Debt>){
        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
        debtsAdapter = DebtsAdapter { onDebtClicked(it) }
        binding.recyclerView.adapter = debtsAdapter
        debtsAdapter.submitList(debts)
    }

    private fun onDebtClicked(debt: Debt){

    }

    fun onModifyDebtButtonClicked(){
        val debt = Debt()
        if (args.client != null) {
            debt.type = CLIENT
            debt.traderName = args.client?.name.toString()
            debt.traderId = args.client?.clientId.toString()
        }else{
            debt.type = SUPPLIER
            debt.traderName = args.supplier?.companyName.toString()
            debt.traderId = args.supplier?.supplierId.toString()
        }
        val action = DebtStatusFragmentDirections.actionDebtStatusFragmentToModifyDebtFragment(debt = debt)
        findNavController().navigate(action)
    }

    override fun onDestroyView() {
        binding.recyclerView.adapter = null
        super.onDestroyView()
    }
}