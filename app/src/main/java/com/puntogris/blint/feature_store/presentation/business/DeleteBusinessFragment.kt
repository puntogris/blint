package com.puntogris.blint.feature_store.presentation.business

import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.maxkeppeler.sheets.info.InfoSheet
import com.puntogris.blint.R
import com.puntogris.blint.common.presentation.base.BaseFragment
import com.puntogris.blint.common.utils.UiInterface
import com.puntogris.blint.common.utils.getString
import com.puntogris.blint.common.utils.types.DeleteBusiness
import com.puntogris.blint.databinding.FragmentDeleteBusinessBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class DeleteBusinessFragment :
    BaseFragment<FragmentDeleteBusinessBinding>(R.layout.fragment_delete_business) {

    private val args: DeleteBusinessFragmentArgs by navArgs()
    private val viewModel: BusinessViewModel by viewModels()

    override fun initializeViews() {
        binding.fragment = this
        UiInterface.registerUi(showAppBar = false)
    }

    fun onDeleteBusinessClicked() {
        if (binding.businessNameText.getString() == args.business.name) {
            showDeleteBusinessDialog()
        } else {
            UiInterface.showSnackBar(getString(R.string.snack_business_name_does_not_match))
        }
    }

    private fun showDeleteBusinessDialog() {
        InfoSheet().show(requireParentFragment().requireContext()) {
            title(R.string.do_you_want_to_delete_business)
            content(R.string.delete_local_business_warning_dialog)
            onNegative(R.string.action_no)
            onPositive(R.string.action_delete) {
                onDeleteConfirmation()
            }
        }
    }

    private fun onDeleteConfirmation() {
        lifecycleScope.launch {
            when (viewModel.deleteBusiness(args.business.businessId)) {
                DeleteBusiness.Failure -> {
                    UiInterface.showSnackBar(getString(R.string.snack_delete_business_error))
                }
                DeleteBusiness.Success.HasBusiness -> {
                    UiInterface.showSnackBar(getString(R.string.snack_delete_business_success))
                    findNavController().navigate(R.id.homeFragment)
                }
                DeleteBusiness.Success.NoBusiness -> {
                    UiInterface.showSnackBar(getString(R.string.snack_delete_business_success))
                    findNavController().navigate(R.id.newUserFragment)
                }
            }
        }
    }
}