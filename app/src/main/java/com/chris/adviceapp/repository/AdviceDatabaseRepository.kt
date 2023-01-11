package com.chris.adviceapp.repository

import androidx.annotation.WorkerThread
import com.chris.adviceapp.database.dao.AdviceDao
import com.chris.adviceapp.database.models.Advice
import kotlinx.coroutines.flow.Flow

class AdviceDatabaseRepository(private val adviceDao: AdviceDao) {

    val allAdvices: Flow<List<Advice>> = adviceDao.getAlphabetizedAdvices()

    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun insert(advice: Advice) {
        adviceDao.insert(advice)
    }

    @WorkerThread
    suspend fun delete(advice: Advice) {
        adviceDao.delete(advice)
    }
}