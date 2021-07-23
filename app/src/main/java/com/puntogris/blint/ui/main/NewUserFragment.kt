package com.puntogris.blint.ui.main

import android.content.Intent
import android.net.Uri
import android.view.View
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.puntogris.blint.R
import com.puntogris.blint.databinding.FragmentNewUserBinding
import com.puntogris.blint.ui.base.BaseFragment
import com.puntogris.blint.utils.*
import com.puntogris.blint.utils.Constants.BLINT_WEBSITE_LEARN_MORE

class NewUserFragment : BaseFragment<FragmentNewUserBinding>(R.layout.fragment_new_user) {

    override fun initializeViews() {
        binding.fragment = this
        setUpUi(showAppBar = true, showToolbar = false)
        getParentBottomAppBar().apply {
            invisible()
            performHide()
        }
        val view = requireActivity().window.decorView
        ContextCompat.getColor(requireContext(), R.color.colorSecondary).apply {
            requireActivity().window.statusBarColor = this
        }
        view.systemUiVisibility = (view.systemUiVisibility and View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR.inv())
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