package com.chris.adviceapp.api

import com.chris.adviceapp.model.Advice
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET

interface AdviceService {

    @GET("advice")
    suspend fun getAdvice() : Response<Advice>


    companion object {

        private val retrofitService: AdviceService by lazy {

            val retrofitService = Retrofit.Builder()
                .baseUrl("https://api.adviceslip.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .build()

            retrofitService.create(AdviceService::class.java)
        }

        fun getInstance(): AdviceService {
            return retrofitService
        }
    }
}