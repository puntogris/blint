package com.puntogris.blint.ui.product

import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.maxkeppeler.bottomsheets.info.InfoSheet
import com.puntogris.blint.R
import com.puntogris.blint.databinding.FragmentDataProductBinding
import com.puntogris.blint.model.Product
import com.puntogris.blint.ui.base.BaseFragment
import com.puntogris.blint.utils.showLongSnackBarAboveFab
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ProductDataFragment : BaseFragment<FragmentDataProductBinding>(R.layout.fragment_data_product) {

    private val viewModel: ProductViewModel by viewModels()

    override fun initializeViews() {
        binding.fragment = this
        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner

        arguments?.takeIf { it.containsKey("product_key") }?.apply {
            getParcelable<Product>("product_key")?.let {
                viewModel.setProductData(it)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        setHasOptionsMenu(true)
        super.onCreate(savedInstanceState)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        menu.findItem(R.id.moreOptions).isVisible = true
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.editOption -> {
                (requireParentFragment() as ProductFragment).navigateToEditProduct()
                true
            }
            R.id.deleteOption -> {
                openBottomSheetForDeletion()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun openBottomSheetForDeletion(){
        InfoSheet().build(requireContext()) {
            title("Queres eliminar este producto?")
            content("Zona de peligro! Estas por eliminar un producto. Tene en cuenta que esta accion es irreversible.")
            onNegative("Cancelar")
            onPositive("Si") {
                viewModel.deleteProductDatabase()
                showLongSnackBarAboveFab("Producto eliminado correctamente.")
                findNavController().navigateUp()
            }
        }.show(parentFragmentManager, "")
    }
}