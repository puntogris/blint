package com.puntogris.blint.ui.business

import androidx.core.view.setPadding
import androidx.fragment.app.activityViewModels
import com.puntogris.blint.R
import com.puntogris.blint.databinding.FragmentLocalBusinessBinding
import com.puntogris.blint.ui.base.BaseFragment
import com.puntogris.blint.ui.main.MainViewModel
import com.puntogris.blint.ui.product.ProductFragment
import com.puntogris.blint.utils.*

class LocalBusinessFragment : BaseFragment<FragmentLocalBusinessBinding>(R.layout.fragment_local_business) {

    private val viewModel: MainViewModel by activityViewModels()

        override fun initializeViews() {
            binding.localBusinessFragment = this
            binding.animationView.setPadding(-400)
        }

        fun onRegisterBusinessButtonClicked(){
            val registerBusinessFragment = requireParentFragment() as RegisterBusinessFragment
            registerBusinessFragment.registerLocalBusiness()
        }
    }