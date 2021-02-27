package com.puntogris.blint.ui.main

import android.content.Intent
import android.net.Uri
import androidx.navigation.fragment.findNavController
import com.puntogris.blint.R
import com.puntogris.blint.databinding.FragmentNewUserBinding
import com.puntogris.blint.ui.base.BaseFragment

class NewUserFragment : BaseFragment<FragmentNewUserBinding>(R.layout.fragment_new_user) {

    override fun initializeViews() {
        binding.fragment = this
    }

    fun onLearnMoreClicked(){
        val url = "https://www.google.com/search?q=chipa&sxsrf=ALeKk00Nc5xDlEXk6-C7Hy2RC2_6kqWPfw:1614392103208&source=lnms&tbm=isch&sa=X&ved=2ahUKEwiC3tue_4jvAhVrIbkGHbZkDNYQ_AUoAXoECAoQAw&biw=1745&bih=887"
        val openBrowserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
        startActivity(openBrowserIntent)
    }
    fun onManageBusinessClicked(){
        findNavController().navigate(R.id.manageBusinessFragment)
    }
}