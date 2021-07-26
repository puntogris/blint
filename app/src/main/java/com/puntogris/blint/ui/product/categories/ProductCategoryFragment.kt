package com.puntogris.blint.ui.product.categories

import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.puntogris.blint.R
import com.puntogris.blint.databinding.FragmentProductCategoryBinding
import com.puntogris.blint.model.Category
import com.puntogris.blint.ui.base.BaseFragment
import com.puntogris.blint.ui.categories.FilterCategoriesViewModel
import com.puntogris.blint.utils.RepoResult
import com.puntogris.blint.utils.launchAndRepeatWithViewLifecycle
import com.puntogris.blint.utils.registerUiInterface
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ProductCategoryFragment : BaseFragment<FragmentProductCategoryBinding>(R.layout.fragment_product_category) {

    private val viewModel: FilterCategoriesViewModel by viewModels()
    private val args: ProductCategoryFragmentArgs by navArgs()
    private lateinit var categoriesAdapter: ProductCategoryAdapter
    private var items = listOf<Category>()

    override fun initializeViews() {
        binding.searchToolbar.setNavigationOnClickListener { findNavController().navigateUp() }
        registerUiInterface.register(showFab = true, showAppBar = false, fabIcon = R.drawable.ic_baseline_arrow_forward_24, showToolbar = false,
        showFabCenter = false){
            findNavController().apply {
                previousBackStackEntry!!.savedStateHandle.set("categories_key", categoriesAdapter.getFinalCategories())
                popBackStack()
            }
        }

        categoriesAdapter = ProductCategoryAdapter()
        binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = categoriesAdapter
        }

        args.categories?.let {
            categoriesAdapter.initialCategories(it.toMutableList())
        }

        binding.categoriesSearch.setOnItemClickListener { adapterView, _, i, _ ->
            val item = adapterView.getItemAtPosition(i)
            val category = items.singleOrNull{ it.categoryName == item }
            if (category != null) categoriesAdapter.addCategory(category)
        }

        launchAndRepeatWithViewLifecycle {
            when(val result = viewModel.getAllCategories()){
                is RepoResult.Error -> {}
                RepoResult.InProgress -> {}
                is RepoResult.Success -> {
                    items = result.data
                    val searchAdapter = ArrayAdapter(requireContext(), R.layout.dropdown_item_list, result.data.map { it.categoryName })
                    (binding.categoriesSearch as? AutoCompleteTextView)?.setAdapter(searchAdapter)
                }
            }
        }
    }

    override fun onDestroyView() {
        binding.recyclerView.adapter = null
        binding.categoriesSearch.setAdapter(null)
        super.onDestroyView()
    }
}