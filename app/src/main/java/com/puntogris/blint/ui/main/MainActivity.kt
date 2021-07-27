package com.puntogris.blint.ui.main

import android.Manifest
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.annotation.DrawableRes
import androidx.annotation.MenuRes
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.view.WindowInsetsControllerCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.NavOptions
import androidx.navigation.findNavController
import androidx.navigation.ui.*
import com.google.android.material.bottomappbar.BottomAppBar
import com.google.android.material.snackbar.Snackbar
import com.puntogris.blint.R
import com.puntogris.blint.databinding.ActivityMainBinding
import com.puntogris.blint.ui.SharedPref
import com.puntogris.blint.ui.base.BaseActivity
import com.puntogris.blint.ui.nav.*
import com.puntogris.blint.utils.*
import com.puntogris.blint.utils.Constants.BLINT_WEBSITE_LEARN_MORE
import com.puntogris.blint.utils.Constants.ENABLED
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

@ExperimentalCoroutinesApi
@AndroidEntryPoint
class MainActivity: BaseActivity<ActivityMainBinding>(R.layout.activity_main), SetupUiListener {

    override fun register(showFab: Boolean,
                          showAppBar: Boolean,
                          showToolbar: Boolean,
                          showFabCenter: Boolean,
                          @DrawableRes fabIcon: Int,
                          fabListener: View.OnClickListener?) {
       binding.toolbar.visibility = if (showToolbar) View.VISIBLE else View.GONE
        if (showFab) {
            binding.mainFab.apply {
                show()
                changeIconFromDrawable(fabIcon)
                setOnClickListener(fabListener)
            }
            binding.bottomAppBar.fabAlignmentMode =
                if (showFabCenter) BottomAppBar.FAB_ALIGNMENT_MODE_CENTER
                else BottomAppBar.FAB_ALIGNMENT_MODE_END

        } else binding.mainFab.hide()
        changeFabStateBottomSheet(showFab)
        if (showAppBar) {
            binding.bottomAppBar.visible()
            binding.bottomAppBar.performShow()
        } else binding.bottomAppBar.gone()
    }

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
        AppCompatDelegate.setDefaultNightMode(sharedPref.getThemePref())
    }

    override fun initializeViews() {
        setUpNavigation()
        setUpScanner()
        setUpBottomDrawer()
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)

        lifecycleScope.launch {
            lifecycle.repeatOnLifecycle(Lifecycle.State.CREATED){
                viewModel.accountStatus.collect {
                    when(it){
                        AccountStatus.Error -> {}
                        is AccountStatus.OutOfSync -> {
                            if(sharedPref.loginCompletedPref()){
                                val nav = NavOptions.Builder().setPopUpTo(navController.graph.startDestination, true).build()
                                navController.navigate(R.id.outOfSyncFragment, null, nav)
                            }
                        }
                        is AccountStatus.Synced -> {
                            if(it.hasBusiness) {
                                if (sharedPref.showNewUserScreenPref()){
                                    sharedPref.setShowNewUserScreenPref(false)
                                    if (navController.currentDestination?.id == R.id.newUserFragment)
                                        navController.navigate(R.id.mainFragment)
                                }
                            }
                        }
                    }
                }
            }
        }
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
                    if (viewModel.isUserLoggedIn() && sharedPref.loginCompletedPref()) {
                        when {
                            sharedPref.showNewUserScreenPref() -> R.id.newUserFragment
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
                R.id.loginFragment,
                R.id.welcomeFragment,
                R.id.introFragment,
                R.id.syncAccountFragment,
                R.id.newUserFragment,
                R.id.outOfSyncFragment,
                R.id.publishOrderFragment
            )
        )
        setupActionBarWithNavController(navController, appBarConfiguration)

        binding.bottomAppBar.apply {
            setNavigationOnClickListener {
                bottomNavDrawer.toggle()
            }
            setOnMenuItemClickListener(this@MainActivity)
        }
        binding.notification.setOnClickListener {
            navController.navigate(R.id.notificationsFragment)
        }
    }

    private fun changeFabStateBottomSheet(value: Boolean) {
        bottomNavDrawer.addOnStateChangedAction(ShowHideFabStateAction(binding.mainFab, value))
    }

    private fun navigateToMenuDestinations(navMenu: NavMenu) {
        if (navMenu != NavMenu.HOME &&
            navMenu != NavMenu.SETTINGS &&
            navMenu != NavMenu.NOTIFICATIONS &&
            viewModel.currentUser.value.currentBusinessStatus != ENABLED) {
            Snackbar.make(binding.root, getString(R.string.action_require_permissions), Snackbar.LENGTH_LONG)
                .setAction(getString(R.string.read_more)){
                    launchWebBrowserIntent(BLINT_WEBSITE_LEARN_MORE)
                }.show()
        }else{
            when(navMenu){
                NavMenu.HOME -> R.id.mainFragment
                NavMenu.PRODUCTS -> R.id.manageProductsFragment
                NavMenu.CLIENTS -> R.id.manageClientsFragment
                NavMenu.SUPPLIERS -> R.id.manageSuppliersFragment
                NavMenu.ORDERS -> R.id.manageOrdersFragment
                NavMenu.NOTIFICATIONS -> R.id.notificationsFragment
                NavMenu.SETTINGS -> R.id.preferencesFragment
            }.apply { navController.navigate(this) }
        }
        bottomNavDrawer.close()
    }

    private fun setUpBottomDrawer(){
        bottomNavDrawer.apply {
            addOnStateChangedAction(ChangeSettingsMenuStateAction { showSettings ->
                this@MainActivity.binding.bottomAppBar.replaceMenu(
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
            setBottomAppBarForHome(getBottomAppBarMenuForDestination(destination))
            binding.apply {
                toolbar.setTitleTextColor(getColor(R.color.white))
                notification.visible()
                badge.visible()
            }
            val wic = WindowInsetsControllerCompat(window, window.decorView)
            wic.isAppearanceLightStatusBars = false
        }else{
            binding.badge.gone()
            binding.notification.gone()
            setupToolbarAndStatusBar()
        }
        hideKeyboard()
    }

    private fun setupToolbarAndStatusBar(){
        val wic = WindowInsetsControllerCompat(window, window.decorView)
        if (isDarkThemeOn()){
            setToolbarAndStatusBarColor(R.color.nightBackground)
            wic.isAppearanceLightStatusBars = false
        }else{
            setToolbarAndStatusBarColor(R.color.grey_3)
            binding.toolbar.setTitleTextColor(getColor(R.color.grey_60))
            wic.isAppearanceLightStatusBars = true
        }
    }

    override fun onMenuItemClick(menuItem: MenuItem?): Boolean {
        when (menuItem?.itemId) {
            R.id.menu_settings -> {
                bottomNavDrawer.close()
                navController.navigate(R.id.preferencesFragment)
            }
            R.id.menu_search -> requestPermissionLauncher.launch(Manifest.permission.CAMERA)
            R.id.businessFragment -> navController.navigate(R.id.manageBusinessFragment)
        }
        return true
    }

    override fun onNavMenuItemClicked(item: NavigationModelItem.NavMenuItem) {
        navigateToMenuDestinations(item.navMenu)
    }

}