package com.puntogris.blint.ui.base

import android.os.Bundle
import android.view.*
import androidx.annotation.LayoutRes
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import com.puntogris.blint.R
import com.puntogris.blint.ui.product.ProductFragment

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

    open fun onEditButtonClicked() {}

    open fun onDeleteButtonClicked(){}

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        menu.findItem(R.id.moreOptions).isVisible = true
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.editOption -> {
                onEditButtonClicked()
                true
            }
            R.id.deleteOption -> {
                onDeleteButtonClicked()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}