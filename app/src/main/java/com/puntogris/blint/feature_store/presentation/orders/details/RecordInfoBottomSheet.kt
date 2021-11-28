package com.puntogris.blint.feature_store.presentation.orders.details

import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.puntogris.blint.R
import com.puntogris.blint.common.presentation.base.BaseBottomSheetFragment
import com.puntogris.blint.common.utils.Constants.INITIAL
import com.puntogris.blint.common.utils.gone
import com.puntogris.blint.common.utils.visible
import com.puntogris.blint.databinding.RecordInfoBottomSheetBinding

class RecordInfoBottomSheet :
    BaseBottomSheetFragment<RecordInfoBottomSheetBinding>(R.layout.record_info_bottom_sheet) {

    private val args: RecordInfoBottomSheetArgs by navArgs()

    override fun initializeViews() {
        binding.record = args.record
        binding.bottomSheet = this
        if (args.record.type == INITIAL) {
            binding.button23.gone()
            binding.chipGroup.gone()
            binding.externalNameTitle.gone()
            binding.textView117.visible()
        }
    }

    fun onExternalChipClicked() {
        if(args.record.traderId.isNotEmpty()){
            when(args.record.type){
                "IN"-> {
                    val action = RecordInfoBottomSheetDirections.actionRecordInfoBottomSheetToSupplierFragment(supplierId = args.record.traderId)
                    findNavController().navigate(action)
                }
                "OUT" -> {
                    val action = RecordInfoBottomSheetDirections.actionRecordInfoBottomSheetToClientFragment(clientId = args.record.traderId)
                    findNavController().navigate(action)
                }
            }
        }
    }

    fun onNavigateToFullOrder() {
        val action =
            RecordInfoBottomSheetDirections.actionRecordInfoBottomSheetToOrderFragment(args.record.orderId)
        findNavController().navigate(action)
    }
}