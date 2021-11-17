package com.puntogris.blint.ui.orders.manage

import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.puntogris.blint.R
import com.puntogris.blint.databinding.FragmentOrdersTabBinding
import com.puntogris.blint.model.OrderWithRecords
import com.puntogris.blint.ui.base.BaseFragment
import com.puntogris.blint.utils.launchAndRepeatWithViewLifecycle
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect

@AndroidEntryPoint
class OrdersTabFragment : BaseFragment<FragmentOrdersTabBinding>(R.layout.fragment_orders_tab) {

    private val viewModel: ManageOrdersViewModel by viewModels(ownerProducer = { requireParentFragment() })

    override fun initializeViews() {
        val ordersAdapter = OrdersAdapter { onOrderClickListener(it) }
        binding.recyclerView.apply {
            adapter = ordersAdapter
            layoutManager = LinearLayoutManager(requireContext())
        }

        launchAndRepeatWithViewLifecycle {
            viewModel.getBusinessOrders().collect {
                ordersAdapter.submitData(it)
            }
        }
    }

    private fun onOrderClickListener(order: OrderWithRecords) {
        val action =
            ManageOrdersFragmentDirections.actionManageOrdersFragmentToOrderInfoBottomSheet(order)
        findNavController().navigate(action)
    }

    override fun onDestroyView() {
        binding.recyclerView.adapter = null
        super.onDestroyView()
    }
}