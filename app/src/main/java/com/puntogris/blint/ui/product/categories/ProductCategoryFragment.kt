package com.puntogris.blint.ui.product.categories

import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.puntogris.blint.R
import com.puntogris.blint.databinding.FragmentProductCategoryBinding
import com.puntogris.blint.model.Category
import com.puntogris.blint.ui.base.BaseFragment
import com.puntogris.blint.utils.UiInterface
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ProductCategoryFragment :
    BaseFragment<FragmentProductCategoryBinding>(R.layout.fragment_product_category) {

    private val viewModel: ProductCategoriesViewModel by viewModels()
    private val args: ProductCategoryFragmentArgs by navArgs()
    private var items = mutableListOf<Category>()

    override fun initializeViews() {
        binding.searchToolbar.setNavigationOnClickListener { findNavController().navigateUp() }

        UiInterface.registerUi(
            showFab = true,
            showAppBar = false,
            fabIcon = R.drawable.ic_baseline_arrow_forward_24,
            showToolbar = false,
            showFabCenter = false
        ) {
//            findNavController().apply {
//                previousBackStackEntry!!.savedStateHandle.set(
//                    PRODUCT_CATEGORY_KEY,
//                )
//                popBackStack()
//            }
        }

        setupCategoriesAdapter()

        binding.categoriesSearch.addTextChangedListener {
            viewModel.setQuery(it.toString())
        }
    }

    private fun setupCategoriesAdapter(){
        ProductCategoryAdapter { onCategoryClicked(it) }.let {
            binding.recyclerView.adapter = it
            subscribeUi(it)
        }
    }

    private fun subscribeUi(adapter: ProductCategoryAdapter){
        viewModel.categoriesLiveData.observe(viewLifecycleOwner){
            adapter.submitData(viewLifecycleOwner.lifecycle, it)
        }
    }

    private fun onRemoveCategory(category: Category) {

    }

    private fun onCategoryClicked(category: Category) {

    }

    override fun onDestroyView() {
        binding.recyclerView.adapter = null
        super.onDestroyView()
    }
}