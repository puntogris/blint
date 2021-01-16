package com.puntogris.blint.ui.business

import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.puntogris.blint.R
import com.puntogris.blint.databinding.FragmentJoinBusinessBinding
import com.puntogris.blint.model.Business
import com.puntogris.blint.model.Employee
import com.puntogris.blint.ui.SharedPref
import com.puntogris.blint.ui.base.BaseFragment
import com.puntogris.blint.ui.login.LoginViewModel
import com.puntogris.blint.utils.gone
import com.puntogris.blint.utils.visible
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class JoinBusinessFragment : BaseFragment<FragmentJoinBusinessBinding>(R.layout.fragment_join_business) {

    private val viewModel: LoginViewModel by viewModels()
    private lateinit var joinBusinessAdapter: JoinBusinessAdapter
    @Inject
    lateinit var sharedPref: SharedPref

    override fun initializeViews() {
        binding.fragment = this
        setUpRecyclerView()
        viewModel.getUserBusiness()
        lifecycleScope.launchWhenStarted {
            viewModel.userBusiness.collect {
                if (it.isEmpty()) onEmployeeDataNotFound()
                else onEmployeeDataFound(it)
            }
        }
    }

    fun onCloseButtonClicked(){
        viewModel.singOut()
        findNavController().navigate(R.id.loginFragment)
    }

    private fun setUpRecyclerView(){
        binding.recyclerView.apply {
            joinBusinessAdapter = JoinBusinessAdapter()
            adapter = joinBusinessAdapter
            layoutManager = LinearLayoutManager(requireContext())
        }
    }

    private fun onEmployeeDataFound(data: List<Business>){
        joinBusinessAdapter.submitList(data)
        binding.apply {
            binding.fillJokeMessage.text = "Encontramos estos negocios enlazados a tu cuenta.\n\nSi esta informacion es valida continua o espera y automaticamente seguiremos buscando el negocio al que perteneces."
            continueButton.visible()
            recyclerViewCard.visible()
            accountMessage.gone()
            progressBar.gone()
        }
    }

    private fun onEmployeeDataNotFound(){
        binding.apply {
            continueButton.gone()
            recyclerViewCard.gone()
            accountMessage.visible()
            progressBar.visible()
        }
    }

    fun onContinueButtonClicked(){
        lifecycleScope.launch {
            viewModel.saveBusinessToLocalDatabase()
            sharedPref.setUserHasBusinessRegisteredPref()
            findNavController().navigate(R.id.mainFragment)
        }
    }

    override fun onDestroyView() {
        binding.recyclerView.adapter = null
        super.onDestroyView()
    }
}