package com.chris.adviceapp.api

import com.chris.adviceapp.model.Advice
import com.chris.adviceapp.model.Image
import retrofit2.Response
import retrofit2.http.GET

interface IMG4MeAPI {
    @GET("advice")
    suspend fun getImage() : Response<Advice>
}