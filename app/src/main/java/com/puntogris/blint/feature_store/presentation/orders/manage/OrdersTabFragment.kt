package com.puntogris.blint.feature_store.presentation.orders.manage

import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.puntogris.blint.R
import com.puntogris.blint.common.presentation.base.BaseFragment
import com.puntogris.blint.common.utils.launchAndRepeatWithViewLifecycle
import com.puntogris.blint.databinding.FragmentOrdersTabBinding
import com.puntogris.blint.feature_store.domain.model.order.OrderWithRecords
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect

@AndroidEntryPoint
class OrdersTabFragment : BaseFragment<FragmentOrdersTabBinding>(R.layout.fragment_orders_tab) {

    private val viewModel: ManageOrdersViewModel by viewModels(ownerProducer = { requireParentFragment() })

    override fun initializeViews() {
        setupOrdersAdapter()
    }

    private fun setupOrdersAdapter() {
        OrdersAdapter { onOrderClickListener(it) }.let {
            binding.recyclerView.adapter = it
            subscribeUi(it)
        }
    }

    private fun subscribeUi(adapter: OrdersAdapter) {
        launchAndRepeatWithViewLifecycle {
            viewModel.getOrders().collect {
                adapter.submitData(it)
            }
        }
    }

    private fun onOrderClickListener(order: OrderWithRecords) {
        val action = ManageOrdersFragmentDirections.actionManageOrdersFragmentToOrderFragment(order = order)
        findNavController().navigate(action)
    }

    override fun onDestroyView() {
        binding.recyclerView.adapter = null
        super.onDestroyView()
    }
}