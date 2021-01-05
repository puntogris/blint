package com.puntogris.blint.ui.settings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import com.google.android.material.transition.MaterialFadeThrough
import com.google.android.material.transition.platform.MaterialArcMotion
import com.maxkeppeler.bottomsheets.options.DisplayMode
import com.maxkeppeler.bottomsheets.options.Option
import com.maxkeppeler.bottomsheets.options.OptionsSheet
import com.puntogris.blint.R

class PreferencesFragment: PreferenceFragmentCompat() {

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.preferences, rootKey)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        findPreference<Preference>("card")?.setOnPreferenceClickListener {
            OptionsSheet().show(requireParentFragment().requireContext()) {
                title("Seleccionar tarjetas del inicio")
                multipleChoices()
                displayMode(DisplayMode.LIST)
                with(
                    Option(R.drawable.ic_baseline_add_business_24, "Agregar Proveedores").select(),
                    Option(R.drawable.ic_baseline_library_add_24, "Agergar Producto").select(),
                    Option(R.drawable.ic_baseline_person_add_24, "Agregar Cliente").select(),
                    Option(R.drawable.ic_baseline_store_24, "Ver Proveedores"),
                    Option(R.drawable.ic_baseline_library_books_24, "Ver Productos"),
                    Option(R.drawable.ic_baseline_people_alt_24, "Ver Clientes")
                )
                onNegative("Cancelar")
                onPositive("Guardar") { index: Int, option: Option ->
                    // Handle selected option
                }
            }
            true
        }
        return super.onCreateView(inflater, container, savedInstanceState)
    }
}