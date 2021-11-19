package com.puntogris.blint.ui.categories.manage

import android.text.InputType
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.maxkeppeler.sheets.core.SheetStyle
import com.maxkeppeler.sheets.input.InputSheet
import com.maxkeppeler.sheets.input.type.InputEditText
import com.puntogris.blint.R
import com.puntogris.blint.databinding.FragmentManageCategoriesBinding
import com.puntogris.blint.ui.base.BaseFragment
import com.puntogris.blint.utils.UiInterface
import com.puntogris.blint.utils.hideKeyboard
import com.puntogris.blint.utils.launchAndRepeatWithViewLifecycle
import com.puntogris.blint.utils.types.SimpleResult
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ManageCategoriesFragment :
    BaseFragment<FragmentManageCategoriesBinding>(R.layout.fragment_manage_categories) {

    private val viewModel: ManageCategoriesViewModel by viewModels()

    override fun initializeViews() {
        binding.fragment = this
        UiInterface.registerUi(showFab = true) {
            showCreateCategoryDialog()
        }
        setupCategoriesAdapter()
    }

    private fun setupCategoriesAdapter() {
        ManageCategoriesAdapter(requireContext()) { onCategoryDeleted(it) }.let {
            binding.recyclerView.adapter = it
            subscribeUi(it)
        }
    }

    private fun subscribeUi(adapter: ManageCategoriesAdapter) {
        launchAndRepeatWithViewLifecycle {
            viewModel.getProductCategories().collect {
                adapter.submitData(it)
            }
        }
    }

    private fun showCreateCategoryDialog() {
        InputSheet().show(requireParentFragment().requireContext()) {
            style(SheetStyle.DIALOG)
            title(R.string.add_category)
            with(InputEditText {
                required(true)
                hint(R.string.name)
                inputType(InputType.TYPE_TEXT_FLAG_CAP_SENTENCES)
                onNegative(R.string.action_cancel)
                onPositive(R.string.action_accept) {
                    onSaveCategory(it["0"].toString())
                    hideKeyboard()
                }
            })
        }
    }

    private fun onSaveCategory(name: String) {
        lifecycleScope.launch {
            val result = when (viewModel.saveCategory(name)) {
                SimpleResult.Failure -> R.string.snack_category_create_error
                SimpleResult.Success -> R.string.snack_category_create_success
            }
            UiInterface.showSnackBar(getString(result))
        }
    }

    private fun onCategoryDeleted(name: String) {
        lifecycleScope.launch {
            val result = when (viewModel.deleteCategory(name)) {
                SimpleResult.Failure -> R.string.snack_categories_delete_error
                SimpleResult.Success -> R.string.snack_categories_delete_success
            }
            UiInterface.showSnackBar(getString(result))
        }
    }

    override fun onDestroyView() {
        binding.recyclerView.adapter = null
        super.onDestroyView()
    }
}