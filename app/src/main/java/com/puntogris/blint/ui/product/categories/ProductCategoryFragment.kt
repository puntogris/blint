package com.puntogris.blint.ui.product.categories

import androidx.core.os.bundleOf
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.setFragmentResult
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.puntogris.blint.R
import com.puntogris.blint.databinding.FragmentProductCategoryBinding
import com.puntogris.blint.model.CheckableCategory
import com.puntogris.blint.ui.base.BaseFragment
import com.puntogris.blint.utils.Constants
import com.puntogris.blint.utils.UiInterface
import com.puntogris.blint.utils.registerToolbarBackButton
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ProductCategoryFragment :
    BaseFragment<FragmentProductCategoryBinding>(R.layout.fragment_product_category) {

    private val viewModel: ProductCategoriesViewModel by viewModels()

    override fun initializeViews() {
        binding.fragment = this

        UiInterface.registerUi(showAppBar = false, showToolbar = false)

        registerToolbarBackButton(binding.searchToolbar)
        setupSearchListener()
        setupCategoriesAdapter()
    }

    private fun setupSearchListener() {
        binding.categoriesSearch.addTextChangedListener {
            viewModel.setQuery(it.toString())
        }
    }

    private fun setupCategoriesAdapter() {
        ProductCategoryAdapter { onCategoryClicked(it) }.let {
            binding.recyclerView.adapter = it
            subscribeUi(it)
        }
    }

    private fun subscribeUi(adapter: ProductCategoryAdapter) {
        viewModel.categoriesLiveData.observe(viewLifecycleOwner) {
            adapter.submitData(viewLifecycleOwner.lifecycle, it)
        }
    }

    private fun onCategoryClicked(category: CheckableCategory) {
        viewModel.toggleCategory(category.category)
    }

    fun onDoneButtonClicked() {
        setFragmentResult(
            Constants.EDIT_FRAGMENT_RESULTS_KEY,
            bundleOf(Constants.PRODUCT_CATEGORIES_KEY to viewModel.getFinalCategories())
        )
        findNavController().navigateUp()
    }

    override fun onDestroyView() {
        binding.recyclerView.adapter = null
        super.onDestroyView()
    }
}