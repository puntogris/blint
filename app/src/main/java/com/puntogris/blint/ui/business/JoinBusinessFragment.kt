package com.puntogris.blint.ui.business

import android.annotation.SuppressLint
import android.view.View
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.airbnb.lottie.LottieDrawable
import com.google.android.material.snackbar.Snackbar
import com.puntogris.blint.R
import com.puntogris.blint.databinding.FragmentJoinBusinessBinding
import com.puntogris.blint.ui.SharedPref
import com.puntogris.blint.ui.base.BaseFragment
import com.puntogris.blint.utils.*
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class JoinBusinessFragment : BaseFragment<FragmentJoinBusinessBinding>(R.layout.fragment_join_business) {

    private val viewModel: BusinessViewModel by viewModels()
    @Inject lateinit var sharedPref: SharedPref

    @SuppressLint("ShowToast")
    override fun initializeViews() {
        binding.fragment = this
        setUpUi(showAppBar = true, showFab = true, showToolbar = false, fabIcon = R.drawable.ic_baseline_check_24){
            val text = binding.joinBusinessCodeText.getString()
            if(text.isBlank()){
                Snackbar.make(binding.root, getString(R.string.snack_type_or_scan_code_to_continue), Snackbar.LENGTH_LONG)
                    .setAnchorView(it)
                    .show()
            }else{
                joinBusinessWithCode(text)
            }
        }
        getParentBottomAppBar().apply {
            invisible()
            performHide()
        }

        findNavController().currentBackStackEntry?.savedStateHandle?.getLiveData<String>("key")?.observe(
            viewLifecycleOwner) {
            joinBusinessWithCode(it)
        }
    }

    private fun joinBusinessWithCode(code:String){
        getParentFab().hide()
        binding.setDataGroup.gone()
        binding.animationView.visible()
        binding.summaryMessage.visible()
        binding.fragmentTitle.text = getString(R.string.joining_business_progress)
        binding.animationView.apply {
            repeatCount = LottieDrawable.INFINITE
            visible()
            playAnimation()
        }
        lifecycleScope.launch {
            when(viewModel.createEmployee(code)){
                JoinBusiness.Error -> {
                    binding.animationView.playAnimationOnce(R.raw.error)
                    binding.fragmentTitle.text = getString(R.string.snack_an_error_occurred)
                    binding.summaryMessage.text = getString(R.string.snack_error_connection_server_try_later)
                }
                JoinBusiness.InProgress -> {

                }
                JoinBusiness.CodeInvalid -> {
                    binding.animationView.playAnimationOnce(R.raw.error)
                    binding.fragmentTitle.text = getString(R.string.incorrect_code)
                    binding.summaryMessage.text = getString(R.string.incorrect_or_expired_code)
                }
                JoinBusiness.Success -> {
                    sharedPref.setUserHasBusinessPref(true)
                    binding.animationView.playAnimationOnce(R.raw.done)
                }
                JoinBusiness.AlreadyJoined -> {
                    binding.animationView.playAnimationOnce(R.raw.error)
                    binding.fragmentTitle.text = getString(R.string.employee_conflict)
                    binding.summaryMessage.text = getString(R.string.account_already_joined_business)
                }
            }
        }
    }

    fun onScannerButtonClicked(){
        val action = JoinBusinessFragmentDirections.actionJoinBusinessFragmentToScannerFragment(1)
        findNavController().navigate(action)
    }
}