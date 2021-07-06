package com.puntogris.blint.ui.orders.detailed_order

import android.widget.ArrayAdapter
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.navigation.navGraphViewModels
import com.puntogris.blint.DetailedOrderGraphNavArgs
import com.puntogris.blint.R
import com.puntogris.blint.databinding.FragmentOrderTypeBinding
import com.puntogris.blint.model.Client
import com.puntogris.blint.model.Supplier
import com.puntogris.blint.ui.base.BaseFragment
import com.puntogris.blint.ui.orders.NewOrderViewModel
import com.puntogris.blint.utils.changeIconFromDrawable
import com.puntogris.blint.utils.getParentFab
import com.puntogris.blint.utils.setUpUi
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class OrderTypeFragment : BaseFragment<FragmentOrderTypeBinding>(R.layout.fragment_order_type) {

    private val viewModel: NewOrderViewModel by navGraphViewModels(R.id.detailedOrderGraphNav) { defaultViewModelProviderFactory }
    private val args: DetailedOrderGraphNavArgs by navArgs()

    override fun initializeViews() {
        setUpUi(
            showFab = true,
            showAppBar = false,
            fabIcon = R.drawable.ic_baseline_arrow_forward_24,
            showFabCenter = false)
        {
            val action = OrderTypeFragmentDirections.actionOrderTypeFragmentToCreateRecordFragment()
            findNavController().navigate(action)
        }
        binding.recordTypeText.setAdapter(ArrayAdapter(requireContext(),R.layout.dropdown_item_list, resources.getStringArray(R.array.order_type)))

        binding.recordTypeText.setOnItemClickListener { _, _, i, _ ->
            when(i){
                0 -> binding.button22.text = getString(R.string.select_supplier)
                1 -> binding.button22.text = getString(R.string.select_client)
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
                viewModel.updateOrderExternalInfo(it.name, it.clientId)
            }
            getParentFab().changeIconFromDrawable(R.drawable.ic_baseline_add_24)
        }

        findNavController().currentBackStackEntry?.savedStateHandle?.getLiveData<Supplier>("supplier_for_order")?.observe(
            viewLifecycleOwner) {
            it?.let {
                viewModel.updateOrderExternalInfo(it.companyName, it.supplierId)
            }
            getParentFab().changeIconFromDrawable(R.drawable.ic_baseline_add_24)
        }
    }
}