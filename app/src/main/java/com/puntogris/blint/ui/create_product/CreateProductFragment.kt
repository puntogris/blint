package com.puntogris.blint.ui.create_product


import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.puntogris.blint.R
import com.puntogris.blint.databinding.FragmentCreateProductBinding
import com.puntogris.blint.ui.base.BaseFragment
import com.puntogris.blint.utils.changeIconFromDrawable
import com.puntogris.blint.utils.getParentFab
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber

@AndroidEntryPoint
class CreateProductFragment : BaseFragment<FragmentCreateProductBinding>(R.layout.fragment_create_product) {

    private val viewModel: CreateProductViewModel by activityViewModels()

    override fun initializeViews() {
        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner
        findNavController().currentBackStackEntry?.savedStateHandle?.getLiveData<String>("key")?.observe(
            viewLifecycleOwner) { result ->
            binding.productBarcodeText.setText(result)
        }
        getParentFab().apply {
            changeIconFromDrawable(R.drawable.ic_baseline_save_24)
            setOnClickListener {

            }
        }
        binding.scanBarcodeButton.setOnClickListener {
            findNavController().navigate(R.id.action_createProductFragment_to_scannerFragment)
        }

    }
}