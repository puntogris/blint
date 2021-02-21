package com.puntogris.blint.ui.main

import android.Manifest
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatDelegate
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
                        if (sharedPref.getWelcomeUiPref()) R.id.mainFragment
                        else R.id.welcomeFragment
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
                R.id.firstSyncFragment
            )
        )
        setupActionBarWithNavController(navController, appBarConfiguration)

        binding.bottomAppBar.apply {
            setNavigationOnClickListener {
                if (navController.currentDestination?.id != R.id.mainFragment)
                    navController.navigate(R.id.mainFragment)
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
            destination.id == R.id.joinBusinessFragment ||
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
            binding.toolbar.gone()
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

        }else if(destination.id == R.id.mainFragment){
            binding.addFav.hide()
            binding.bottomAppBar.visible()
            binding.toolbar.setBackgroundColor(getColor(R.color.colorSecondary))
            window.statusBarColor = resources.getColor(R.color.colorSecondary)
        }else if(destination.id == R.id.calendarFragment){
            setupToolbarAndStatusBar()
            binding.addFav.isClickable = false
            binding.bottomAppBar.gone()
            binding.addFav.hide()
        }else if(destination.id == R.id.manageBusinessFragment){
            setupToolbarAndStatusBar()
            binding.addFav.isClickable = false
            binding.bottomAppBar.visible()
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

    @com.google.android.material.badge.ExperimentalBadgeUtils
    override fun onPrepareOptionsMenu(menu: Menu?): Boolean {
        val badge = BadgeDrawable.create(this)
        badge.number = 4
        BadgeUtils.attachBadgeDrawable(badge, binding.toolbar, R.id.notificationsFragment)
        return super.onPrepareOptionsMenu(menu)
    }

    override fun onMenuItemClick(menuItem: MenuItem?): Boolean {
        when (menuItem?.itemId) {
            R.id.scannerFragment -> requestPermissionLauncher.launch(Manifest.permission.CAMERA)
            R.id.preferencesFragment -> navController.navigate(R.id.preferencesFragment)
            R.id.notificationsFragment -> navController.navigate(R.id.notificationsFragment)
            R.id.businessFragment -> navController.navigate(R.id.manageBusinessFragment)
        }
        return true
    }
}