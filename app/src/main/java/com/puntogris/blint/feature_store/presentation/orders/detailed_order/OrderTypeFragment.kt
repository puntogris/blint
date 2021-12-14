package com.puntogris.blint.feature_store.presentation.orders.detailed_order

import android.widget.ArrayAdapter
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.navigation.navGraphViewModels
import com.puntogris.blint.DetailedOrderGraphNavArgs
import com.puntogris.blint.R
import com.puntogris.blint.common.presentation.base.BaseFragment
import com.puntogris.blint.common.utils.UiInterface
import com.puntogris.blint.databinding.FragmentOrderTypeBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class OrderTypeFragment : BaseFragment<FragmentOrderTypeBinding>(R.layout.fragment_order_type) {

    private val viewModel: NewOrderViewModel by navGraphViewModels(R.id.detailedOrderGraphNav) { defaultViewModelProviderFactory }
    private val args: DetailedOrderGraphNavArgs by navArgs()

    override fun initializeViews() {
        binding.fragment = this
        UiInterface.registerUi(
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
                viewModel.updateOrderType(i)
            }
        }
    }

    fun onAddTraderClicked() {
        findNavController().navigate(R.id.action_orderTypeFragment_to_orderTraderBottomSheet)
    }
}