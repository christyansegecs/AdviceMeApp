package com.chris.adviceapp.repository

import com.chris.adviceapp.model.Advice
import kotlinx.coroutines.flow.Flow
import retrofit2.Response

interface AdviceAPIRepository {

    suspend fun getAdvice(): Flow<Response<Advice>>

}