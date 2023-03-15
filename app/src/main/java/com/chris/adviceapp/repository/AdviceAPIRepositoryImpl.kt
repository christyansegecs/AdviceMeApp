package com.chris.adviceapp.repository

import com.chris.adviceapp.api.AdviceAPI
import com.chris.adviceapp.model.Advice
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import retrofit2.Response

class AdviceAPIRepositoryImpl(
    private val adviceAPI: AdviceAPI
) : AdviceAPIRepository {

    override suspend fun getAdvice(): Flow<Response<Advice>> = flow {
        emit(adviceAPI.getAdvice())
    }.flowOn(Dispatchers.IO)

}