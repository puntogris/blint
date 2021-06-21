package com.puntogris.blint.ui.debt

import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import com.puntogris.blint.R
import com.puntogris.blint.databinding.SelectTraderDebtBottomSheetBinding
import com.puntogris.blint.ui.base.BaseBottomSheetFragment
import com.puntogris.blint.utils.Constants.CLIENT_DEBT
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SelectTraderForDebt :BaseBottomSheetFragment<SelectTraderDebtBottomSheetBinding>(R.layout.select_trader_debt_bottom_sheet) {

    private val args:SelectTraderForDebtArgs by navArgs()
    private val viewModel: DebtViewModel by viewModels()

    override fun initializeViews() {
        val adapter = SimpleDebtAdapter{}
        if (args.debtType == CLIENT_DEBT){

        }else{

        }
    }

}