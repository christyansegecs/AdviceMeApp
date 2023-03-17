package com.chris.adviceapp.repository

import com.chris.adviceapp.model.Advice
import com.chris.adviceapp.model.Image
import kotlinx.coroutines.flow.Flow
import retrofit2.Response

interface IMG4MeAPIRepository {

    suspend fun getPicture(): Flow<Response<Advice>>
}