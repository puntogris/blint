package com.puntogris.blint.ui.categories

import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.google.android.material.chip.Chip
import com.maxkeppeler.sheets.info.InfoSheet
import com.puntogris.blint.R
import com.puntogris.blint.databinding.FragmentManageCategoriesBinding
import com.puntogris.blint.model.Category
import com.puntogris.blint.ui.base.BaseFragment
import com.puntogris.blint.ui.product.ProductViewModel
import com.puntogris.blint.utils.getString
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ManageCategoriesFragment : BaseFragment<FragmentManageCategoriesBinding>(R.layout.fragment_manage_categories) {

    private val viewModel: ProductViewModel by viewModels()

    override fun initializeViews() {
        lifecycleScope.launchWhenStarted {
            viewModel.getAllCategoriesFlow().collect {
                if (it.isNullOrEmpty()) showEmptyUi()
                else showChipsUi(it)
            }
        }
        binding.button11.setOnClickListener {
            lifecycleScope.launch {
                val name = binding.categoryNameText.getString()
                viewModel.insertCategory(name)
            }
        }
    }

    private fun showChipsUi(categories:List<Category>){
        binding.categoriesChipGroup.removeAllViews()
        categories.forEach { category ->
            Chip(requireContext()).apply {
                text = category.name
                setOnClickListener {
                    InfoSheet().build(requireContext()) {
                        title("Eliminar categoria")
                        content("Estas por eliminar la categoria '${category.name}', esta accion es irreversible.")
                        onNegative("No")
                        onPositive("Eliminar") {
                            this@ManageCategoriesFragment.lifecycleScope.launch {
                                this@ManageCategoriesFragment.viewModel.deleteCategory(category)
                            }
                        }
                    }.show(parentFragmentManager, "")
                }
                binding.categoriesChipGroup.addView(this)
            }
        }
    }

    private fun showEmptyUi(){

    }
}