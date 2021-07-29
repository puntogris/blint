package com.puntogris.blint.ui.main

import androidx.core.content.ContextCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.navigation.fragment.findNavController
import com.puntogris.blint.R
import com.puntogris.blint.databinding.FragmentNewUserBinding
import com.puntogris.blint.ui.base.BaseFragment
import com.puntogris.blint.utils.*
import com.puntogris.blint.utils.Constants.BLINT_WEBSITE_LEARN_MORE

class NewUserFragment : BaseFragment<FragmentNewUserBinding>(R.layout.fragment_new_user) {

    override fun initializeViews() {
        binding.fragment = this
        registerUiInterface.register(showAppBar = true, showToolbar = false)
        (requireActivity() as SetupUiListener).setBottomAppBarInvisible()
        ContextCompat.getColor(requireContext(), R.color.colorSecondary).apply {
            requireActivity().window.statusBarColor = this
        }
        val view = requireActivity().window
        val wic = WindowInsetsControllerCompat(view, view.decorView)
        wic.isAppearanceLightStatusBars = false
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