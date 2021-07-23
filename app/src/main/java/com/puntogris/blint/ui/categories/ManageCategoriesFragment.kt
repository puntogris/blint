package com.puntogris.blint.ui.categories

import android.text.InputType
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.maxkeppeler.sheets.core.SheetStyle
import com.maxkeppeler.sheets.info.InfoSheet
import com.maxkeppeler.sheets.input.InputSheet
import com.maxkeppeler.sheets.input.type.InputEditText
import com.puntogris.blint.R
import com.puntogris.blint.databinding.FragmentManageCategoriesBinding
import com.puntogris.blint.model.Category
import com.puntogris.blint.ui.base.BaseFragment
import com.puntogris.blint.ui.product.ProductViewModel
import com.puntogris.blint.utils.*
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ManageCategoriesFragment : BaseFragment<FragmentManageCategoriesBinding>(R.layout.fragment_manage_categories) {

    private val viewModel: ProductViewModel by viewModels()
    private lateinit var categoriesAdapter: CategoriesAdapter

    @ExperimentalCoroutinesApi
    override fun initializeViews() {
        binding.fragment = this
        setUpUi(showFab = true){ showCreateCategoryDialog() }

        categoriesAdapter = CategoriesAdapter{onCategoryClicked()}
        binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = categoriesAdapter
        }

        launchAndRepeatWithViewLifecycle {
            viewModel.getProductCategories().collect {
                categoriesAdapter.submitList(it)
            }
        }
    }

    fun onClearCategoriesSelected(){
        categoriesAdapter.clearSelected()
        showSelectionUi()
    }

    private fun onCategoryClicked(){
        showSelectionUi()
        categoriesAdapter.notifyDataSetChanged()
    }

    private fun showSelectionUi(){
        val amountChecked = categoriesAdapter.getAmountChecked()
        binding.apply {
            if (amountChecked == 0) {
                selectedCategoriesText.gone()
                imageView17.gone()
                imageView18.gone()
                imageView19.gone()
            }else{
                if(amountChecked == 1) binding.imageView18.visible()
                else binding.imageView18.gone()
                selectedCategoriesText.text = amountChecked.toString()
                selectedCategoriesText.visible()
                imageView17.visible()
                imageView19.visible()
            }
        }
    }

    fun onEditCategoryClicked(){
        val category = categoriesAdapter.getSelectedCategories().first()
        InputSheet().build(requireContext()){
            style(SheetStyle.DIALOG)
            title(this@ManageCategoriesFragment.getString(R.string.rename_category))
            with(InputEditText{
                defaultValue(category.name)
                required(true)
                hint(this@ManageCategoriesFragment.getString(R.string.name))
                onNegative(this@ManageCategoriesFragment.getString(R.string.action_cancel))
                onPositive(this@ManageCategoriesFragment.getString(R.string.action_accept)){
                    category.name = it["0"].toString()
                    onEditCategoryConfirmed(category)
                }
            })
        }.show(parentFragmentManager, "")
    }

    private fun onEditCategoryConfirmed(category: Category){
        lifecycleScope.launch {
            when(viewModel.updateCategoryDatabase(category)){
                SimpleResult.Failure ->
                    showLongSnackBarAboveFab(getString(R.string.snack_category_modify_error))
                SimpleResult.Success ->
                    showLongSnackBarAboveFab(getString(R.string.snack_category_modify_success))
            }
        }
    }

    private fun showCreateCategoryDialog(){
        InputSheet().build(requireContext()){
            style(SheetStyle.DIALOG)
            title(this@ManageCategoriesFragment.getString(R.string.add_category))
            with(InputEditText{
                required(true)
                hint(this@ManageCategoriesFragment.getString(R.string.name))
                inputType(InputType.TYPE_TEXT_FLAG_CAP_SENTENCES)
                onNegative(this@ManageCategoriesFragment.getString(R.string.action_cancel))
                onPositive(this@ManageCategoriesFragment.getString(R.string.action_accept)){
                    onCreateCategoryConfirmed(it["0"].toString())
                    hideKeyboard()
                }
            })
        }.show(parentFragmentManager, "")
    }

    private fun onCreateCategoryConfirmed(name: String){
        lifecycleScope.launch {
            when(viewModel.saveCategoryDatabase(name)){
                SimpleResult.Failure ->
                    showLongSnackBarAboveFab(getString(R.string.snack_category_create_error))
                SimpleResult.Success ->
                    showLongSnackBarAboveFab(getString(R.string.snack_category_create_success))
            }
        }
    }

    fun onDeleteCategoriesClicked(){
        InfoSheet().build(requireContext()) {
            title(this@ManageCategoriesFragment.getString(R.string.delete_category))
            content(this@ManageCategoriesFragment.getString(R.string.delete_categories_warning))
            onNegative(this@ManageCategoriesFragment.getString(R.string.action_cancel))
            onPositive(this@ManageCategoriesFragment.getString(R.string.action_delete)) { onDeleteCategoryConfirmed() }
        }.show(parentFragmentManager, "")
    }

    private fun onDeleteCategoryConfirmed(){
        lifecycleScope.launch {
            when(viewModel.deleteCategory(categoriesAdapter.getSelectedCategories())){
                SimpleResult.Failure ->
                    showLongSnackBarAboveFab(getString(R.string.snack_categories_delete_error))
                SimpleResult.Success ->{
                    showSelectionUi()
                    categoriesAdapter.notifyItemRemoved(0)
                    categoriesAdapter.clearSelected()
                    categoriesAdapter.notifyDataSetChanged()
                    showLongSnackBarAboveFab(getString(R.string.snack_categories_delete_success))
                }
            }
        }
    }

    override fun onDestroyView() {
        binding.recyclerView.adapter = null
        super.onDestroyView()
    }
}