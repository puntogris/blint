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
            binding.navigateToOrderButton.gone()
            binding.chipGroup.gone()
            binding.externalNameTitle.gone()
            binding.initialRecordAlert.visible()
        }
    }

    fun onExternalChipClicked() {
        if (args.record.traderId.isNotEmpty()) {
            val action =
                RecordInfoBottomSheetDirections.actionGlobalTraderFragment(
                    traderId = args.record.traderId
                )
            findNavController().navigate(action)
        }
    }

    fun onNavigateToFullOrder() {
        val action =
            RecordInfoBottomSheetDirections.actionRecordInfoBottomSheetToOrderFragment(args.record.orderId)
        findNavController().navigate(action)
    }
}