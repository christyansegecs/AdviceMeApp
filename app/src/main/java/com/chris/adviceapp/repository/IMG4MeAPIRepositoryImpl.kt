package com.chris.adviceapp.repository

import com.chris.adviceapp.api.IMG4MeAPI
import com.chris.adviceapp.model.Advice
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import retrofit2.Response

class IMG4MeAPIRepositoryImpl(
    private val iMG4MeAPI: IMG4MeAPI
) : IMG4MeAPIRepository {

    override suspend fun getPicture(): Flow<Response<Advice>> = flow {
        emit(iMG4MeAPI.getImage())
    }.flowOn(Dispatchers.IO)

}