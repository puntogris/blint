package com.puntogris.blint.ui.orders.detailed_order

import android.widget.ArrayAdapter
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.navigation.navGraphViewModels
import com.puntogris.blint.DetailedOrderGraphNavArgs
import com.puntogris.blint.R
import com.puntogris.blint.databinding.FragmentOrderTypeBinding
import com.puntogris.blint.model.ProductWithRecord
import com.puntogris.blint.model.Record
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

        if (args.product != null && !viewModel.productWithRecords.any { it.product.productId == args.product?.productId }) {
            args.product?.let {
                viewModel.productWithRecords.add(
                    ProductWithRecord(it, Record(productName = it.name, productId = it.productId))
                )
            }
        }

        binding.recordTypeText.apply {
            setAdapter(
                ArrayAdapter(
                    requireContext(),
                    R.layout.dropdown_item_list,
                    resources.getStringArray(R.array.order_type)
                )
            )
            setOnItemClickListener { _, _, i, _ ->
                binding.button22.text =
                    if (i == 0) getString(R.string.select_supplier)
                    else getString(R.string.select_client)
                viewModel.updateRecordType(i)
            }
        }
    }

    fun onAddTraderClicked() {
        val action =
            OrderTypeFragmentDirections.actionOrderTypeFragmentToAddOrderClientSupplierBottomSheet(
                viewModel.getOrderType()
            )
        findNavController().navigate(action)
    }
}