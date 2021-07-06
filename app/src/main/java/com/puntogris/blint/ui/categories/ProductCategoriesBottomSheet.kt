package com.puntogris.blint.ui.categories

import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.maxkeppeler.sheets.options.DisplayMode
import com.maxkeppeler.sheets.options.Option
import com.maxkeppeler.sheets.options.OptionsSheet
import com.puntogris.blint.R
import com.puntogris.blint.databinding.ProductCategoriesBottomSheetBinding
import com.puntogris.blint.model.Category
import com.puntogris.blint.ui.base.BaseBottomSheetFragment
import com.puntogris.blint.ui.product.ProductViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

@ExperimentalCoroutinesApi
@AndroidEntryPoint
class ProductCategoriesBottomSheet: BaseBottomSheetFragment<ProductCategoriesBottomSheetBinding>(R.layout.product_categories_bottom_sheet) {

    private val viewModel: ProductViewModel by viewModels()

    override fun initializeViews() {

        val adapter = CategoriesAdapter{ onCategoryClicked()}
        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())

        lifecycleScope.launch {
            viewModel.getProductCategories().collect {


//                val categories = args.productWithSuppCate?.categories?.map { it.categoryId }
//                val options = it.map { category ->
//                    if (!categories.isNullOrEmpty() && category.categoryId in  categories)
//                        Option(category.name).select()
//                    else
//                        Option(category.name)
//                }.toMutableList()

//                findNavController().apply {
//                    previousBackStackEntry!!.savedStateHandle.set("categories_key", listOf(Category(name = "a"), Category(name = "b")))
//                    popBackStack()
//                }

            }
        }
    }
    private fun onCategoryClicked(){

    }

}