package com.puntogris.blint.ui.main

import android.Manifest
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.annotation.MenuRes
import androidx.annotation.StringRes
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.findNavController
import androidx.navigation.ui.*
import com.google.android.material.bottomappbar.BottomAppBar
import com.google.android.material.transition.MaterialFadeThrough
import com.puntogris.blint.R
import com.puntogris.blint.databinding.ActivityMainBinding
import com.puntogris.blint.ui.SharedPref
import com.puntogris.blint.ui.base.BaseActivity
import com.puntogris.blint.ui.nav.*
import com.puntogris.blint.utils.*
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

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
                bottomNavDrawer.toggle()

//                if(sharedPref.getUserHasBusinessPref()){
//                    if (navController.currentDestination?.id != R.id.mainFragment)
//                        navController.navigate(R.id.mainFragment)
//                }else navController.navigate(R.id.newUserFragment)
            }
            setOnMenuItemClickListener(this@MainActivity)
        }
    }

    private fun navigateToMenuDestinations(@StringRes titleRes: Int, navMenu: NavMenu) {
        when(navMenu){
            NavMenu.HOME -> navController.navigate(R.id.mainFragment)
            NavMenu.PRODUCTOS -> navController.navigate(R.id.manageProductsFragment)
            NavMenu.CLIENTES -> navController.navigate(R.id.manageClientsFragment)
            NavMenu.PROVEEDORES -> navController.navigate(R.id.manageSuppliersFragment)
            NavMenu.MOVIMIENTOS -> navController.navigate(R.id.manageRecordsFragment)
            NavMenu.INFORME -> navController.navigate(R.id.reportsFragment)
            NavMenu.AGENDA -> navController.navigate(R.id.calendarFragment)
            NavMenu.CUENTA -> navController.navigate(R.id.accountPreferences)
            NavMenu.NOTIFICACIONES -> navController.navigate(R.id.notificationsFragment)
            NavMenu.CONFIGURACION -> navController.navigate(R.id.preferencesFragment)
        }
     //   binding.bottomAppBarTitle.text = getString(titleRes)
        bottomNavDrawer.close()
    }


    private fun setUpBottomDrawer(){
        bottomNavDrawer.apply {
            addOnSlideAction(HalfClockwiseRotateSlideAction(binding.bottomAppBarChevron))
            addOnSlideAction(AlphaSlideAction(binding.bottomAppBarTitle, true))
            addOnStateChangedAction(ChangeSettingsMenuStateAction { showSettings ->
//                 Toggle between the current destination's BAB menu and the menu which should
//                 be displayed when the BottomNavigationDrawer is open.
                binding.bottomAppBar.replaceMenu(if (showSettings) {
                    R.menu.bottom_app_bar_settings_menu
                } else {
                    getBottomAppBarMenuForDestination()
                })
            })

            addOnSandwichSlideAction(HalfCounterClockwiseRotateSlideAction(binding.bottomAppBarChevron))
            addNavigationListener(this@MainActivity)
        }

        binding.bottomAppBarContentContainer.setOnClickListener {
            bottomNavDrawer.toggle()
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
            bottomAppBarTitle.visibility = View.VISIBLE
            bottomAppBar.performShow()
        }
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
            destination.id == R.id.restoreBackupFragment ||
            destination.id == R.id.addProductRecordBottomSheet ||
                    destination.id == R.id.addOrderClientSupplierBottomSheet ||
                    destination.id == R.id.addProductRecordBottomSheet
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
            destination.id == R.id.newUserFragment){
            if (destination.id == R.id.mainFragment){
                setBottomAppBarForHome(getBottomAppBarMenuForDestination(destination))
            }
            binding.addFav.hide()
            binding.bottomAppBar.visible()
            binding.toolbar.setBackgroundColor(getColor(R.color.colorSecondary))
            window.statusBarColor = resources.getColor(R.color.colorSecondary)
        }else if(destination.id == R.id.calendarFragment ){
            setupToolbarAndStatusBar()
            binding.addFav.isClickable = false
            binding.bottomAppBar.gone()
            binding.addFav.hide()
            binding.addFav.gone()
        }else if(destination.id == R.id.manageBusinessFragment ||
                destination.id == R.id.employeeFragment ||
                destination.id == R.id.reportsFragment ||
                destination.id == R.id.businessFragment ||
                destination.id == R.id.manageCategoriesFragment ||
                destination.id == R.id.preferencesFragment ){
            setupToolbarAndStatusBar()
            binding.addFav.isClickable = false
            binding.bottomAppBar.visible()
            binding.addFav.hide()
            binding.bottomAppBar.performShow()
        }else if(destination.id == R.id.addBusinessEmployee){
            binding.addFav.isClickable = false
            binding.addFav.hide()
        }
        else if(destination.id == R.id.createRecordFragment ||
                destination.id == R.id.orderTypeFragment ||
                destination.id == R.id.reviewRecordFragment ||
                destination.id == R.id.publishOrderFragment){
            binding.addFav.show()
            binding.bottomAppBar.performHide()
        }
        else {
            setupToolbarAndStatusBar()
            binding.bottomAppBar.visible()
            binding.addFav.show()
            binding.bottomAppBar.performShow()
        }
        if (destination.id == R.id.createRecordFragment ||
            destination.id == R.id.orderTypeFragment ||
            destination.id == R.id.reviewRecordFragment ||
            destination.id == R.id.createRecordFragment||
            destination.id == R.id.publishOrderFragment ||
            destination.id == R.id.addProductRecordBottomSheet ||
            destination.id == R.id.addOrderClientSupplierBottomSheet){
            //rehacer
        }else{
            binding.addFav.changeIconFromDrawable(R.drawable.ic_baseline_add_24)
        }
        bottomNavDrawer.addOnStateChangedAction(ShowHideFabStateAction(binding.addFav, binding.addFav.isVisible))


    }

    private fun setupToolbarAndStatusBar(){
        if (isDarkThemeOn()){
            ContextCompat.getColor(this, R.color.nightBackground).apply {
                window.statusBarColor = this
                binding.toolbar.setBackgroundColor(this)
            }
            val view = window.decorView

            view.setSystemUiVisibility(view.getSystemUiVisibility() and View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR.inv())
        }else{
            ContextCompat.getColor(this, R.color.grey_5).apply {
                window.statusBarColor = this
                binding.toolbar.setBackgroundColor(this)
            }
            
            val view = window.decorView
            view.setSystemUiVisibility(view.getSystemUiVisibility() or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR)
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
        navigateToMenuDestinations(item.titleRes, item.navMenu)
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