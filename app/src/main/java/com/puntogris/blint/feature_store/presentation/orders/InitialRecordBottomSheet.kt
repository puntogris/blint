package com.puntogris.blint.feature_store.presentation.orders

import androidx.navigation.fragment.navArgs
import com.puntogris.blint.R
import com.puntogris.blint.common.presentation.base.BaseBottomSheetFragment
import com.puntogris.blint.databinding.BottomSheetInitialRecordBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class InitialRecordBottomSheet :
    BaseBottomSheetFragment<BottomSheetInitialRecordBinding>(R.layout.bottom_sheet_initial_record) {

    private val args: InitialRecordBottomSheetArgs by navArgs()

    override fun initializeViews() {
        binding.record = args.record
    }
}
