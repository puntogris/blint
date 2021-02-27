package com.puntogris.blint.ui.main

import android.Manifest
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Button
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatDelegate
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.ui.*
import com.google.android.material.badge.BadgeDrawable
import com.google.android.material.badge.BadgeUtils
import com.puntogris.blint.R
import com.puntogris.blint.databinding.ActivityMainBinding
import com.puntogris.blint.ui.SharedPref
import com.puntogris.blint.ui.base.BaseActivity
import com.puntogris.blint.utils.*
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : BaseActivity<ActivityMainBinding>(R.layout.activity_main) {

    private lateinit var navController: NavController
    private lateinit var appBarConfiguration: AppBarConfiguration
    lateinit var requestPermissionLauncher: ActivityResultLauncher<String>

    @Inject
    lateinit var sharedPref: SharedPref
    private val viewModel: MainViewModel by viewModels()

    override fun preInitViews() {
        setTheme(R.style.Theme_Blint)
    }

    override fun initializeViews() {
        setUpTheme()
        setUpNavigation()
        setUpScanner()
    }

    private fun setUpTheme() {
        AppCompatDelegate.setDefaultNightMode(sharedPref.getThemePref())
    }

    private fun setUpScanner() {
        requestPermissionLauncher =
            registerForActivityResult(ActivityResultContracts.RequestPermission())
            { isGranted: Boolean ->
                if (isGranted) navController.navigate(R.id.scannerFragment)
                else showLongSnackBarAboveFab("Necesitamos acceso a la camara para poder abrir el escaner.")
            }
    }

    private fun setUpNavigation() {
        setSupportActionBar(binding.toolbar)
        navController = getNavController()
        navController.graph = navController.navInflater.inflate(R.navigation.navigation)
            .apply {
                startDestination =
                    if (viewModel.isUserLoggedIn()) {
                        when {
                            !sharedPref.getWelcomeUiPref() -> R.id.welcomeFragment
                            !sharedPref.getUserHasBusinessPref() -> R.id.newUserFragment
                            else -> R.id.mainFragment
                        }
                    } else R.id.loginFragment
            }

        binding.run {
            navController.addOnDestinationChangedListener(this@MainActivity)
        }
        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.mainFragment,
                R.id.registerBusinessFragment,
                R.id.preferencesFragment,
                R.id.loginFragment,
                R.id.manageBusinessFragment,
                R.id.welcomeFragment,
                R.id.introFragment,
                R.id.firstSyncFragment,
                R.id.newUserFragment
            )
        )
        setupActionBarWithNavController(navController, appBarConfiguration)

        binding.bottomAppBar.apply {
            setNavigationOnClickListener {
                if(sharedPref.getUserHasBusinessPref()){
                    if (navController.currentDestination?.id != R.id.mainFragment)
                        navController.navigate(R.id.mainFragment)
                }else navController.navigate(R.id.newUserFragment)
            }
            setOnMenuItemClickListener(this@MainActivity)
        }
    }


    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }

    override fun onDestinationChanged(
        controller: NavController,
        destination: NavDestination,
        arguments: Bundle?
    ) {
        if (
            destination.id == R.id.registerBusinessFragment ||
            destination.id == R.id.registerLocalBusinessFragment ||
            destination.id == R.id.registerOnlineBusinessFragment ||
            destination.id == R.id.loginProblemsFragment ||
            destination.id == R.id.eventInfoBottomSheet
        ) {
            binding.addFav.isClickable = false
            binding.bottomAppBar.gone()
            binding.addFav.hide()
        } else if (
            destination.id == R.id.aboutFragment ||
            destination.id == R.id.termsConditionsFragment ||
            destination.id == R.id.privacyPolicyFragment ||
            destination.id == R.id.createBackupFragment ||
            destination.id == R.id.restoreBackupFragment
        ) {
            binding.bottomAppBar.performHide()
            binding.addFav.hide()
        }
       else if(destination.id == R.id.welcomeFragment ||
                destination.id == R.id.loginFragment ||
                destination.id == R.id.introFragment ||
                destination.id == R.id.firstSyncFragment){
            window.statusBarColor = resources.getColor(R.color.colorSecondary)
            binding.addFav.isClickable = false
            binding.bottomAppBar.gone()
            binding.addFav.hide()
            binding.toolbar.setBackgroundColor(getColor(R.color.colorSecondary))

        }else if(destination.id == R.id.mainFragment ||
            destination.id == R.id.newUserFragment
        ){
            binding.addFav.hide()
            binding.bottomAppBar.visible()
            binding.toolbar.setBackgroundColor(getColor(R.color.colorSecondary))
            window.statusBarColor = resources.getColor(R.color.colorSecondary)
        }else if(destination.id == R.id.calendarFragment){
            setupToolbarAndStatusBar()
            binding.addFav.isClickable = false
            binding.bottomAppBar.gone()
            binding.addFav.hide()
        }else if(destination.id == R.id.manageBusinessFragment ||
                destination.id == R.id.preferencesFragment){
            setupToolbarAndStatusBar()
            binding.addFav.isClickable = false
            binding.bottomAppBar.visible()
            binding.addFav.hide()
            binding.bottomAppBar.performShow()
        }else if(destination.id == R.id.addBusinessEmployee){
            binding.addFav.isClickable = false
            binding.addFav.hide()
        }
        else {
            setupToolbarAndStatusBar()
            binding.bottomAppBar.visible()
            binding.addFav.show()
            binding.bottomAppBar.performShow()
        }
        binding.addFav.changeIconFromDrawable(R.drawable.ic_baseline_add_24)

    }

    private fun setupToolbarAndStatusBar(){
        if (isDarkThemeOn()){
            window.statusBarColor = resources.getColor(R.color.nightBackground)
            binding.toolbar.setBackgroundColor(getColor(R.color.nightBackground))
            val view = window.decorView
            view.setSystemUiVisibility(view.getSystemUiVisibility() and View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR.inv())
        }else{
            window.statusBarColor = resources.getColor(R.color.grey_5)
            binding.toolbar.setBackgroundColor(getColor(R.color.grey_5))
            val view = window.decorView
            view.setSystemUiVisibility(view.getSystemUiVisibility() or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR)
        }
    }


    override fun onMenuItemClick(menuItem: MenuItem?): Boolean {
        when (menuItem?.itemId) {
            R.id.scannerFragment -> {
                if (checkIfBusinessExist()) requestPermissionLauncher.launch(Manifest.permission.CAMERA)
            }
            R.id.preferencesFragment -> navController.navigate(R.id.preferencesFragment)
            R.id.notificationsFragment -> navController.navigate(R.id.notificationsFragment)
            R.id.businessFragment -> navController.navigate(R.id.manageBusinessFragment)
        }
        return true
    }

    private fun checkIfBusinessExist():Boolean{
        return if (sharedPref.getUserHasBusinessPref()){
            true
        }else{
            showLongSnackBarAboveFab("No puedes acceder a esta funcion sin pertenecer a un negocio.")
            false
        }
    }
}