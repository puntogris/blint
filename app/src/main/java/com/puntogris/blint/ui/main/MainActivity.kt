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
import androidx.core.view.isVisible
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
class MainActivity : BaseActivity<ActivityMainBinding>(R.layout.activity_main) {

    override fun registerUi(
        showFab: Boolean,
        showAppBar: Boolean,
        showToolbar: Boolean,
        showFabCenter: Boolean,
        @DrawableRes fabIcon: Int,
        fabListener: View.OnClickListener?
    ) {
        if (showToolbar) binding.toolbar.visible() else binding.toolbar.gone()
        if (showFab) {
            binding.fab.apply {
                show()
                setFabImageAndClickListener(fabIcon, fabListener)
            }
            binding.bottomAppBar.fabAlignmentMode =
                if (showFabCenter) BottomAppBar.FAB_ALIGNMENT_MODE_CENTER
                else BottomAppBar.FAB_ALIGNMENT_MODE_END
        } else binding.fab.hide()

        changeFabStateBottomSheet(showFab)
        if (showAppBar) {
            binding.bottomAppBar.visible()
            binding.bottomAppBar.performShow()
        } else binding.bottomAppBar.gone()
    }

    override fun setBottomAppBarInvisible() {
        binding.bottomAppBar.apply {
            invisible()
            performHide()
        }
    }

    override fun hideFab() {
        binding.fab.hide()
    }

    override fun setFabImageAndClickListener(fabIcon: Int, fabListener: View.OnClickListener?) {
        binding.fab.apply {
            changeIconFromDrawable(fabIcon)
            setOnClickListener(fabListener)
        }
    }

    override fun setFabImage(fabIcon: Int) {
        binding.fab.changeIconFromDrawable(fabIcon)
    }

    override fun showSnackBar(
        message: String,
        duration: Int,
        actionText: Int,
        action: View.OnClickListener?
    ) {
        Snackbar.make(binding.root, message, duration).apply {
            anchorView = when {
                binding.fab.isVisible && binding.bottomAppBar.isVisible -> binding.fab
                binding.fab.isVisible -> binding.fab
                binding.bottomAppBar.isVisible -> binding.bottomAppBar
                else -> null
            }
            if (action != null) setAction(actionText, action)
            show()
        }
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
            lifecycle.repeatOnLifecycle(Lifecycle.State.CREATED) {
                viewModel.accountStatus.collect {
                    when (it) {
                        AccountStatus.Error -> {}
                        is AccountStatus.OutOfSync -> {
                            if (sharedPref.loginCompletedPref()) {
                                val nav = NavOptions.Builder()
                                    .setPopUpTo(navController.graph.startDestination, true).build()
                                navController.navigate(R.id.outOfSyncFragment, null, nav)
                            }
                        }
                        is AccountStatus.Synced -> {
                            if (it.hasBusiness) {
                                if (sharedPref.showNewUserScreenPref()) {
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
                else showSnackBar(getString(R.string.snack_require_camera_permission))
            }
    }

    private fun setUpNavigation() {
        setSupportActionBar(binding.toolbar)
        navController = getNavController()
        navController.graph = navController.navInflater.inflate(R.navigation.navigation)
            .apply {
                startDestination =
                    if (sharedPref.loginCompletedPref()) R.id.loginFragment
                    else R.id.mainFragment
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
    }

    private fun changeFabStateBottomSheet(value: Boolean) {
        bottomNavDrawer.addOnStateChangedAction(ShowHideFabStateAction(binding.fab, value))
    }

    private fun navigateToMenuDestinations(navMenu: NavMenu) {
        if (navMenu != NavMenu.HOME &&
            navMenu != NavMenu.SETTINGS &&
            viewModel.currentUser.value.businessStatus != ENABLED
        ) {
            showSnackBar(getString(R.string.action_require_permissions)) {
                launchWebBrowserIntent(BLINT_WEBSITE_LEARN_MORE)
            }
        } else {
            when (navMenu) {
                NavMenu.HOME -> R.id.mainFragment
                NavMenu.PRODUCTS -> R.id.manageProductsFragment
                NavMenu.CLIENTS -> R.id.manageClientsFragment
                NavMenu.SUPPLIERS -> R.id.manageSuppliersFragment
                NavMenu.ORDERS -> R.id.manageOrdersFragment
                NavMenu.SETTINGS -> R.id.preferencesFragment
            }.apply { navController.navigate(this) }
        }
        bottomNavDrawer.close()
    }

    private fun setUpBottomDrawer() {
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

    override fun onDestinationChanged(
        controller: NavController,
        destination: NavDestination,
        arguments: Bundle?
    ) {
        hideKeyboard()
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