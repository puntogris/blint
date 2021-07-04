package com.puntogris.blint.ui.business

import androidx.navigation.fragment.navArgs
import com.maxkeppeler.sheets.info.InfoSheet
import com.puntogris.blint.R
import com.puntogris.blint.databinding.FragmentDeleteBusinessBinding
import com.puntogris.blint.ui.base.BaseFragment
import com.puntogris.blint.utils.changeIconFromDrawable
import com.puntogris.blint.utils.getParentFab
import com.puntogris.blint.utils.getString
import com.puntogris.blint.utils.showLongSnackBarAboveFab
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DeleteBusinessFragment : BaseFragment<FragmentDeleteBusinessBinding>(R.layout.fragment_delete_business) {

    private val args:DeleteBusinessFragmentArgs by navArgs()

    override fun initializeViews() {
        getParentFab().apply {
            changeIconFromDrawable(R.drawable.ic_baseline_delete_24)
            setOnClickListener {
                if (binding.businessNameText.getString() == args.business.businessName){
                    showDeleteBusinessUi()
                }else{
                    showLongSnackBarAboveFab(context.getString(R.string.snack_business_name_does_not_match))
                }
            }
        }
    }

    private fun showDeleteBusinessUi(){
        InfoSheet().build(requireContext()){
            title(getString(R.string.do_you_want_to_delete_business))
            content(getString(R.string.delete_business_warning_dialog))
            onNegative(getString(R.string.action_no)) {
                // Handle event
            }
            onPositive(getString(R.string.action_delete)) {
                // Handle event
            }
        }.show(parentFragmentManager, "")
    }
}