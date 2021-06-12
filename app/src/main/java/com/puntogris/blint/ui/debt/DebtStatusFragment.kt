package com.puntogris.blint.ui.debt

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.puntogris.blint.R
import com.puntogris.blint.databinding.FragmentDebtStatusBinding
import com.puntogris.blint.model.Client
import com.puntogris.blint.model.Debt
import com.puntogris.blint.model.Supplier
import com.puntogris.blint.ui.base.BaseFragment
import com.puntogris.blint.utils.Constants.CLIENT_DEBT
import com.puntogris.blint.utils.createLongSnackBar
import com.puntogris.blint.utils.getParentFab
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class DebtStatusFragment : BaseFragment<FragmentDebtStatusBinding>(R.layout.fragment_debt_status) {

    private val viewModel: DebtViewModel by viewModels()
    private val args: DebtStatusFragmentArgs by navArgs()
    private lateinit var debtsAdapter: DebtsAdapter

    override fun initializeViews() {
        binding.fragment = this
        getParentFab().setOnClickListener {
            onModifyDebtButtonClicked()
        }
        lifecycleScope.launch {
            if (args.debtType == CLIENT_DEBT){
                val client = viewModel.getClientFromDb(args.id.toInt())
                binding.textView184.text = client.debt.toString() + " $"
                viewModel.updateDebtWithTraderInfo(client.clientId, client.name, client.businessId)
            }else {
                val supplier = viewModel.getSupplierFromDb(args.id.toInt())
                binding.textView184.text = supplier.debt.toString() + " $"
                viewModel.updateDebtWithTraderInfo(supplier.supplierId, supplier.companyName, supplier.businessId)
            }
            val debts = viewModel.getLastDebts(args.id.toInt())
            setUpDebtsRecyclerView(debts)


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
        val debt = viewModel.getDebtData()
        if (debt.traderId != 0 && debt.traderName.isNotBlank()){
            val action = DebtStatusFragmentDirections.actionDebtStatusFragmentToModifyDebtFragment(debt = debt,debtType = args.debtType)
            findNavController().navigate(action)
        }else{
            createLongSnackBar("Espere un segundo que estamos buscando la informacion del servidor.")
        }

    }
}