package com.puntogris.blint.ui.about

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.ViewModel
import com.puntogris.blint.data.remote.UserRepository

class AboutViewModel @ViewModelInject constructor(private val userRepository: UserRepository):
    ViewModel() {


    suspend fun sendReport(message: String) =
        userRepository.sendReportToFirestore(message)

}