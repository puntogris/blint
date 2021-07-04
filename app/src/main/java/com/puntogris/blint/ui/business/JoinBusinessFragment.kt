package com.puntogris.blint.ui.business

import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.airbnb.lottie.LottieDrawable
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

    override fun initializeViews() {
        binding.fragment = this
        getParentFab().apply {
            changeIconFromDrawable(R.drawable.ic_baseline_check_24)
            setOnClickListener {
                val text = binding.joinBusinessCodeText.getString()
                if(text.isEmpty()){
                    showLongSnackBarAboveFab(context.getString(R.string.snack_type_or_scan_code_to_continue))
                }else{
                    joinBusinessWithCode(text)
                }
            }
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
                    binding.animationView.apply {
                        setAnimation(R.raw.error)
                        repeatCount = 0
                        playAnimation()
                    }
                    binding.fragmentTitle.text = getString(R.string.snack_an_error_ocurred)
                    binding.summaryMessage.text = getString(R.string.snack_error_connection_server_try_later)
                }
                JoinBusiness.InProgress -> {}
                JoinBusiness.CodeInvalid -> {
                    binding.animationView.apply {
                        setAnimation(R.raw.error)
                        repeatCount = 0
                        playAnimation()
                    }
                    binding.fragmentTitle.text = getString(R.string.incorrect_code)
                    binding.summaryMessage.text = getString(R.string.incorrect_or_expired_code)
                }
                JoinBusiness.Success -> {
                    sharedPref.setUserHasBusinessPref(true)
                    binding.animationView.apply {
                        binding.animationView.apply {
                            setAnimation(R.raw.done)
                            repeatCount = 0
                            playAnimation()
                        }
                        setAnimation(R.raw.done)
                        repeatCount = 0
                        playAnimation()
                    }
                }
                JoinBusiness.AlreadyJoined -> {
                    binding.animationView.apply {
                        setAnimation(R.raw.error)
                        repeatCount = 0
                        playAnimation()
                    }
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