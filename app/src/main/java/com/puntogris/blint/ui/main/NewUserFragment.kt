package com.puntogris.blint.ui.main

import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import com.maxkeppeler.sheets.info.InfoSheet
import com.puntogris.blint.R
import com.puntogris.blint.databinding.FragmentNewUserBinding
import com.puntogris.blint.ui.base.BaseFragment
import com.puntogris.blint.ui.login.LoginViewModel
import com.puntogris.blint.utils.Constants.BLINT_WEBSITE_LEARN_MORE
import com.puntogris.blint.utils.UiInterface
import com.puntogris.blint.utils.launchWebBrowserIntent
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class NewUserFragment : BaseFragment<FragmentNewUserBinding>(R.layout.fragment_new_user) {

    private val viewModel: LoginViewModel by viewModels()

    override fun initializeViews() {
        binding.fragment = this
        UiInterface.apply {
            registerUi(showAppBar = true, showToolbar = false)
            setBottomAppBarInvisible()
        }
    }

    fun onLogOutClicked() {
        InfoSheet().show(requireParentFragment().requireContext()) {
            title("logout")
            content("sure you wnat to logout?")
            onNegative(R.string.action_cancel)
            onPositive(R.string.action_yes) {
                lifecycleScope.launch {
                    // viewModel.singOut()
                    val nav = NavOptions.Builder().setPopUpTo(R.id.navigation, true).build()
                    findNavController().navigate(R.id.loginFragment, null, nav)
                }
            }
        }

    }

    fun onLearnMoreClicked() {
        launchWebBrowserIntent(BLINT_WEBSITE_LEARN_MORE)
    }

    fun onCreateBusinessClicked() {
        findNavController().navigate(R.id.registerBusinessFragment)
    }
}