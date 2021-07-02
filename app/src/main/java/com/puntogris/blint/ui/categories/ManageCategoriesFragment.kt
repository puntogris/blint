package com.puntogris.blint.ui.categories

import android.view.animation.AnimationUtils
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.maxkeppeler.sheets.core.SheetStyle
import com.maxkeppeler.sheets.info.InfoSheet
import com.maxkeppeler.sheets.input.InputSheet
import com.maxkeppeler.sheets.input.type.InputEditText
import com.puntogris.blint.R
import com.puntogris.blint.databinding.FragmentManageCategoriesBinding
import com.puntogris.blint.model.Category
import com.puntogris.blint.ui.base.BaseFragment
import com.puntogris.blint.ui.product.ProductViewModel
import com.puntogris.blint.utils.*
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ManageCategoriesFragment : BaseFragment<FragmentManageCategoriesBinding>(R.layout.fragment_manage_categories) {

    private val viewModel: ProductViewModel by viewModels()
    private lateinit var categoriesAdapter: CategoriesAdapter

    @ExperimentalCoroutinesApi
    override fun initializeViews() {
        binding.fragment = this
        setUpUi(showFab = true){ showCreateCategoryDialog() }

        categoriesAdapter = CategoriesAdapter{onCategoryClicked()}
        binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = categoriesAdapter
        }

        lifecycleScope.launchWhenStarted {
            viewModel.getProductCategories().collect {
                categoriesAdapter.submitList(it)
            }
        }
    }

    fun onClearCategoriesSelected(){
        categoriesAdapter.clearSelected()
        showSelectionUi()
    }

    private fun onCategoryClicked(){
        showSelectionUi()
        categoriesAdapter.notifyDataSetChanged()
    }

    private fun showSelectionUi(){
        val amountChecked = categoriesAdapter.getAmountChecked()
        binding.apply {
            if (amountChecked == 0) {
                selectedCategoriesText.gone()
                imageView17.gone()
                imageView18.gone()
                imageView19.gone()
            }else{
                if(amountChecked == 1) binding.imageView18.visible()
                else binding.imageView18.gone()
                selectedCategoriesText.text = amountChecked.toString()
                selectedCategoriesText.visible()
                imageView17.visible()
                imageView19.visible()
            }
        }
    }

    fun onEditCategoryClicked(){
        val category = categoriesAdapter.getSelectedCategories().first()
        InputSheet().build(requireContext()){
            style(SheetStyle.DIALOG)
            title("Renombrar categoria")
            with(InputEditText{
                required(true)
                hint("Nombre")
                onNegative("Cancelar")
                onPositive("Aceptar"){
                    category.name = it["0"].toString()
                    onEditCategoryConfirmed(category)
                }
            })
        }.show(parentFragmentManager, "")
    }

    private fun onEditCategoryConfirmed(category: Category){
        lifecycleScope.launch {
            when(viewModel.updateCategoryDatabase(category)){
                SimpleResult.Failure ->
                    showLongSnackBarAboveFab("Ocurrio un error al modificar la categoria.")
                SimpleResult.Success ->
                    showLongSnackBarAboveFab("Categoria modificada correctamente.")
            }
        }
    }

    private fun showCreateCategoryDialog(){
        InputSheet().build(requireContext()){
            style(SheetStyle.DIALOG)
            title("Agregar categoria")
            with(InputEditText{
                required(true)
                hint("Nombre")
                onNegative("Cancelar")
                onPositive("Aceptar"){
                    onCreateCategoryConfirmed(it["0"].toString())
                    hideKeyboard()
                }
            })
        }.show(parentFragmentManager, "")
    }

    private fun onCreateCategoryConfirmed(name: String){
        lifecycleScope.launch {
            when(viewModel.saveCategoryDatabase(name)){
                SimpleResult.Failure ->
                    showLongSnackBarAboveFab("Ocurrio un error al crear la categoria.")
                SimpleResult.Success ->
                    showLongSnackBarAboveFab("Categoria guardada correctamente.")
            }
        }
    }

    fun onDeleteCategoriesClicked(){
        InfoSheet().build(requireContext()) {
            title("Eliminar categorias")
            content("Estas por eliminar las categorias ,esta accion es irreversible.")
            onNegative("Cancelar")
            onPositive("Eliminar") { onDeleteCategoryConfirmed() }
        }.show(parentFragmentManager, "")
    }

    private fun onDeleteCategoryConfirmed(){
        lifecycleScope.launch {
            when(viewModel.deleteCategory(categoriesAdapter.getSelectedCategories())){
                SimpleResult.Failure ->
                    showLongSnackBarAboveFab("Ocurrio un error al eliminar las categorias.")
                SimpleResult.Success ->{
                    showSelectionUi()
                    categoriesAdapter.notifyItemRemoved(0)
                    categoriesAdapter.clearSelected()
                    categoriesAdapter.notifyDataSetChanged()
                    showLongSnackBarAboveFab("Categorias eliminadas satisfactoriamente.")
                }
            }
        }
    }
}