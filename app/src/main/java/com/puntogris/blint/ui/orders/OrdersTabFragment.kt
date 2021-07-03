package com.puntogris.blint.ui.orders

import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.puntogris.blint.R
import com.puntogris.blint.databinding.FragmentOrdersTabBinding
import com.puntogris.blint.model.Order
import com.puntogris.blint.ui.base.BaseFragment
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect

@AndroidEntryPoint
class OrdersTabFragment : BaseFragment<FragmentOrdersTabBinding>(R.layout.fragment_orders_tab) {

    private val viewModel: OrdersViewModel by viewModels()

    override fun initializeViews() {
        val ordersAdapter = OrdersAdapter{onOrderClickListener(it)}
        binding.recyclerView.apply {
            adapter = ordersAdapter
            layoutManager = LinearLayoutManager(requireContext())
        }

        lifecycleScope.launchWhenStarted {
            viewModel.getBusinessOrders().collect {
                ordersAdapter.submitData(it)
            }
        }
    }

    private fun onOrderClickListener(order: Order){
        val action = ManageOrdersFragmentDirections.actionManageOrdersFragmentToOrderInfoBottomSheet(order)
        findNavController().navigate(action)
    }
}