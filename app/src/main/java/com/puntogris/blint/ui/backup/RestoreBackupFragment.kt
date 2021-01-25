package com.puntogris.blint.ui.backup

import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.airbnb.lottie.LottieDrawable
import com.maxkeppeler.sheets.info.InfoSheet
import com.puntogris.blint.R
import com.puntogris.blint.databinding.FragmentRestoreBackupBinding
import com.puntogris.blint.model.Business
import com.puntogris.blint.ui.base.BaseFragment
import com.puntogris.blint.ui.settings.PreferencesViewModel
import com.puntogris.blint.utils.*
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

@AndroidEntryPoint
class RestoreBackupFragment : BaseFragment<FragmentRestoreBackupBinding>(R.layout.fragment_restore_backup) {

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
                    is RepoResult.Error -> showNoBusinessFoundUI()
                    RepoResult.InProgress -> binding.loadingBusinessProgressBar.visible()
                }
            }
        }
    }
    private fun showNoBusinessFoundUI(){
        binding.loadingBusinessProgressBar.gone()
        binding.loadingBusinessSummary.text = "No encontramos ningun negocio local enlazado a tu cuenta."
    }

    private fun showBusinessUI(data: List<Business>){
        backupAdapter.submitList(data)
        binding.apply {
            businessesCardView.visible()
            loadingBusinessProgressBar.gone()
            loadingBusinessSummary.gone()
            businessTitle.visible()
        }
    }
    private fun showBackupInProgressUI(){
        binding.apply {
            animationView.repeatCount = LottieDrawable.INFINITE
            animationView.visible()
            animationView.playAnimation()
            businessesCardView.gone()
            businessTitle.gone()
            backupSummary.text = "*No cierres esta ventana hasta que finalize la restauracion."
        }
    }

    private fun setUpRecyclerView(){
        binding.recyclerView.apply {
            backupAdapter = BackupAdapter{businessClickListener(it)}
            adapter = backupAdapter
            layoutManager = LinearLayoutManager(requireContext())
        }
    }

    private fun businessClickListener(business: Business){
        InfoSheet().show(requireParentFragment().requireContext()){
            title("Estas seguro de esta accion?")
            content("Esto restaura todo tu negocio desde la ultima copia que subiste a la nube, toda la informacion que tengas se borrara y reemplazara por la copia de seguridad. Ten en cuenta que las imagenes no se restableceran.")
            onNegative("Cancelar")
            onPositive("Restaurar") { startBusinessBackup(business.businessId) }
        }
    }

    private fun startBusinessBackup(businessID: String){
        showBackupInProgressUI()
        lifecycleScope.launch {
            when(viewModel.restoreBackup(businessID, getDatabasePath())){
                SimpleResult.Success -> showSuccessfulBackupUI()
                SimpleResult.Failure -> showFailureBackupUI()
            }
        }
    }

    private fun showSuccessfulBackupUI(){
        binding.backupSummary.text = "*Restauracion realizada satisfactoriamente. Que tengas un muy buen dia."
        binding.animationView.apply {
            setAnimation(R.raw.done)
            repeatCount = 0
            playAnimation()
        }
    }

    private fun showFailureBackupUI(){
        binding.animationView.gone()
        binding.animationView.cancelAnimation()
        binding.backupSummary.text = "*Ocurrio un problema restaurando tu negocio. Intenta nuevamente mas tarde."
    }
}