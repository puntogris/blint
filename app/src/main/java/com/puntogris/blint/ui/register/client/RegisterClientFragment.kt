package com.puntogris.blint.ui.register.client

import com.puntogris.blint.R
import com.puntogris.blint.databinding.FragmentRegisterBusinessBinding
import com.puntogris.blint.model.Product
import com.puntogris.blint.ui.base.BaseFragment
import com.puntogris.blint.utils.changeIconFromDrawable
import com.puntogris.blint.utils.createSnackBar
import com.puntogris.blint.utils.getParentFab
import com.puntogris.blint.utils.getString
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class RegisterClientFragment : BaseFragment<FragmentRegisterBusinessBinding>(R.layout.fragment_register_client) {

    override fun initializeViews() {
        getParentFab().apply {
            changeIconFromDrawable(R.drawable.ic_baseline_save_24)
            setOnClickListener {

            }
        }
    }
}