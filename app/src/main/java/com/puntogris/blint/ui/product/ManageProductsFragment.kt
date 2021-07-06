package com.puntogris.blint.ui.product

import android.Manifest
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.FragmentContainerView
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.puntogris.blint.R
import com.puntogris.blint.databinding.FragmentManageProductsBinding
import com.puntogris.blint.model.ProductWithSuppliersCategories
import com.puntogris.blint.ui.base.BaseFragmentOptions
import com.puntogris.blint.ui.custom_views.ConstraintRadioGroup
import com.puntogris.blint.ui.nav.BottomNavDrawerFragment
import com.puntogris.blint.ui.nav.ShowHideFabStateAction
import com.puntogris.blint.utils.*
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ManageProductsFragment : BaseFragmentOptions<FragmentManageProductsBinding>(R.layout.fragment_manage_products) {

    private val viewModel: ProductViewModel by viewModels()
    private lateinit var manageProductsAdapter : ManageProductsAdapter
    lateinit var requestPermissionLauncher: ActivityResultLauncher<String>

    override fun initializeViews() {
        binding.productSearch.clearFocus()
        binding.searchToolbar.setNavigationOnClickListener { findNavController().navigateUp() }
        binding.fragment = this

        setUpUi(showToolbar = false, showAppBar = true, showFab = true){
            findNavController().navigate(R.id.editProductFragment)
        }

        requireActivity().findViewById<FragmentContainerView>(R.id.bottom_nav_drawer).getFragment<BottomNavDrawerFragment>()
            ?.addOnStateChangedAction(ShowHideFabStateAction(getParentFab(), true))

        manageProductsAdapter = ManageProductsAdapter({onProductShortClickListener(it)},{onProductLongClickListener(it)})

        binding.recyclerView.apply {
            adapter = manageProductsAdapter
            layoutManager = LinearLayoutManager(requireContext())
        }

        launchAndRepeatWithViewLifecycle {
            searchProductAndFillAdapter()
        }

        binding.productSearch.apply {
            setOnFocusChangeListener { _, _ ->
                binding.searchTypeRadioGroup.visible()
            }
            addTextChangedListener {
                lifecycleScope.launch {
                    it.toString().let { text ->
                        if (text.isBlank()) searchProductAndFillAdapter()
                        else {
                            val data = when(binding.searchTypeRadioGroup.checkedRadioButtonId){
                                R.id.qrSearchType -> SearchText.QrCode(text)
                                R.id.internalCodeSearchType -> SearchText.InternalCode(text)
                                else -> SearchText.Name(text)
                            }
                            searchProductWithNameAndFillAdapter(data)
                        }
                    }
                }
            }
        }

        binding.searchTypeRadioGroup.let {
            it.setOnCheckedChangeListener(object : ConstraintRadioGroup.OnCheckedChangeListener{
                override fun onCheckedChanged(group: ConstraintRadioGroup?, checkedId: Int) {
                    val text = binding.productSearch.getString()
                    if (text.isNotEmpty()){
                        lifecycleScope.launch {
                            searchProductWithNameAndFillAdapter(
                                when(it.checkedRadioButtonId){
                                R.id.qrSearchType -> SearchText.QrCode(text)
                                R.id.internalCodeSearchType -> SearchText.InternalCode(text)
                                else -> SearchText.Name(text)
                            })
                        }
                    }
                }
            })
        }

        requestPermissionLauncher =
            registerForActivityResult(ActivityResultContracts.RequestPermission())
            { isGranted: Boolean ->
                if (isGranted) {
                    val action = ManageProductsFragmentDirections.actionManageProductsFragmentToScannerFragment(1)
                    findNavController().navigate(action)
                }
                else showLongSnackBarAboveFab(getString(R.string.snack_require_camera_permission))
            }

        findNavController().currentBackStackEntry?.savedStateHandle?.getLiveData<String>("key")?.observe(
            viewLifecycleOwner) {
            it?.let { code ->
                binding.productSearch.setText(code)
                binding.searchTypeRadioGroup.visible()
                binding.searchTypeRadioGroup.check(R.id.qrSearchType)
                launchAndRepeatWithViewLifecycle {
                    searchProductWithNameAndFillAdapter(SearchText.QrCode(code))
                }
            }
        }
    }

    fun onScanBarcodeClicked(){
        hideKeyboard()
        requestPermissionLauncher.launch(Manifest.permission.CAMERA)

    }

    private suspend fun searchProductAndFillAdapter(){
        viewModel.getProductsPaging().collect {
            manageProductsAdapter.submitData(it)
        }
    }

    private suspend fun searchProductWithNameAndFillAdapter(search: SearchText){
        viewModel.getProductWithName(search).collect { data ->
            manageProductsAdapter.submitData(data)
        }
    }

    private fun onProductShortClickListener(product: ProductWithSuppliersCategories){
        hideKeyboard()
        val action = ManageProductsFragmentDirections.actionManageProductsFragmentToProductFragment(product)
        findNavController().navigate(action)
    }

    private fun onProductLongClickListener(product: ProductWithSuppliersCategories){
        showOrderPickerAndNavigate(product.product)
    }


//
//    override fun onOptionsItemSelected(item: MenuItem): Boolean {
//        return when (item.itemId) {
//            R.id.newProduct -> {
//                findNavController().navigate(R.id.editProductFragment)
//                true
//            }
//            R.id.manageCategories -> {
//                findNavController().navigate(R.id.manageCategoriesFragment)
//                true
//            }
//            else -> super.onOptionsItemSelected(item)
//        }
//    }

    override fun onDestroyView() {
        binding.productSearch.clearFocus()
        binding.recyclerView.adapter = null
        super.onDestroyView()
    }
}