package com.puntogris.blint.ui.backup

import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.airbnb.lottie.Lottie
import com.airbnb.lottie.LottieAnimationView
import com.airbnb.lottie.LottieDrawable
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
            animationView.repeatCount =LottieDrawable.INFINITE
            animationView.visible()
            animationView.playAnimation()
            businessesCardView.gone()
            businessTitle.gone()
            backupSummary.text = "*No cierres esta ventana hasta que finalize el respaldo."
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
            content("Esto reemplazara a un respaldo de tu negocio ya existente en el caso de que exista.")
            onNegative("Cancelar")
            onPositive("Respaldar") {  startBusinessBackup(business.id) }
        }
    }

    private fun startBusinessBackup(businessID: String){
        showBackupInProgressUI()
        lifecycleScope.launch {
            when(viewModel.backupBusiness(businessID, createDatabasesPathList())){
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
        binding.backupSummary.text = "*Respaldo realizado satisfactoriamente. Que tengas un muy buen dia."
        binding.animationView.apply {
            setAnimation(R.raw.done)
            repeatCount = 0
            playAnimation()
        }
    }

    private fun showFailureBackupUI(){
        binding.animationView.cancelAnimation()
        binding.backupSummary.text = "*Ocurrio un problema respaldando tu negocio. Intenta nuevamente mas tarde."

    }
}