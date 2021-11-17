package com.puntogris.blint.ui.categories.manage

import android.text.InputType
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.maxkeppeler.sheets.core.SheetStyle
import com.maxkeppeler.sheets.input.InputSheet
import com.maxkeppeler.sheets.input.type.InputEditText
import com.puntogris.blint.R
import com.puntogris.blint.databinding.FragmentManageCategoriesBinding
import com.puntogris.blint.model.Category
import com.puntogris.blint.ui.base.BaseFragment
import com.puntogris.blint.utils.*
import com.puntogris.blint.utils.types.RepoResult
import com.puntogris.blint.utils.types.SimpleResult
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ManageCategoriesFragment :
    BaseFragment<FragmentManageCategoriesBinding>(R.layout.fragment_manage_categories) {

    private val viewModel: ManageCategoriesViewModel by viewModels()
    private lateinit var categoriesAdapter: ManageCategoriesAdapter

    @ExperimentalCoroutinesApi
    override fun initializeViews() {
        binding.fragment = this
        UiInterface.registerUi(showFab = true) { showCreateCategoryDialog() }

        categoriesAdapter = ManageCategoriesAdapter(requireContext()) { onCategoryDeleted(it) }
        binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = categoriesAdapter
        }

        launchAndRepeatWithViewLifecycle {
            when (val result = viewModel.getProductCategories()) {
                is RepoResult.Error -> {}
                RepoResult.InProgress -> {}
                is RepoResult.Success -> {
                    categoriesAdapter.updateList(result.data)
                }
            }
        }
    }

    private fun onCategoryDeleted(name: String) {
        lifecycleScope.launch {
            when (viewModel.deleteCategory(name)) {
                SimpleResult.Failure ->
                    UiInterface.showSnackBar(getString(R.string.snack_categories_delete_error))
                SimpleResult.Success ->
                    UiInterface.showSnackBar(getString(R.string.snack_categories_delete_success))
            }
        }
    }

    private fun showCreateCategoryDialog() {
        InputSheet().build(requireContext()) {
            style(SheetStyle.DIALOG)
            title(this@ManageCategoriesFragment.getString(R.string.add_category))
            with(InputEditText {
                required(true)
                hint(this@ManageCategoriesFragment.getString(R.string.name))
                inputType(InputType.TYPE_TEXT_FLAG_CAP_SENTENCES)
                onNegative(this@ManageCategoriesFragment.getString(R.string.action_cancel))
                onPositive(this@ManageCategoriesFragment.getString(R.string.action_accept)) {
                    onCreateCategoryConfirmed(it["0"].toString())
                    hideKeyboard()
                }
            })
        }.show(parentFragmentManager, "")
    }

    private fun onCreateCategoryConfirmed(name: String) {
        lifecycleScope.launch {
            when (viewModel.saveCategoryDatabase(name)) {
                SimpleResult.Failure ->
                    UiInterface.showSnackBar(getString(R.string.snack_category_create_error))
                SimpleResult.Success -> {
                    categoriesAdapter.addCategory(Category(name))
                    UiInterface.showSnackBar(getString(R.string.snack_category_create_success))
                }
            }
        }
    }

    override fun onDestroyView() {
        binding.recyclerView.adapter = null
        super.onDestroyView()
    }
}