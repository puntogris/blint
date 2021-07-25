package com.puntogris.blint.ui.business

import androidx.core.view.setPadding
import androidx.fragment.app.activityViewModels
import com.puntogris.blint.R
import com.puntogris.blint.databinding.FragmentLocalBusinessBinding
import com.puntogris.blint.ui.base.BaseFragment
import com.puntogris.blint.ui.business.register_business.RegisterBusinessFragment
import com.puntogris.blint.ui.main.MainViewModel
import com.puntogris.blint.utils.*
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi

@AndroidEntryPoint
class LocalBusinessFragment : BaseFragment<FragmentLocalBusinessBinding>(R.layout.fragment_local_business) {

        override fun initializeViews() {
            setUpUi(showAppBar = false)
            binding.localBusinessFragment = this
            binding.animationView.setPadding(-400)
        }

        fun onRegisterBusinessButtonClicked(){
            val registerBusinessFragment = requireParentFragment() as RegisterBusinessFragment
            registerBusinessFragment.registerLocalBusiness()
        }
    }