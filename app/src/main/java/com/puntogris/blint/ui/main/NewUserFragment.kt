package com.puntogris.blint.ui.main

import androidx.navigation.fragment.findNavController
import com.puntogris.blint.R
import com.puntogris.blint.databinding.FragmentNewUserBinding
import com.puntogris.blint.ui.base.BaseFragment
import com.puntogris.blint.utils.*
import com.puntogris.blint.utils.Constants.BLINT_WEBSITE_LEARN_MORE

class NewUserFragment : BaseFragment<FragmentNewUserBinding>(R.layout.fragment_new_user) {

    override fun initializeViews() {
        binding.fragment = this
        registerUiInterface.apply {
            register(showAppBar = true, showToolbar = false)
            setBottomAppBarInvisible()
            setToolbarAndStatusBarColor(R.color.colorSecondary)
            setDarkStatusBar()
        }
    }

    fun onLearnMoreClicked(){
        launchWebBrowserIntent(BLINT_WEBSITE_LEARN_MORE)
    }
    fun onJoinBusinessClicked(){
        findNavController().navigate(R.id.joinBusinessFragment)
    }
    fun onCreateBusinessClicked(){
        findNavController().navigate(R.id.registerBusinessFragment)
    }
}