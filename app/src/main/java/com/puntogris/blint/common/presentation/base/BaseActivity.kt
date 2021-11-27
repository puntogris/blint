package com.puntogris.blint.common.presentation.base

import android.os.Bundle
import android.view.Menu
import androidx.annotation.LayoutRes
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.navigation.NavController
import com.puntogris.blint.R
import com.puntogris.blint.common.utils.getNavHostFragment
import com.puntogris.blint.feature_store.presentation.main.SetupUiListener

abstract class BaseActivity<T : ViewDataBinding>(@LayoutRes val layout: Int) :
    AppCompatActivity(),
    NavController.OnDestinationChangedListener,
    Toolbar.OnMenuItemClickListener,
    SetupUiListener {

    private var _binding: T? = null
    val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        preInitViews()
        super.onCreate(savedInstanceState)
        _binding = DataBindingUtil.setContentView(this, layout)
        initializeViews()
    }

    open fun initializeViews() {}
    open fun preInitViews() {}

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    override fun onBackPressed() {
        if (isTaskRoot
            && getNavHostFragment().childFragmentManager.backStackEntryCount == 0
            && supportFragmentManager.backStackEntryCount == 0
        ) {
            finishAfterTransition()
        } else super.onBackPressed()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.top_app_bar_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }
}