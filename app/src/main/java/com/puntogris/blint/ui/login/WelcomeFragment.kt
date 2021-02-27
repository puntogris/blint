package com.puntogris.blint.ui.login

import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.puntogris.blint.R
import com.puntogris.blint.databinding.FragmentWelcomeBinding
import com.puntogris.blint.ui.base.BaseFragment
import java.util.*

class WelcomeFragment : BaseFragment<FragmentWelcomeBinding>(R.layout.fragment_welcome) {


    override fun initializeViews() {
        binding.button12.setOnClickListener {
            findNavController().navigate(R.id.introFragment)
        }
        binding.button112.setOnClickListener {
            findNavController().navigate(R.id.loginFragment)
        }
    }
}