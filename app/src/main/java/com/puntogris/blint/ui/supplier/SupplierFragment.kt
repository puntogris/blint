package com.puntogris.blint.ui.supplier

import com.puntogris.blint.R
import com.puntogris.blint.databinding.FragmentSupplierBinding
import com.puntogris.blint.ui.base.BaseFragment
import com.puntogris.blint.utils.changeIconFromDrawable
import com.puntogris.blint.utils.getParentFab

class SupplierFragment : BaseFragment<FragmentSupplierBinding>(R.layout.fragment_supplier) {

    override fun initializeViews() {
        getParentFab().apply {
            changeIconFromDrawable(R.drawable.ic_baseline_save_24)
            setOnClickListener {

            }
        }
    }
}