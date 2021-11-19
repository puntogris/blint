package com.puntogris.blint.ui.product.categories

import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.puntogris.blint.R
import com.puntogris.blint.databinding.FragmentProductCategoryBinding
import com.puntogris.blint.model.Category
import com.puntogris.blint.ui.base.BaseFragment
import com.puntogris.blint.ui.categories.FilterCategoriesViewModel
import com.puntogris.blint.utils.Constants.CATEGORIES_SUPPLIERS_LIMIT
import com.puntogris.blint.utils.Constants.PRODUCT_CATEGORY_KEY
import com.puntogris.blint.utils.UiInterface
import com.puntogris.blint.utils.gone
import com.puntogris.blint.utils.launchAndRepeatWithViewLifecycle
import com.puntogris.blint.utils.types.RepoResult
import com.puntogris.blint.utils.visible
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ProductCategoryFragment :
    BaseFragment<FragmentProductCategoryBinding>(R.layout.fragment_product_category) {

    private val viewModel: FilterCategoriesViewModel by viewModels()
    private val args: ProductCategoryFragmentArgs by navArgs()
    private lateinit var addCategoryAdapter: AddProductCategoryAdapter
    private lateinit var removeCategoryAdapter: RemoveProductCategoryAdapter
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
            findNavController().apply {
                previousBackStackEntry!!.savedStateHandle.set(
                    PRODUCT_CATEGORY_KEY,
                    removeCategoryAdapter.getFinalCategories()
                )
                popBackStack()
            }
        }

        addCategoryAdapter = AddProductCategoryAdapter { onCategoryClicked(it) }
        binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = addCategoryAdapter
        }

        removeCategoryAdapter = RemoveProductCategoryAdapter { onRemoveCategory(it) }
        binding.productCategories.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = removeCategoryAdapter
        }

        if (args.categories.isNullOrEmpty()) binding.textView195.visible()
        else removeCategoryAdapter.initialCategories(args.categories!!.toMutableList())


        binding.categoriesSearch.addTextChangedListener {
            it.toString().let { text ->
                if (text.isBlank()) addCategoryAdapter.submitList(items)
                else {
                    addCategoryAdapter.currentList.filter { category ->
                        category.categoryName.contains(text)
                    }.also { list -> addCategoryAdapter.submitList(list) }
                }
            }
        }

        launchAndRepeatWithViewLifecycle {
//            when (val result = viewModel.getAllCategories()) {
//                is RepoResult.Error -> {
//                }
//                RepoResult.InProgress -> {
//                }
//                is RepoResult.Success -> {
//                    items = result.data.toMutableList()
//                    addCategoryAdapter.submitList(result.data)
//                }
//            }
        }
    }

    private fun onRemoveCategory(category: Category) {
        removeCategoryAdapter.removeCategory(category)
        if (removeCategoryAdapter.itemCount == 0) binding.textView195.visible()
    }

    private fun onCategoryClicked(category: Category) {
        if (removeCategoryAdapter.itemCount >= CATEGORIES_SUPPLIERS_LIMIT)
            UiInterface.showSnackBar(
                getString(
                    R.string.snack_product_categories_limit,
                    CATEGORIES_SUPPLIERS_LIMIT
                )
            )
        else {
            removeCategoryAdapter.addCategory(category)
            if (removeCategoryAdapter.itemCount != 0) binding.textView195.gone()
        }

    }

    override fun onDestroyView() {
        binding.recyclerView.adapter = null
        binding.productCategories.adapter = null
        super.onDestroyView()
    }
}