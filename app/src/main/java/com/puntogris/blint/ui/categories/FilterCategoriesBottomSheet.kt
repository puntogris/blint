package com.puntogris.blint.ui.categories

import androidx.fragment.app.viewModels
import com.puntogris.blint.R
import com.puntogris.blint.databinding.FilterCategoriesBottomSheetBinding
import com.puntogris.blint.ui.base.BaseBottomSheetFragment
import com.puntogris.blint.utils.gone
import com.puntogris.blint.utils.visible
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class FilterCategoriesBottomSheet: BaseBottomSheetFragment<FilterCategoriesBottomSheetBinding>(R.layout.filter_categories_bottom_sheet) {

    private val viewModel: CategoriesViewModel by viewModels()

    override fun initializeViews() {



        binding.categoriesChipGroup.visible()
        binding.progressBar3.gone()
    }
}