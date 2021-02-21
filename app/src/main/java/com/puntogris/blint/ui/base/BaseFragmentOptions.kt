package com.puntogris.blint.ui.base

import android.os.Bundle
import android.view.*
import androidx.annotation.LayoutRes
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import com.puntogris.blint.R
import com.puntogris.blint.ui.product.ProductFragment
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.HiltAndroidApp

abstract class BaseFragmentOptions<T: ViewDataBinding>(@LayoutRes val layout: Int): Fragment() {

    private var _binding : T? = null
    val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        setHasOptionsMenu(true)
        super.onCreate(savedInstanceState)
        preInitializeViews()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = DataBindingUtil.inflate(inflater, layout, container, false)
        initializeViews()
        return binding.root
    }

    open fun initializeViews() {}

    open fun preInitializeViews() {}

    open fun setUpMenuOptions(menu: Menu){}


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        setUpMenuOptions(menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

}