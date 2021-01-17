package com.puntogris.blint.data.remote

import com.puntogris.blint.utils.RepoResult

interface IBackupRepository {
    fun createBackupForBusiness(list: List<String>): RepoResult
}