package com.puntogris.blint.ui.login

import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.puntogris.blint.R
import com.puntogris.blint.databinding.FragmentWelcomeBinding
import com.puntogris.blint.ui.base.BaseFragment
import com.puntogris.blint.utils.registerUiInterface
import com.puntogris.blint.utils.setupStatusBarForLoginBackground
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class WelcomeFragment : BaseFragment<FragmentWelcomeBinding>(R.layout.fragment_welcome) {

    private val args: WelcomeFragmentArgs by navArgs()

    override fun initializeViews() {
        binding.fragment = this
        registerUiInterface.register(showFab = false, showAppBar = false, showToolbar = false)
        setupStatusBarForLoginBackground()
    }

    fun onExitButtonClicked(){
        findNavController().navigate(R.id.loginFragment)
    }

    fun onContinueButtonClicked(){
        if (args.showIntro){
            findNavController().navigate(R.id.introFragment)
        }else{
            val action = WelcomeFragmentDirections.actionWelcomeFragmentToFirstSyncFragment(args.userData)
            findNavController().navigate(action)
        }
    }
}