package com.chris.adviceapp.repository

import com.chris.adviceapp.api.AdviceService
import com.chris.adviceapp.model.Advice
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import retrofit2.Response

class AdviceRepository constructor (private val retrofitService: AdviceService) {

    fun getAdvice(): Flow<Response<Advice>> = flow {
        emit(retrofitService.getAdvice())
    }.flowOn(Dispatchers.IO)

}