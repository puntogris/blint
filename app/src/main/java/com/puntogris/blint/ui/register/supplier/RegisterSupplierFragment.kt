package com.puntogris.blint.ui.register.supplier

import com.puntogris.blint.R
import com.puntogris.blint.databinding.FragmentRegisterSupplierBinding
import com.puntogris.blint.ui.base.BaseFragment
import com.puntogris.blint.utils.changeIconFromDrawable
import com.puntogris.blint.utils.getParentFab

class RegisterSupplierFragment : BaseFragment<FragmentRegisterSupplierBinding>(R.layout.fragment_register_supplier) {

    override fun initializeViews() {
        getParentFab().apply {
            changeIconFromDrawable(R.drawable.ic_baseline_save_24)
            setOnClickListener {

            }
        }
    }
}