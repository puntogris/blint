package com.puntogris.blint.ui.record

import android.widget.ArrayAdapter
import androidx.navigation.fragment.findNavController
import androidx.navigation.navGraphViewModels
import com.puntogris.blint.R
import com.puntogris.blint.databinding.FragmentOrderTypeBinding
import com.puntogris.blint.model.Client
import com.puntogris.blint.model.Supplier
import com.puntogris.blint.ui.base.BaseFragment
import com.puntogris.blint.utils.changeIconFromDrawable
import com.puntogris.blint.utils.getParentFab
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class OrderTypeFragment : BaseFragment<FragmentOrderTypeBinding>(R.layout.fragment_order_type) {

    private val viewModel: NewOrderViewModel by navGraphViewModels(R.id.newOrderGraphNav) { defaultViewModelProviderFactory }

    override fun initializeViews() {

        binding.recordTypeText.setAdapter(ArrayAdapter(requireContext(),R.layout.dropdown_item_list, listOf("Entrada", "Salida")))

        binding.recordTypeText.setOnItemClickListener { _, _, i, _ ->
            when(i){
                0 -> binding.button22.text = "Seleccionar Proveedor"
                1 -> binding.button22.text = "Seleccionar Cliente"
            }
            viewModel.updateRecordType(i)
        }

        binding.button22.setOnClickListener {
            val action = OrderTypeFragmentDirections.actionOrderTypeFragmentToAddOrderClientSupplierBottomSheet(viewModel.getOrderType())
            findNavController().navigate(action)
        }

        findNavController().currentBackStackEntry?.savedStateHandle?.getLiveData<Client>("client_for_order")?.observe(
            viewLifecycleOwner) {
            it?.let {
                viewModel.updateOrderExternalInfo(it.name, it.clientId.toString())
            }
            getParentFab().changeIconFromDrawable(R.drawable.ic_baseline_add_24)
        }

        findNavController().currentBackStackEntry?.savedStateHandle?.getLiveData<Supplier>("supplier_for_order")?.observe(
            viewLifecycleOwner) {
            it?.let {
                viewModel.updateOrderExternalInfo(it.companyName, it.supplierId.toString())
            }
            getParentFab().changeIconFromDrawable(R.drawable.ic_baseline_add_24)
        }

        getParentFab().apply {
            changeIconFromDrawable(R.drawable.ic_baseline_arrow_forward_24)
            setOnClickListener {
                val action = OrderTypeFragmentDirections.actionOrderTypeFragmentToCreateRecordFragment()
                findNavController().navigate(action)
            }
        }

    }

}