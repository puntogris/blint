package com.puntogris.blint.ui.business

import androidx.navigation.fragment.navArgs
import com.maxkeppeler.sheets.info.InfoSheet
import com.puntogris.blint.R
import com.puntogris.blint.databinding.FragmentDeleteBusinessBinding
import com.puntogris.blint.ui.base.BaseFragment
import com.puntogris.blint.utils.changeIconFromDrawable
import com.puntogris.blint.utils.getParentFab
import com.puntogris.blint.utils.getString
import com.puntogris.blint.utils.showLongSnackBarAboveFab
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DeleteBusinessFragment : BaseFragment<FragmentDeleteBusinessBinding>(R.layout.fragment_delete_business) {

    private val args:DeleteBusinessFragmentArgs by navArgs()

    override fun initializeViews() {
        getParentFab().apply {
            changeIconFromDrawable(R.drawable.ic_baseline_delete_24)
            setOnClickListener {
                if (binding.businessNameText.getString() == args.business.businessName){
                    showDeleteBusinessUi()
                }else{
                    showLongSnackBarAboveFab("El nombre no coincide con el del negocio.")
                }
            }
        }
    }

    private fun showDeleteBusinessUi(){
        InfoSheet().build(requireContext()){
            title("Queres eliminar este negocio?")
            content("Zona de peligro! Tene en cuenta que esta accion pondra a tu negocio desactivado por 15 dias y luego sera eliminado definitivamente.")
            onNegative("No") {
                // Handle event
            }
            onPositive("Eliminar") {
                // Handle event
            }
        }.show(parentFragmentManager, "")
    }
}