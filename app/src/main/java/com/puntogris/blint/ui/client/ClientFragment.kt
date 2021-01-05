package com.puntogris.blint.ui.client

import com.puntogris.blint.R
import com.puntogris.blint.databinding.FragmentClientBinding
import com.puntogris.blint.databinding.FragmentRegisterBusinessBinding
import com.puntogris.blint.ui.base.BaseFragment
import com.puntogris.blint.utils.changeIconFromDrawable
import com.puntogris.blint.utils.getParentFab
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ClientFragment : BaseFragment<FragmentClientBinding>(R.layout.fragment_client) {

    override fun initializeViews() {
        getParentFab().apply {
            changeIconFromDrawable(R.drawable.ic_baseline_save_24)
            setOnClickListener {

            }
        }
    }
}