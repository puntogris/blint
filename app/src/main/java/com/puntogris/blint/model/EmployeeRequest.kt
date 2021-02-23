package com.puntogris.blint.model

import com.google.firebase.Timestamp

class EmployeeRequest (
    val email: String,
    val businessName: String,
    val businessId: String,
    val businessTimestamp: Timestamp,
    val role:String,
    var employeeId:String = "",
    var ownerId:String = ""
    )