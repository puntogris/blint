package com.puntogris.blint.feature_store.presentation.settings

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.puntogris.blint.R
import com.puntogris.blint.common.utils.launchAndRepeatWithViewLifecycle
import com.puntogris.blint.common.utils.registerToolbarBackButton
import com.puntogris.blint.common.utils.setDateFromFirebaseUser
import com.puntogris.blint.common.utils.setUserDataImage
import com.puntogris.blint.common.utils.viewBinding
import com.puntogris.blint.databinding.FragmentUserAccountBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest

@AndroidEntryPoint
class UserAccountFragment : Fragment(R.layout.fragment_user_account) {

    private val viewModel: PreferencesViewModel by viewModels()

    private val binding by viewBinding(FragmentUserAccountBinding::bind)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        registerToolbarBackButton(binding.toolbar)

        launchAndRepeatWithViewLifecycle {
            viewModel.currentUser.collectLatest { user ->
                if (user != null) {
                    setDateFromFirebaseUser(binding.textViewUserRegistrationDate, user.createdAt)
                    binding.textViewUserEmail.text = user.email
                    binding.textViewUserName.text = user.name
                    setUserDataImage(binding.imageViewUserPhoto, user.photoUrl)
                }
            }
        }
    }
}
