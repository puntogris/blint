package com.puntogris.blint.ui.record

import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.puntogris.blint.R
import com.puntogris.blint.databinding.FragmentOrderBinding
import com.puntogris.blint.model.OrdersTableItem
import com.puntogris.blint.ui.base.BaseFragment
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class OrderFragment : BaseFragment<FragmentOrderBinding>(R.layout.fragment_order) {

    private val args: OrderFragmentArgs by navArgs()
    private lateinit var adapter: OrdersTableAdapter

    private val viewModel: RecordsViewModel by viewModels()

    override fun initializeViews() {
        binding.fragment = this

        adapter = OrdersTableAdapter()
        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerView.adapter = adapter

        if (args.orderId.isNotEmpty()){
            lifecycleScope.launch {
                val orderItems = viewModel.fetchOrderRecords(args.orderId)
                adapter.submitList(orderItems)
            }
        }else {
            args.order?.let { order ->
                val tableItems = order.items.map {
                    OrdersTableItem(it.productName, it.amount, it.value)
                }
                adapter.submitList(tableItems)
            }
        }
    }

    fun onGenerateOrderReceiptClicked(){
        val action = OrderFragmentDirections.actionOrderFragmentToGenerateOrderReceiptBottomSheet()
        findNavController().navigate(action)
    }

    fun onExternalChipClicked(){
//        if(args.order.traderId != 0){
//            when(args.order.type){
//                "IN"-> {
//                    val action = OrderInfoBottomSheetDirections.actionOrderInfoBottomSheetToSupplierFragment(args.order.traderId)
//                    findNavController().navigate(action)
//                }
//                "OUT" -> {
//                    val action = OrderInfoBottomSheetDirections.actionOrderInfoBottomSheetToSupplierFragment(args.order.traderId)
//                    findNavController().navigate(action)
//                }
//            }
//        }
    }
}