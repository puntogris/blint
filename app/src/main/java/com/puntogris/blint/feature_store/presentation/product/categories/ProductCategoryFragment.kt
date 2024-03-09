package com.puntogris.blint.feature_store.presentation.product.categories

import android.os.Bundle
import android.view.View
import androidx.core.os.bundleOf
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResult
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.puntogris.blint.R
import com.puntogris.blint.common.utils.Keys
import com.puntogris.blint.common.utils.launchAndRepeatWithViewLifecycle
import com.puntogris.blint.common.utils.registerToolbarBackButton
import com.puntogris.blint.common.utils.viewBinding
import com.puntogris.blint.databinding.FragmentProductCategoryBinding
import com.puntogris.blint.feature_store.domain.model.CheckableCategory
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ProductCategoryFragment : Fragment(R.layout.fragment_product_category) {

    private val viewModel: ProductCategoriesViewModel by viewModels()

    private val binding by viewBinding(FragmentProductCategoryBinding::bind)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        registerToolbarBackButton(binding.toolbar)
        setupCategoriesAdapter()
        setupListeners()
    }

    private fun setupListeners() {
        binding.buttonDone.setOnClickListener {
            onDoneButtonClicked()
        }
        binding.editTextSearchCategory.doAfterTextChanged { editable ->
            if (editable != null) {
                viewModel.setQuery(editable)
            }
        }
    }

    private fun setupCategoriesAdapter() {
        val adapter = ProductCategoryAdapter { onCategoryClicked(it) }
        binding.recyclerViewCategories.adapter = adapter
        subscribeUi(adapter)
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
        binding.recyclerViewCategories.adapter = null
        super.onDestroyView()
    }
}
