package com.puntogris.blint.feature_store.presentation.orders

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.core.view.WindowInsetsControllerCompat
import androidx.navigation.fragment.navArgs
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.puntogris.blint.R
import com.puntogris.blint.common.utils.isDarkThemeOn
import com.puntogris.blint.common.utils.setDateFromTimestampWithTime
import com.puntogris.blint.common.utils.viewBinding
import com.puntogris.blint.databinding.BottomSheetInitialRecordBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class InitialRecordBottomSheet : BottomSheetDialogFragment() {

    private val binding by viewBinding(BottomSheetInitialRecordBinding::bind)

    private val args: InitialRecordBottomSheetArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.bottom_sheet_initial_record, container, false)
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return (super.onCreateDialog(savedInstanceState) as BottomSheetDialog).apply {
            window?.let {
                it.clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND)
                WindowInsetsControllerCompat(it, it.decorView).isAppearanceLightStatusBars =
                    !isDarkThemeOn()
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        with(binding) {
            textViewProductName.text = args.record.productName
            textViewProductAmount.text = args.record.amount.toString()
            setDateFromTimestampWithTime(textViewProductDate, args.record.timestamp)
        }
    }
}
