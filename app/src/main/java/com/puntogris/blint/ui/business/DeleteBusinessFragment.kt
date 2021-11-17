package com.puntogris.blint.ui.business

import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.maxkeppeler.sheets.info.InfoSheet
import com.puntogris.blint.R
import com.puntogris.blint.databinding.FragmentDeleteBusinessBinding
import com.puntogris.blint.ui.base.BaseFragment
import com.puntogris.blint.utils.Constants.LOCAL
import com.puntogris.blint.utils.UiInterface
import com.puntogris.blint.utils.getString
import com.puntogris.blint.utils.types.DeleteBusiness
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class DeleteBusinessFragment :
    BaseFragment<FragmentDeleteBusinessBinding>(R.layout.fragment_delete_business) {

    private val args: DeleteBusinessFragmentArgs by navArgs()
    private val viewModel: BusinessViewModel by viewModels()

    override fun initializeViews() {
        UiInterface.registerUi(
            showFab = true,
            showAppBar = false,
            fabIcon = R.drawable.ic_baseline_delete_24
        ) {
            if (binding.businessNameText.getString() == args.business.name) {
                showDeleteBusinessUi()
            } else {
                UiInterface.showSnackBar(getString(R.string.snack_business_name_does_not_match))
            }
        }

        binding.textView120.text = getString(R.string.delete_local_business_warning)
    }

    private fun showDeleteBusinessUi() {
        InfoSheet().show(requireParentFragment().requireContext()) {
            title(R.string.do_you_want_to_delete_business)
            content(
                if (args.business.type == LOCAL) R.string.delete_local_business_warning_dialog
                else R.string.delete_business_warning_dialog
            )
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
                    findNavController().navigate(R.id.mainFragment)
                }
                DeleteBusiness.Success.NoBusiness -> {
                    UiInterface.showSnackBar(getString(R.string.snack_delete_business_success))
                    findNavController().navigate(R.id.newUserFragment)
                }
            }
        }
    }
}