package com.puntogris.blint.ui.orders.detailed_order

import android.widget.ArrayAdapter
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.navigation.navGraphViewModels
import com.puntogris.blint.DetailedOrderGraphNavArgs
import com.puntogris.blint.R
import com.puntogris.blint.databinding.FragmentOrderTypeBinding
import com.puntogris.blint.ui.base.BaseFragment
import com.puntogris.blint.utils.UiInterface
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class OrderTypeFragment : BaseFragment<FragmentOrderTypeBinding>(R.layout.fragment_order_type) {

    private val viewModel: NewOrderViewModel by navGraphViewModels(R.id.detailedOrderGraphNav) { defaultViewModelProviderFactory }
    private val args: DetailedOrderGraphNavArgs by navArgs()

    override fun initializeViews() {
        binding.fragment = this
        UiInterface.registerUi(
            showFab = true,
            showAppBar = false,
            fabIcon = R.drawable.ic_baseline_arrow_forward_24,
            showFabCenter = false
        )
        {
            val action = OrderTypeFragmentDirections.actionOrderTypeFragmentToCreateRecordFragment()
            findNavController().navigate(action)
        }

        args.product?.let {
            viewModel.addProduct(it)
        }
        setupOrderTypeAdapter()
    }

    private fun setupOrderTypeAdapter() {
        binding.recordType.apply {
            setAdapter(
                ArrayAdapter(
                    requireContext(),
                    R.layout.dropdown_item_list,
                    resources.getStringArray(R.array.order_type)
                )
            )
            setOnItemClickListener { _, _, i, _ ->
                binding.addTraderButton.setText(
                    if (i == 0) R.string.select_supplier else R.string.select_client
                )
                viewModel.updateOrderType(i)
            }
        }
    }

    fun onAddTraderClicked() {
        val action =
            OrderTypeFragmentDirections.actionOrderTypeFragmentToAddOrderClientSupplierBottomSheet(
                viewModel.newOrder.value.type
            )
        findNavController().navigate(action)
    }
}