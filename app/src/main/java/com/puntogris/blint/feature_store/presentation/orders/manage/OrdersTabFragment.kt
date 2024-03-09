package com.puntogris.blint.feature_store.presentation.orders.manage

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.puntogris.blint.R
import com.puntogris.blint.common.utils.launchAndRepeatWithViewLifecycle
import com.puntogris.blint.common.utils.showEmptyUiOnEmptyAdapter
import com.puntogris.blint.common.utils.viewBinding
import com.puntogris.blint.databinding.FragmentOrdersTabBinding
import com.puntogris.blint.feature_store.domain.model.order.OrderWithRecords
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class OrdersTabFragment : Fragment(R.layout.fragment_orders_tab) {

    private val viewModel: ManageOrdersViewModel by viewModels(ownerProducer = { requireParentFragment() })

    private val binding by viewBinding(FragmentOrdersTabBinding::bind)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupOrdersAdapter()
    }

    private fun setupOrdersAdapter() {
        val adapter = OrdersAdapter { onOrderClickListener(it) }
        binding.ordersTabRecyclerView.adapter = adapter
        subscribeUi(adapter)
    }

    private fun subscribeUi(adapter: OrdersAdapter) {
        launchAndRepeatWithViewLifecycle {
            viewModel.getOrders().collect {
                adapter.submitData(it)
            }
        }
        launchAndRepeatWithViewLifecycle {
            showEmptyUiOnEmptyAdapter(adapter, binding.ordersTabEmptyUi)
        }
    }

    private fun onOrderClickListener(order: OrderWithRecords) {
        val action = ManageOrdersFragmentDirections.actionGlobalOrderFragment(order = order)
        findNavController().navigate(action)
    }

    override fun onDestroyView() {
        binding.ordersTabRecyclerView.adapter = null
        super.onDestroyView()
    }
}
