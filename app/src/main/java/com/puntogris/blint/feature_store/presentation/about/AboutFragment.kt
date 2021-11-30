package com.puntogris.blint.feature_store.presentation.about

import com.puntogris.blint.R
import com.puntogris.blint.common.presentation.base.BaseFragment
import com.puntogris.blint.common.utils.UiInterface
import com.puntogris.blint.databinding.FragmentAboutBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AboutFragment : BaseFragment<FragmentAboutBinding>(R.layout.fragment_about) {

    override fun initializeViews() {
        UiInterface.registerUi(showAppBar = false)
        binding.fragment = this
    }

}