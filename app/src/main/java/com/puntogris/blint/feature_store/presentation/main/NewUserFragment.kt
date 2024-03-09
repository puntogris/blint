package com.puntogris.blint.feature_store.presentation.main

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.maxkeppeler.sheets.info.InfoSheet
import com.puntogris.blint.R
import com.puntogris.blint.common.utils.Constants
import com.puntogris.blint.common.utils.UiInterface
import com.puntogris.blint.common.utils.launchWebBrowserIntent
import com.puntogris.blint.common.utils.navigateAndClearStack
import com.puntogris.blint.common.utils.types.Resource
import com.puntogris.blint.common.utils.viewBinding
import com.puntogris.blint.databinding.FragmentNewUserBinding
import com.puntogris.blint.feature_auth.presentation.LoginViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class NewUserFragment : Fragment(R.layout.fragment_new_user) {

    private val viewModel: LoginViewModel by viewModels()

    private val binding by viewBinding(FragmentNewUserBinding::bind)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.buttonRegister.setOnClickListener {
            onCreateBusinessClicked()
        }
        binding.textViewReadMore.setOnClickListener {
            onLearnMoreClicked()
        }
        binding.imageViewCloseIcon.setOnClickListener {
            onSignOutClicked()
        }
    }

    fun onSignOutClicked() {
        InfoSheet().show(requireParentFragment().requireContext()) {
            title(R.string.sign_out_pref)
            content(R.string.ask_user_action_confirmation)
            onNegative(R.string.action_cancel)
            onPositive(R.string.action_yes) {
                onSignOutConfirmed()
            }
        }
    }

    private fun onSignOutConfirmed() {
        lifecycleScope.launch {
            when (viewModel.signOut()) {
                is Resource.Error -> {
                    UiInterface.showSnackBar(getString(R.string.snack_an_error_occurred))
                }

                is Resource.Success -> {
                    findNavController().navigateAndClearStack(R.id.loginFragment)
                }
            }
        }
    }

    fun onLearnMoreClicked() {
        launchWebBrowserIntent(Constants.APP_LEARN_MORE_URL)
    }

    fun onCreateBusinessClicked() {
        findNavController().navigate(R.id.registerStoreFragment)
    }
}
