package com.puntogris.blint.feature_store.presentation.categories

import android.os.Bundle
import android.text.InputType
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.maxkeppeler.sheets.core.SheetStyle
import com.maxkeppeler.sheets.input.InputSheet
import com.maxkeppeler.sheets.input.type.InputEditText
import com.puntogris.blint.R
import com.puntogris.blint.common.utils.UiInterface
import com.puntogris.blint.common.utils.hideKeyboard
import com.puntogris.blint.common.utils.launchAndRepeatWithViewLifecycle
import com.puntogris.blint.common.utils.registerToolbarBackButton
import com.puntogris.blint.common.utils.showEmptyUiOnEmptyAdapter
import com.puntogris.blint.common.utils.types.Resource
import com.puntogris.blint.common.utils.viewBinding
import com.puntogris.blint.databinding.FragmentManageCategoriesBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ManageCategoriesFragment : Fragment(R.layout.fragment_manage_categories) {

    private val viewModel: ManageCategoriesViewModel by viewModels()

    private val binding by viewBinding(FragmentManageCategoriesBinding::bind)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupCategoriesAdapter()
        setupToolbar()
    }

    private fun setupCategoriesAdapter() {
        val adapter = ManageCategoriesAdapter(requireContext()) { onCategoryDeleted(it) }
        binding.recyclerViewCategories.adapter = adapter
        subscribeUi(adapter)
    }

    private fun subscribeUi(adapter: ManageCategoriesAdapter) {
        launchAndRepeatWithViewLifecycle {
            viewModel.getProductCategories().collect {
                adapter.submitData(it)
            }
        }
        launchAndRepeatWithViewLifecycle {
            showEmptyUiOnEmptyAdapter(adapter, binding.emptyUi.root)
        }
    }

    private fun setupToolbar() {
        with(binding.toolbar) {
            registerToolbarBackButton(this)
            setOnMenuItemClickListener {
                if (it.itemId == R.id.action_menu_item_add) {
                    showCreateCategoryDialog()
                }
                true
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
                is Resource.Error -> R.string.snack_category_create_error
                is Resource.Success -> R.string.snack_category_create_success
            }
            UiInterface.showSnackBar(getString(result))
        }
    }

    private fun onCategoryDeleted(name: String) {
        lifecycleScope.launch {
            val result = when (viewModel.deleteCategory(name)) {
                is Resource.Error -> R.string.snack_categories_delete_error
                is Resource.Success -> R.string.snack_categories_delete_success
            }
            UiInterface.showSnackBar(getString(result))
        }
    }

    override fun onDestroyView() {
        binding.recyclerViewCategories.adapter = null
        super.onDestroyView()
    }
}
