package com.puntogris.blint.ui.main

import android.content.Intent
import android.net.Uri
import androidx.core.view.isVisible
import androidx.navigation.fragment.findNavController
import com.puntogris.blint.R
import com.puntogris.blint.databinding.FragmentNewUserBinding
import com.puntogris.blint.ui.base.BaseFragment
import com.puntogris.blint.utils.*
import com.puntogris.blint.utils.Constants.BLINT_WEBSITE_LEARN_MORE

class NewUserFragment : BaseFragment<FragmentNewUserBinding>(R.layout.fragment_new_user) {

    override fun initializeViews() {
        setUpUi(showAppBar = false, showToolbar = false)
        binding.fragment = this
    }

    fun onLearnMoreClicked(){
        val url = BLINT_WEBSITE_LEARN_MORE
        val openBrowserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
        startActivity(openBrowserIntent)
    }
    fun onJoinBusinessClicked(){
        findNavController().navigate(R.id.joinBusinessFragment)
    }

    fun onCreateBusinessClicked(){
        findNavController().navigate(R.id.registerBusinessFragment)
    }
}