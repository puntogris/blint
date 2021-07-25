package com.puntogris.blint.ui.business

import android.annotation.SuppressLint
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import com.airbnb.lottie.LottieDrawable
import com.google.android.material.snackbar.Snackbar
import com.puntogris.blint.R
import com.puntogris.blint.databinding.FragmentJoinBusinessBinding
import com.puntogris.blint.ui.base.BaseFragment
import com.puntogris.blint.utils.*
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class JoinBusinessFragment : BaseFragment<FragmentJoinBusinessBinding>(R.layout.fragment_join_business) {

    private val viewModel: BusinessViewModel by viewModels()
    lateinit var requestPermissionLauncher: ActivityResultLauncher<String>

    @SuppressLint("ShowToast")
    override fun initializeViews() {
        binding.fragment = this
        registerUiInterface.register(showAppBar = true, showFab = true, showToolbar = false, fabIcon = R.drawable.ic_baseline_check_24){
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

        requestPermissionLauncher =
            registerForActivityResult(ActivityResultContracts.RequestPermission())
            { isGranted: Boolean ->
                if (isGranted) {
                    val action = JoinBusinessFragmentDirections.actionJoinBusinessFragmentToScannerFragment(1)
                    findNavController().navigate(action)
                }
                else showLongSnackBarAboveFab(getString(R.string.snack_require_camera_permission))
            }
    }

    fun navigateBackClicked(){
        findNavController().navigateUp()
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
                    binding.button2.visible()
                }
                JoinBusiness.InProgress -> { }
                JoinBusiness.CodeInvalid -> {
                    binding.animationView.playAnimationOnce(R.raw.error)
                    binding.fragmentTitle.text = getString(R.string.incorrect_code)
                    binding.summaryMessage.text = getString(R.string.incorrect_or_expired_code)
                    binding.button2.visible()
                }
                JoinBusiness.Success -> {
                    requireActivity().runOnUiThread {
                        binding.button8.visible()
                        binding.button8.setOnClickListener {
                            val nav = NavOptions.Builder().setPopUpTo(R.id.navigation, true).build()
                            findNavController().navigate(R.id.mainFragment, null, nav)
                        }
                        binding.fragmentTitle.text = getString(R.string.joined_business_success_title)
                        binding.summaryMessage.text = getString(R.string.joined_business_success_message)
                        binding.animationView.playAnimationOnce(R.raw.done)
                    }
                }
                JoinBusiness.AlreadyJoined -> {
                    binding.animationView.playAnimationOnce(R.raw.error)
                    binding.fragmentTitle.text = getString(R.string.employee_conflict)
                    binding.summaryMessage.text = getString(R.string.account_already_joined_business)
                    binding.button2.visible()
                }
            }
        }
    }

    fun onScannerButtonClicked(){
        requestPermissionLauncher.launch(android.Manifest.permission.CAMERA)
    }
}