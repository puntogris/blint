package com.puntogris.blint.ui.supplier

import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.puntogris.blint.R
import com.puntogris.blint.databinding.FragmentSupplierDataBinding
import com.puntogris.blint.ui.base.BaseFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SupplierDataFragment : BaseFragment<FragmentSupplierDataBinding>(R.layout.fragment_supplier_data) {

    private val viewModel: SupplierViewModel by viewModels()

    override fun initializeViews() {
        binding.fragment = this
        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner

        arguments?.takeIf { it.containsKey("supplier_key") }?.apply {
            getInt("supplier_key").let {
                lifecycleScope.launchWhenStarted {
                    val supplier = viewModel.getSupplier(it)
                    viewModel.setSupplierData(supplier)
                }
            }
        }
    }

}