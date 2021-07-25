package com.puntogris.blint.ui.sync

import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.puntogris.blint.R
import com.puntogris.blint.databinding.FragmentSyncAccountBinding
import com.puntogris.blint.ui.SharedPref
import com.puntogris.blint.ui.base.BaseFragment
import com.puntogris.blint.utils.*
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class SyncAccountFragment : BaseFragment<FragmentSyncAccountBinding>(R.layout.fragment_sync_account) {

    private val viewModel: SyncViewModel by viewModels()
    private val args: SyncAccountFragmentArgs by navArgs()
    @Inject lateinit var sharedPref: SharedPref

    override fun initializeViews() {
        binding.fragment = this
        setUpUi(showFab = false, showAppBar = false, showToolbar = false)
        setupStatusBarForLoginBackground()
        launchAndRepeatWithViewLifecycle(Lifecycle.State.CREATED) {
            when(viewModel.syncAccount(args.userData)){
                is SyncAccount.Error -> {
                    showLongSnackBarAboveFab(getString(R.string.snack_an_error_occurred))
                }
                is SyncAccount.Success -> {
                    onSuccessSync()
                }
            }
        }
    }

    private fun onSuccessSync(){
        binding.animationView.playAnimationOnce(R.raw.done)
        binding.continueButton.isEnabled = true
        binding.subtitle.text = getString(R.string.account_adventure_message)
        binding.title.text = getString(R.string.account_sync_success)
    }

    fun onExitButtonClicked(){
        findNavController().navigate(R.id.loginFragment)
    }

    fun onContinueButtonClicked(){
        if (sharedPref.showNewUserScreenPref()) findNavController().navigate(R.id.newUserFragment)
        else findNavController().navigate(R.id.mainFragment)
    }
}