package com.puntogris.blint.ui.client

import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.maxkeppeler.bottomsheets.info.InfoSheet
import com.puntogris.blint.R
import com.puntogris.blint.databinding.FragmentClientDataBinding
import com.puntogris.blint.model.Client
import com.puntogris.blint.ui.base.BaseFragment
import com.puntogris.blint.ui.product.ProductFragment
import com.puntogris.blint.utils.*
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ClientDataFragment : BaseFragment<FragmentClientDataBinding>(R.layout.fragment_client_data) {

    private val viewModel: ClientViewModel by viewModels()

    override fun initializeViews() {
        binding.fragment = this
        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner

        arguments?.takeIf { it.containsKey("client_key") }?.apply {
            getParcelable<Client>("client_key")?.let {
                viewModel.setClientData(it)
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
                (requireParentFragment() as ClientFragment).navigateToEditClient()
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
            title("Queres eliminar este cliente?")
            content("Zona de peligro! Estas por eliminar un cliente. Tene en cuenta que esta accion es irreversible.")
            onNegative("Cancelar")
            onPositive("Si") {
                viewModel.deleteClientDatabase()
                showLongSnackBarAboveFab("Cliente eliminado correctamente.")
                findNavController().navigateUp()
            }
        }.show(parentFragmentManager, "")
    }
}