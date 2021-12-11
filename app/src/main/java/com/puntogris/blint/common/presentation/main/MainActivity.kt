package com.puntogris.blint.common.presentation.main

import android.Manifest
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.annotation.DrawableRes
import androidx.core.view.isVisible
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.ui.*
import com.google.android.material.bottomappbar.BottomAppBar
import com.google.android.material.snackbar.Snackbar
import com.puntogris.blint.R
import com.puntogris.blint.common.presentation.base.BaseActivity
import com.puntogris.blint.common.utils.*
import com.puntogris.blint.databinding.ActivityMainBinding
import com.puntogris.blint.feature_store.presentation.main.MainBottomNavDrawer
import com.puntogris.blint.feature_store.presentation.main.MainViewModel
import dagger.hilt.android.AndroidEntryPoint
import java.util.*

@AndroidEntryPoint
class MainActivity : BaseActivity<ActivityMainBinding>(R.layout.activity_main) {

    private val viewModel: MainViewModel by viewModels()
    private lateinit var navController: NavController
    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var requestPermissionLauncher: ActivityResultLauncher<String>

    override fun preInitViews() {
        setTheme(R.style.Blint_Theme_DayNight)
    }

    override fun initializeViews() {
        setUpNavigation()
        setUpScanner()
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
                setStartDestination(
                    when {
                        viewModel.showLoginScreen() -> R.id.loginFragment
                        viewModel.showNewUserScreen() -> R.id.newUserFragment
                        else -> R.id.homeFragment
                    }
                )
            }
        navController.addOnDestinationChangedListener(this@MainActivity)

        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.homeFragment,
                R.id.loginFragment,
                R.id.syncAccountFragment,
                R.id.newUserFragment,
                R.id.publishOrderFragment
            )
        )
        setupActionBarWithNavController(navController, appBarConfiguration)

        binding.bottomAppBar.apply {
            setNavigationOnClickListener {
                showBottomDrawer()
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
        hideKeyboard()
    }

    private fun showBottomDrawer() {
        MainBottomNavDrawer.newInstance().show(supportFragmentManager, MainBottomNavDrawer.TAG)
    }

    override fun onMenuItemClick(menuItem: MenuItem?): Boolean {
        when (menuItem?.itemId) {
            R.id.menu_search -> requestPermissionLauncher.launch(Manifest.permission.CAMERA)
            R.id.businessFragment -> navController.navigate(R.id.manageBusinessFragment)
        }
        return true
    }

    override fun registerUi(
        showFab: Boolean,
        showAppBar: Boolean,
        showToolbar: Boolean,
        showFabCenter: Boolean,
        @DrawableRes fabIcon: Int,
        fabListener: View.OnClickListener?
    ) {
        binding.toolbar.isVisible = showToolbar
        if (showFab) {
            binding.fab.apply {
                show()
                setFabImageAndClickListener(fabIcon, fabListener)
            }
            binding.bottomAppBar.fabAlignmentMode =
                if (showFabCenter) BottomAppBar.FAB_ALIGNMENT_MODE_CENTER
                else BottomAppBar.FAB_ALIGNMENT_MODE_END
        } else binding.fab.hide()

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
        }.show()
    }
}