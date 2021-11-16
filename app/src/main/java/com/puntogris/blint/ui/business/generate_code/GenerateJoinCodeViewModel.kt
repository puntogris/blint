package com.puntogris.blint.ui.business.generate_code

import android.os.CountDownTimer
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.Timestamp
import com.puntogris.blint.data.repository.business.BusinessRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class GenerateJoinCodeViewModel @Inject constructor(
    private val businessRepository: BusinessRepository
): ViewModel() {

    private var expirationTimer:CountDownTimer? = null

    private val _codeExpirationInSeconds = MutableLiveData<Int>()
    val codeExpirationInSeconds: LiveData<Int> = _codeExpirationInSeconds

    suspend fun fetchJoiningCode(businessId: String) = businessRepository.generateJoiningCode(businessId)

    fun codeExpirationCountDown(timestamp: Timestamp){
        val lastCodeTime = timestamp.seconds
        val timeNow = Timestamp.now().seconds
        val timeDifference = 600 - (timeNow - lastCodeTime)

        expirationTimer = object: CountDownTimer(timeDifference * 1000, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                _codeExpirationInSeconds.value = (millisUntilFinished / 1000).toInt()
            }

            override fun onFinish() {}
        }
        expirationTimer?.start()
    }

    fun cancelExpirationTimer(){
        expirationTimer?.cancel()
    }
    override fun onCleared() {
        expirationTimer?.cancel()
        super.onCleared()
    }

}