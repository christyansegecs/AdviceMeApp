package com.chris.adviceapp.api

import com.chris.adviceapp.model.Advice
import retrofit2.Response
import retrofit2.http.GET

interface AdviceAPI {

    @GET("advice")
    suspend fun getAdvice() : Response<Advice>

}