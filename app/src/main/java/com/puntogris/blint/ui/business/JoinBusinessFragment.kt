package com.puntogris.blint.ui.business

import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.puntogris.blint.R
import com.puntogris.blint.databinding.FragmentJoinBusinessBinding
import com.puntogris.blint.ui.base.BaseFragment
import com.puntogris.blint.ui.login.LoginViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class JoinBusinessFragment : BaseFragment<FragmentJoinBusinessBinding>(R.layout.fragment_join_business) {

    private val viewModel: LoginViewModel by viewModels()

    override fun initializeViews() {
        binding.fragment = this
    }

    fun onCloseButtonClicked(){
        viewModel.singOut()
        findNavController().navigate(R.id.mainFragment)
    }
}