package com.puntogris.blint.feature_store.presentation.settings

import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.puntogris.blint.R
import com.puntogris.blint.common.presentation.base.BaseFragment
import com.puntogris.blint.common.utils.*
import com.puntogris.blint.common.utils.types.ProgressResource
import com.puntogris.blint.common.utils.types.Resource
import com.puntogris.blint.databinding.FragmentDeleteAccountBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

@AndroidEntryPoint
class DeleteAccountFragment :
    BaseFragment<FragmentDeleteAccountBinding>(R.layout.fragment_delete_account) {

    private val viewModel: DeleteAccountViewModel by viewModels()

    override fun initializeViews() {
        binding.fragment = this
        registerToolbarBackButton(binding.toolbar)
    }

    fun onDeleteAccountClicked() {
        lifecycleScope.launch {
            with(binding) {
                viewModel.deleteAccount(binding.userEmail.getString()).collect {
                    when (it) {
                        is ProgressResource.Error -> {
                            deleteAccountAnimation.gone()
                            userEmail.visible()
                            UiInterface.showSnackBar(getString(it.error))
                        }
                        ProgressResource.InProgress -> {
                            deleteAccountAnimation.playAnimationInfinite(R.raw.loading)
                            userEmail.gone()
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