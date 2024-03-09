package com.puntogris.blint.feature_store.presentation.settings

import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.puntogris.blint.R
import com.puntogris.blint.common.presentation.base.BaseFragment
import com.puntogris.blint.common.utils.UiInterface
import com.puntogris.blint.common.utils.getString
import com.puntogris.blint.common.utils.gone
import com.puntogris.blint.common.utils.navigateAndClearStack
import com.puntogris.blint.common.utils.playAnimationInfinite
import com.puntogris.blint.common.utils.registerToolbarBackButton
import com.puntogris.blint.common.utils.types.ProgressResource
import com.puntogris.blint.common.utils.types.Resource
import com.puntogris.blint.common.utils.visible
import com.puntogris.blint.databinding.FragmentDeleteAccountBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class DeleteAccountFragment :
    BaseFragment<FragmentDeleteAccountBinding>(R.layout.fragment_delete_account) {

    private val viewModel: DeleteAccountViewModel by viewModels()

    override fun initializeViews() {
        registerToolbarBackButton(binding.toolbar)
        setupListeners()
    }

    private fun setupListeners() {
        binding.buttonDeleteAccount.setOnClickListener {
            onDeleteAccountClicked()
        }
    }

    fun onDeleteAccountClicked() {
        lifecycleScope.launch {
            with(binding) {
                viewModel.deleteAccount(binding.editTextAccountEmail.getString()).collect {
                    when (it) {
                        is ProgressResource.Error -> {
                            viewAnimation.gone()
                            inputLayoutAccountEmail.visible()
                            UiInterface.showSnackBar(getString(it.error))
                        }

                        ProgressResource.InProgress -> {
                            viewAnimation.playAnimationInfinite(R.raw.loading)
                            inputLayoutAccountEmail.gone()
                        }

                        is ProgressResource.Success -> signOutUser()
                    }
                }
            }
        }
    }

    private fun signOutUser() {
        lifecycleScope.launch {
            when (viewModel.signOut()) {
                is Resource.Error -> {
                    UiInterface.showSnackBar(getString(R.string.snack_an_error_occurred))
                }

                is Resource.Success -> {
                    findNavController().navigateAndClearStack(R.id.loginFragment)
                    UiInterface.showSnackBar(getString(R.string.snack_account_deleted_success))
                }
            }
        }
    }
}
