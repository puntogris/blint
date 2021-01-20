package com.puntogris.blint.ui.record

import androidx.navigation.fragment.navArgs
import com.puntogris.blint.R
import com.puntogris.blint.databinding.RecordInfoBottomSheetBinding
import com.puntogris.blint.ui.base.BaseBottomSheetFragment

class RecordInfoBottomSheet: BaseBottomSheetFragment<RecordInfoBottomSheetBinding>(R.layout.record_info_bottom_sheet) {

    private val args:RecordInfoBottomSheetArgs by navArgs()

    override fun initializeViews() {
        binding.record = args.record
    }
}