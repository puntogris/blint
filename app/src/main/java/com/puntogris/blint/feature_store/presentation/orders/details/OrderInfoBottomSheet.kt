package com.puntogris.blint.feature_store.presentation.orders.details

import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.puntogris.blint.R
import com.puntogris.blint.common.presentation.base.BaseBottomSheetFragment
import com.puntogris.blint.databinding.OrderInfoBottomSheetBinding

class OrderInfoBottomSheet :
    BaseBottomSheetFragment<OrderInfoBottomSheetBinding>(R.layout.order_info_bottom_sheet) {

    private val args: OrderInfoBottomSheetArgs by navArgs()

    override fun initializeViews() {
        binding.order = args.order.order
        binding.bottomSheet = this
    }

    fun onExternalChipClicked() {
//        if(args.order.traderId.isNotEmpty()){
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

    fun onNavigateToFullOrder() {
        val action =
            OrderInfoBottomSheetDirections.actionOrderInfoBottomSheetToOrderFragment(order = args.order)
        findNavController().navigate(action)
    }
}