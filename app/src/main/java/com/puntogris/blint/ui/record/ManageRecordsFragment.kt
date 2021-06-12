package com.puntogris.blint.ui.record

import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.puntogris.blint.R
import com.puntogris.blint.databinding.FragmentManageRecordsBinding
import com.puntogris.blint.model.Order
import com.puntogris.blint.ui.base.BaseFragment
import com.puntogris.blint.utils.getParentFab
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect

@AndroidEntryPoint
class ManageRecordsFragment : BaseFragment<FragmentManageRecordsBinding>(R.layout.fragment_manage_records) {

   private val viewModel: RecordsViewModel by viewModels()

    override fun initializeViews() {
        getParentFab().setOnClickListener {
            findNavController().navigate(R.id.newOrderGraphNav)
        }
        val ordersAdapter = OrdersAdapter { onOrderClickListener(it) }
        binding.recyclerView.adapter = ordersAdapter
        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())

        lifecycleScope.launchWhenStarted {
            viewModel.getOrders().collect {
                ordersAdapter.submitData(it)
            }
        }

//        binding.productSearchText.addTextChangedListener {
//            lifecycleScope.launch {
//                viewModel.getRecordsWithName(it.toString()).collect {
//                    ordersAdapter.submitData(it)
//                }
//            }
//        }
    }

    private fun onOrderClickListener(order:Order){
        val action = ManageRecordsFragmentDirections.actionManageRecordsFragmentToOrderInfoBottomSheet(order)
        findNavController().navigate(action)
    }

    override fun onDestroyView() {
        binding.recyclerView.adapter = null
        super.onDestroyView()
    }
}