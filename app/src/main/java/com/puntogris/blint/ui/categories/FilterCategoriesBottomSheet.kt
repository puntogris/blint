package com.puntogris.blint.ui.categories

import android.view.View
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.google.android.material.chip.Chip
import com.puntogris.blint.R
import com.puntogris.blint.databinding.FilterCategoriesBottomSheetBinding
import com.puntogris.blint.ui.base.BaseBottomSheetFragment
import com.puntogris.blint.utils.Constants.CATEGORY_FILTER_KEY
import com.puntogris.blint.utils.RepoResult
import com.puntogris.blint.utils.gone
import com.puntogris.blint.utils.launchAndRepeatWithViewLifecycle
import com.puntogris.blint.utils.setProductCategoriesChip
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class FilterCategoriesBottomSheet: BaseBottomSheetFragment<FilterCategoriesBottomSheetBinding>(R.layout.filter_categories_bottom_sheet) {

    private val viewModel: FilterCategoriesViewModel by viewModels()

    override fun initializeViews() {
        binding.lifecycleOwner = viewLifecycleOwner
        binding.viewModel = viewModel

        launchAndRepeatWithViewLifecycle {
            when(val result = viewModel.getAllCategories()){
                is RepoResult.Error -> {
                    binding.progressBar3.gone()
                }
                RepoResult.InProgress -> {}
                is RepoResult.Success -> {
                    binding.progressBar3.gone()
                    result.data.forEach { category ->
                        val chip = Chip(requireContext())
                        chip.text = category.categoryName
                        chip.setOnClickListener {
                            onChipClicked(category.categoryName)
                        }
                        binding.categoriesChipGroup.addView(chip)
                    }
                }
            }
        }
    }

    private fun onChipClicked(category: String){
        findNavController().apply {
            previousBackStackEntry!!.savedStateHandle.set(CATEGORY_FILTER_KEY, category)
            popBackStack()
        }
    }
}