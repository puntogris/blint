package com.puntogris.blint.ui.orders

import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.puntogris.blint.R
import com.puntogris.blint.databinding.RecordInfoBottomSheetBinding
import com.puntogris.blint.ui.base.BaseBottomSheetFragment
import com.puntogris.blint.utils.gone
import com.puntogris.blint.utils.visible

class RecordInfoBottomSheet: BaseBottomSheetFragment<RecordInfoBottomSheetBinding>(R.layout.record_info_bottom_sheet) {

    private val args:RecordInfoBottomSheetArgs by navArgs()

    override fun initializeViews() {
        binding.record = args.record
        binding.bottomSheet = this
        if (args.record.type == "INITIAL"){
            binding.button23.gone()
            binding.chipGroup.gone()
            binding.externalNameTitle.gone()
            binding.textView117.visible()
        }
    }

    fun onExternalChipClicked(){
//        if(args.record.traderId.isNotEmpty()){
//            when(args.record.type){
//                "IN"-> {
//                    val action = RecordInfoBottomSheetDirections.actionRecordInfoBottomSheetToSupplierFragment(args.record.traderId)
//                    findNavController().navigate(action)
//                }
//                "OUT" -> {
//                    val action = RecordInfoBottomSheetDirections.actionRecordInfoBottomSheetToClientFragment(args.record.traderId)
//                    findNavController().navigate(action)
//                }
//            }
//        }
    }

    fun onNavigateToFullOrder(){
        val action = RecordInfoBottomSheetDirections.actionRecordInfoBottomSheetToOrderFragment(args.record.orderId)
        findNavController().navigate(action)
    }
}