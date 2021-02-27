package com.puntogris.blint.ui.business

import android.os.CountDownTimer
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.Timestamp
import com.puntogris.blint.data.local.dao.EmployeeDao
import com.puntogris.blint.data.remote.UserRepository
import com.puntogris.blint.model.Employee
import com.puntogris.blint.model.EmployeeRequest
import com.puntogris.blint.model.JoinCode

class BusinessViewModel @ViewModelInject constructor(
    private val employeeDao: EmployeeDao,
    private val userRepository: UserRepository): ViewModel() {

    private var expirationTimer:CountDownTimer? = null

    private val _codeExpirationInSeconds = MutableLiveData<Int>()
    val codeExpirationInSeconds: LiveData<Int> = _codeExpirationInSeconds

    suspend fun getBusiness() = employeeDao.getEmployeesList()

    fun getBusinessEmployees(businessId:String) = userRepository.getBusinessEmployees(businessId)

    suspend fun hasUserOwnerPermissions(employeeId:String) :Boolean{
        return employeeDao.getBusinessUserRole(employeeId) == "ADMINISTRATOR"
    }

    suspend fun fetchJoiningCode(businessId: String) = userRepository.generateJoiningCode(businessId)

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

    suspend fun createEmployee(code:String) = userRepository.createEmployeeWithCode(code)

    override fun onCleared() {
        if(expirationTimer == null) expirationTimer?.cancel()
        super.onCleared()
    }

}