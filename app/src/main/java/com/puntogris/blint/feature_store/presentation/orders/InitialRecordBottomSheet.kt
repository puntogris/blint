package com.puntogris.blint.feature_store.presentation.orders

import androidx.navigation.fragment.navArgs
import com.puntogris.blint.R
import com.puntogris.blint.common.presentation.base.BaseBottomSheetFragment
import com.puntogris.blint.common.utils.setDateFromTimestampWithTime
import com.puntogris.blint.databinding.BottomSheetInitialRecordBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class InitialRecordBottomSheet :
    BaseBottomSheetFragment<BottomSheetInitialRecordBinding>(R.layout.bottom_sheet_initial_record) {

    private val args: InitialRecordBottomSheetArgs by navArgs()

    override fun initializeViews() {
        with(binding) {
            textViewProductName.text = args.record.productName
            textViewProductAmount.text = args.record.amount.toString()
            textViewProductDate.setDateFromTimestampWithTime(args.record.timestamp)
        }
    }
}
