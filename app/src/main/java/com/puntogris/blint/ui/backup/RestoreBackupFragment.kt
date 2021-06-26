package com.puntogris.blint.ui.backup

import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.airbnb.lottie.LottieDrawable
import com.maxkeppeler.sheets.info.InfoSheet
import com.puntogris.blint.R
import com.puntogris.blint.databinding.FragmentRestoreBackupBinding
import com.puntogris.blint.model.Business
import com.puntogris.blint.model.Employee
import com.puntogris.blint.ui.base.BaseFragment
import com.puntogris.blint.ui.settings.PreferencesViewModel
import com.puntogris.blint.utils.*
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import java.util.*

@AndroidEntryPoint
class RestoreBackupFragment : BaseFragment<FragmentRestoreBackupBinding>(R.layout.fragment_restore_backup) {

    private val viewModel: PreferencesViewModel by viewModels()
    private lateinit var backupAdapter: BackupAdapter

    override fun initializeViews() {
        setUpRecyclerView()
        getUserBusiness()

        binding.button19.setOnClickListener {
            showConfirmationDialogForBackUp()
        }
    }

    private fun getUserBusiness(){
        lifecycleScope.launch {
            viewModel.getBackUpRequirements().collect {
                when(it){
                    is RepoResult.Success -> showBusinessUI(it.data)
                    is RepoResult.Error -> showNoBusinessFoundUI()
                    RepoResult.InProgress -> binding.loadingBusinessProgressBar.visible()
                }
            }
        }
    }


    private fun showNoBusinessFoundUI(){
        binding.loadingBusinessProgressBar.gone()
        binding.loadingBusinessSummary.text = "No encontramos negocios locales enlazados a tu cuenta."
    }

    private fun showBusinessUI(data: List<Business>){
        getLastBackUpDate()
        backupAdapter.submitList(data)
        binding.apply {
            loadingBusinessProgressBar.gone()
            loadingBusinessSummary.gone()
            businessTitle.visible()
            recyclerView.visible()
            button19.visible()
            textView69.visible()
            textView70.visible()
            textView71.visible()
        }
    }

    private fun getLastBackUpDate(){
        lifecycleScope.launchWhenStarted {
            viewModel.getLastBackUpDate().collect {
                when(it){
                    is RepoResult.Error -> {
                        binding.textView71.text = "No encontrado."
                    }
                    RepoResult.InProgress -> {}
                    is RepoResult.Success -> {
                        binding.textView71.text = Date(it.data).getDateFormattedString()
                    }
                }
            }
        }
    }

    private fun showBackupInProgressUI(){
        binding.apply {
            animationView.repeatCount = LottieDrawable.INFINITE
            animationView.visible()
            animationView.playAnimation()
            recyclerView.gone()
            businessTitle.gone()
            button19.gone()
            textView69.gone()
            textView70.gone()
            textView71.gone()
            businessTitle.visible()
            businessTitle.text = "Respaldo en progreso.."
            backupSummary.text = "*No cierres esta ventana hasta que finalize el respaldo."
        }
    }

    private fun setUpRecyclerView(){
        binding.recyclerView.apply {
            backupAdapter = BackupAdapter()
            adapter = backupAdapter
            layoutManager = LinearLayoutManager(requireContext())
        }
    }

    private fun showConfirmationDialogForBackUp(){
        InfoSheet().show(requireParentFragment().requireContext()){
            title("Estas seguro de esta accion?")
            content("Esto restaurara tus negocios desde la ultima copia que subiste a la nube, toda la informacion que tengas se borrara y reemplazara por la copia de seguridad. Ten en cuenta que las imagenes no se restableceran.")
            onNegative("Cancelar")
            onPositive("Restaurar") {  startBusinessBackup() }
        }
    }

    private fun startBusinessBackup(){
        showBackupInProgressUI()
        lifecycleScope.launch {
            when(viewModel.restoreBackup(getDatabasePath())){
                SimpleResult.Success -> {
                    showSuccessfulBackupUI()
                }
                SimpleResult.Failure -> {
                    showFailureBackupUI()
                }
            }
        }
    }

    private fun showSuccessfulBackupUI(){
        binding.backupSummary.text = "*Restauracion realizada satisfactoriamente. Que tengas un muy buen dia."
        binding.businessTitle.text = "Restauracion realizada."
        binding.animationView.apply {
            setAnimation(R.raw.done)
            repeatCount = 0
            playAnimation()
        }
    }

    private fun showFailureBackupUI(){
        binding.businessTitle.text = "Restauracion erronea."
        binding.backupSummary.text = "*Ocurrio un problema restaurando tu negocio. Esto se puede deber a que no se encontro un respaldo previamente o problemas del servidor.\nIntenta nuevamente mas tarde."
        binding.animationView.apply {
            setAnimation(R.raw.error)
            repeatCount = 0
            playAnimation()
        }
    }
}