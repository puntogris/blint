package com.puntogris.blint.data.remote

import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.puntogris.blint.data.local.dao.StatisticsDao
import com.puntogris.blint.data.local.dao.UsersDao
import javax.inject.Inject

class StatisticRepository @Inject constructor(
    private val statisticsDao: StatisticsDao,
    private val usersDao: UsersDao
):IStatisticRepository {

    val firestore = Firebase.firestore

}