package com.puntogris.blint.ui.login

import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.puntogris.blint.R
import com.puntogris.blint.databinding.FragmentWelcomeBinding
import com.puntogris.blint.ui.base.BaseFragment
import dagger.hilt.android.AndroidEntryPoint
import java.util.*

@AndroidEntryPoint
class WelcomeFragment : BaseFragment<FragmentWelcomeBinding>(R.layout.fragment_welcome) {

    private val args: WelcomeFragmentArgs by navArgs()

    override fun initializeViews() {

        binding.button12.setOnClickListener {
            if (args.showIntro) findNavController().navigate(R.id.introFragment)
            else {
                val action = WelcomeFragmentDirections.actionWelcomeFragmentToFirstSyncFragment(args.username, args.userCountry)
                findNavController().navigate(action)
            }
        }
        binding.button112.setOnClickListener {
            findNavController().navigate(R.id.loginFragment)
        }
    }
}