package com.puntogris.blint.ui.backup

import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.maxkeppeler.bottomsheets.info.InfoSheet
import com.puntogris.blint.R
import com.puntogris.blint.databinding.FragmentCreateBackUpBinding
import com.puntogris.blint.model.Business
import com.puntogris.blint.ui.base.BaseFragment
import com.puntogris.blint.ui.settings.PreferencesViewModel
import com.puntogris.blint.utils.*
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import java.io.*
import java.util.zip.ZipEntry
import java.util.zip.ZipOutputStream

@AndroidEntryPoint
class CreateBackupFragment : BaseFragment<FragmentCreateBackUpBinding>(R.layout.fragment_create_back_up) {

    private val viewModel: PreferencesViewModel by viewModels()
    private lateinit var backupAdapter: BackupAdapter

    override fun initializeViews() {
        setUpRecyclerView()
        getUserBusiness()
    }

    private fun getUserBusiness(){
        lifecycleScope.launch {
            viewModel.getOwnerBusiness().collect {
                when(it){
                    is RepoResult.Success -> showBusinessUI(it.data)
                    is RepoResult.Error -> {
                        binding.progressBar2.gone()
                        binding.textView68.text =
                            "No encontramos ningun negocio local enlazado a tu cuenta."
                    }
                    RepoResult.InProgress -> binding.progressBar2.visible()
                }
            }
        }
    }

    private fun showBusinessUI(data: List<Business>){
        backupAdapter.submitList(data)
        binding.businessesCardView.visible()
        binding.progressBar2.gone()
        binding.textView68.gone()
        binding.textView66.visible()
    }
    private fun showBackupInProgressUI(){
        binding.businessesCardView.gone()
        binding.progressBar2.visible()
        binding.textView66.gone()
        binding.textView67.text = "*No cierres esta ventana hasta que finalize el respaldo."
    }

    private fun setUpRecyclerView(){
        binding.recyclerView.apply {
            backupAdapter = BackupAdapter{businessClickListener(it)}
            adapter = backupAdapter
            layoutManager = LinearLayoutManager(requireContext())
        }
    }

    private fun businessClickListener(business: Business){
        InfoSheet().build(requireContext()){
            title("Estas seguro de esta accion?")
            content("Esto reemplazara a un respaldo de tu negocio ya existente en el caso de que exista.")
            onNegative("Cancelar")
            onPositive("Respaldar") {
                backupBusiness(business.id)
            }
        }.show(parentFragmentManager, "")
    }

    private fun backupBusiness(businessID: String){
        showBackupInProgressUI()
        lifecycleScope.launch {
            when(viewModel.backupBusiness(businessID, createDatabasesPathList())){
                SimpleResult.Success -> {
                    println("ya")
                    binding.progressBar2.gone()
                }
                SimpleResult.Failure -> {
                    println("no")
                    binding.progressBar2.gone()
                }
            }
        }

    }



}