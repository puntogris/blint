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
import com.puntogris.blint.model.FirestoreCategory
import com.puntogris.blint.ui.base.BaseFragment
import com.puntogris.blint.ui.product.ProductViewModel
import com.puntogris.blint.utils.setUpUi
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ProductCategoryFragment : BaseFragment<FragmentProductCategoryBinding>(R.layout.fragment_product_category) {

    private val viewModel: ProductViewModel by viewModels()
    private val args: ProductCategoryFragmentArgs by navArgs()
    private lateinit var adapter: ProductCategoryAdapter
    private var items = mutableListOf<FirestoreCategory>()

    override fun initializeViews() {
        binding.searchToolbar.setNavigationOnClickListener { findNavController().navigateUp() }
        setUpUi(showFab = true, showAppBar = false, fabIcon = R.drawable.ic_baseline_save_24, showToolbar = false){
            findNavController().apply {
                previousBackStackEntry!!.savedStateHandle.set("categories_key", adapter.getFinalCategories())
                popBackStack()
            }
        }
        adapter = ProductCategoryAdapter()
        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerView.adapter = adapter
        args.categories?.let {
            adapter.initialCategories(it.toMutableList())
        }

        binding.categoriesSearch.setOnItemClickListener { adapterView, view, i, l ->
            adapter.addCategory(items[i])
        }

        binding.categoriesSearch.addTextChangedListener {
            it?.toString()?.let { text ->
                lifecycleScope.launch {
                    val data = viewModel.getCategoriesWithName(text)
                    items = data.toMutableList()
                    val adapter = ArrayAdapter(requireContext(), R.layout.dropdown_item_list, data.map { it.name })
                    (binding.categoriesSearch as? AutoCompleteTextView)?.setAdapter(adapter)
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