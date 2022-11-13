package com.chris.adviceapp.repository

import androidx.annotation.WorkerThread
import com.chris.adviceapp.database.dao.AdviceDao
import kotlinx.coroutines.flow.Flow

class AdviceDatabaseRepository(private val adviceDao: AdviceDao) {

    val allAdvices: Flow<List<com.chris.adviceapp.database.models.Advice>> = adviceDao.getAlphabetizedAdvices()

    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun insert(advice: com.chris.adviceapp.database.models.Advice) {
        adviceDao.insert(advice)
    }
}