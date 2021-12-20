package com.puntogris.blint.feature_store.presentation.product.categories

import androidx.core.os.bundleOf
import androidx.fragment.app.setFragmentResult
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.puntogris.blint.R
import com.puntogris.blint.common.presentation.base.BaseFragment
import com.puntogris.blint.common.utils.Keys
import com.puntogris.blint.common.utils.launchAndRepeatWithViewLifecycle
import com.puntogris.blint.common.utils.registerToolbarBackButton
import com.puntogris.blint.databinding.FragmentProductCategoryBinding
import com.puntogris.blint.feature_store.domain.model.CheckableCategory
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect

@AndroidEntryPoint
class ProductCategoryFragment :
    BaseFragment<FragmentProductCategoryBinding>(R.layout.fragment_product_category) {

    private val viewModel: ProductCategoriesViewModel by viewModels()

    override fun initializeViews() {
        binding.fragment = this
        binding.viewModel = viewModel

        registerToolbarBackButton(binding.productCategoryToolbar)
        setupCategoriesAdapter()
    }

    private fun setupCategoriesAdapter() {
        ProductCategoryAdapter { onCategoryClicked(it) }.let {
            binding.productCategoryRecyclerView.adapter = it
            subscribeUi(it)
        }
    }

    private fun subscribeUi(adapter: ProductCategoryAdapter) {
        launchAndRepeatWithViewLifecycle {
            viewModel.categoriesFlow.collect {
                adapter.submitData(it)
            }
        }
    }

    private fun onCategoryClicked(category: CheckableCategory) {
        viewModel.toggleCategory(category.category)
    }

    fun onDoneButtonClicked() {
        setFragmentResult(
            Keys.EDIT_PRODUCT_KEY,
            bundleOf(Keys.PRODUCT_CATEGORIES_KEY to viewModel.getFinalCategories())
        )
        findNavController().navigateUp()
    }

    override fun onDestroyView() {
        binding.productCategoryRecyclerView.adapter = null
        super.onDestroyView()
    }
}