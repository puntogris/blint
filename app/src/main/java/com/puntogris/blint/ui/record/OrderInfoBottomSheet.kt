package com.puntogris.blint.ui.record

import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.puntogris.blint.R
import com.puntogris.blint.databinding.OrderInfoBottomSheetBinding
import com.puntogris.blint.ui.base.BaseBottomSheetFragment

class OrderInfoBottomSheet: BaseBottomSheetFragment<OrderInfoBottomSheetBinding>(R.layout.order_info_bottom_sheet) {

    private val args: OrderInfoBottomSheetArgs by navArgs()

    override fun initializeViews() {
        binding.order = args.order
        binding.bottomSheet = this
    }

    fun onExternalChipClicked(){
        if(args.order.traderId.isNotEmpty()){
            when(args.order.type){
                "IN"-> {
                    val action = OrderInfoBottomSheetDirections.actionOrderInfoBottomSheetToSupplierFragment(args.order.traderId)
                    findNavController().navigate(action)
                }
                "OUT" -> {
                    val action = OrderInfoBottomSheetDirections.actionOrderInfoBottomSheetToSupplierFragment(args.order.traderId)
                    findNavController().navigate(action)
                }
            }
        }
    }

    fun onNavigateToFullOrder(){
        val action = OrderInfoBottomSheetDirections.actionOrderInfoBottomSheetToOrderFragment()
        findNavController().navigate(action)
    }
}