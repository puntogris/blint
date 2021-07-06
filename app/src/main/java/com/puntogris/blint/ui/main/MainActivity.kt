package com.puntogris.blint.ui.main

import android.Manifest
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.annotation.MenuRes
import androidx.appcompat.app.AppCompatDelegate
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.findNavController
import androidx.navigation.ui.*
import com.google.android.material.badge.BadgeUtils
import com.puntogris.blint.R
import com.puntogris.blint.databinding.ActivityMainBinding
import com.puntogris.blint.ui.SharedPref
import com.puntogris.blint.ui.base.BaseActivity
import com.puntogris.blint.ui.nav.*
import com.puntogris.blint.utils.*
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi
import javax.inject.Inject

@ExperimentalCoroutinesApi
@AndroidEntryPoint
class MainActivity : BaseActivity<ActivityMainBinding>(R.layout.activity_main) {

    private val viewModel: MainViewModel by viewModels()
    private lateinit var navController: NavController
    private lateinit var appBarConfiguration: AppBarConfiguration
    lateinit var requestPermissionLauncher: ActivityResultLauncher<String>

    private val bottomNavDrawer: BottomNavDrawerFragment by lazy(LazyThreadSafetyMode.NONE) {
        supportFragmentManager.findFragmentById(R.id.bottom_nav_drawer) as BottomNavDrawerFragment
    }

    @Inject
    lateinit var sharedPref: SharedPref

    override fun preInitViews() {
        setTheme(R.style.Theme_Blint)
    }

    override fun initializeViews() {
        setUpTheme()
        setUpNavigation()
        setUpScanner()
        setUpBottomDrawer()
    }

    private fun setUpTheme() {
        AppCompatDelegate.setDefaultNightMode(sharedPref.getThemePref())
    }

    private fun setUpScanner() {
        requestPermissionLauncher =
            registerForActivityResult(ActivityResultContracts.RequestPermission())
            { isGranted: Boolean ->
                if (isGranted) navController.navigate(R.id.scannerFragment)
                else showLongSnackBarAboveFab(getString(R.string.snack_require_camera_permission))
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
                R.id.loginFragment,
                R.id.welcomeFragment,
                R.id.introFragment,
                R.id.firstSyncFragment,
                R.id.newUserFragment
            )
        )
        setupActionBarWithNavController(navController, appBarConfiguration)

        binding.bottomAppBar.apply {
            setNavigationOnClickListener {
                bottomNavDrawer.toggle()
//                if(sharedPref.getUserHasBusinessPref()){
//                    if (navController.currentDestination?.id != R.id.mainFragment)
//                        navController.navigate(R.id.mainFragment)
//                }else navController.navigate(R.id.newUserFragment)
            }
            setOnMenuItemClickListener(this@MainActivity)
        }
        binding.notification.setOnClickListener {
            navController.navigate(R.id.notificationsFragment)
        }
    }

    fun changeFabStateBottomSheet(value: Boolean) {
        bottomNavDrawer.addOnStateChangedAction(ShowHideFabStateAction(binding.mainFab, value))
    }

    private fun navigateToMenuDestinations(navMenu: NavMenu) {
        when(navMenu){
            NavMenu.HOME -> R.id.mainFragment
            NavMenu.PRODUCTS -> R.id.manageProductsFragment
            NavMenu.CLIENTS -> R.id.manageClientsFragment
            NavMenu.SUPPLIERS -> R.id.manageSuppliersFragment
            NavMenu.ORDERS -> R.id.manageOrdersFragment
            NavMenu.NOTIFICATIONS -> R.id.notificationsFragment
            NavMenu.SETTINGS -> R.id.preferencesFragment
        }.apply { navController.navigate(this) }
        bottomNavDrawer.close()
    }


    private fun setUpBottomDrawer(){
        bottomNavDrawer.apply {
            addOnStateChangedAction(ChangeSettingsMenuStateAction { showSettings ->
                binding.bottomAppBar.replaceMenu(
                    if (showSettings) R.menu.bottom_app_bar_settings_menu
                    else getBottomAppBarMenuForDestination()
                )
            })
            addNavigationListener(this@MainActivity)
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }

    @MenuRes
    private fun getBottomAppBarMenuForDestination(destination: NavDestination? = null): Int {
        val dest = destination ?: findNavController(R.id.nav_host_fragment).currentDestination
        return when (dest?.id) {
            R.id.mainFragment -> R.menu.bottom_app_bar_home_menu
            else -> R.menu.bottom_app_bar_home_menu
        }
    }

    private fun setBottomAppBarForHome(@MenuRes menuRes: Int) {
        binding.run {
            bottomAppBar.visibility = View.VISIBLE
            bottomAppBar.replaceMenu(menuRes)
            bottomAppBar.performShow()
        }
    }

    override fun onDestinationChanged(
        controller: NavController,
        destination: NavDestination,
        arguments: Bundle?
    ) {
        if(destination.id == R.id.mainFragment){
            setToolbarAndStatusBarColor(R.color.colorSecondary)
            binding.toolbar.setTitleTextColor(getColor(R.color.white))
            setBottomAppBarForHome(getBottomAppBarMenuForDestination(destination))
            binding.notification.visible()
            binding.badge.visible()
            if (!isDarkThemeOn()){
                val view = window.decorView
                view.setSystemUiVisibility(view.getSystemUiVisibility() and View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR.inv())
            }
        }else{
            binding.badge.gone()
            binding.notification.gone()
            setupToolbarAndStatusBar()
        }
        hideKeyboard()
    }

    private fun setupToolbarAndStatusBar(){
        val view = window.decorView
        if (isDarkThemeOn()){
            setToolbarAndStatusBarColor(R.color.nightBackground)
            view.systemUiVisibility = view.systemUiVisibility and View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR.inv()
        }else{
            setToolbarAndStatusBarColor(R.color.grey_3)
            binding.toolbar.setTitleTextColor(getColor(R.color.grey_60))
            view.systemUiVisibility = view.systemUiVisibility or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
        }
    }

    override fun onMenuItemClick(menuItem: MenuItem?): Boolean {
        when (menuItem?.itemId) {
            R.id.menu_settings -> {
                bottomNavDrawer.close()
                navController.navigate(R.id.preferencesFragment)
            }
            R.id.menu_search -> if (checkIfBusinessExist()) requestPermissionLauncher.launch(
                Manifest.permission.CAMERA
            )
            R.id.businessFragment -> navController.navigate(R.id.manageBusinessFragment)
        }
        return true
    }

    override fun onNavMenuItemClicked(item: NavigationModelItem.NavMenuItem) {
        navigateToMenuDestinations(item.navMenu)
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