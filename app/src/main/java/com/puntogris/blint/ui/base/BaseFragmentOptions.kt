package com.puntogris.blint.ui.base

import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import androidx.annotation.LayoutRes
import androidx.databinding.ViewDataBinding

abstract class BaseFragmentOptions<T : ViewDataBinding>(@LayoutRes layout: Int) : BaseFragment<T>(layout) {

    override fun onCreate(savedInstanceState: Bundle?) {
        setHasOptionsMenu(true)
        super.onCreate(savedInstanceState)
    }

    open fun setUpMenuOptions(menu: Menu) = Unit

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        setUpMenuOptions(menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

}