package com.chris.adviceapp.di.modules

import com.chris.adviceapp.api.AdviceAPI
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

val apiModule = module {
    single { provideAdviceAPI() }
}

fun provideAdviceAPI() : AdviceAPI{
    return Retrofit.Builder()
        .baseUrl("https://api.adviceslip.com/")
        .addConverterFactory(GsonConverterFactory.create())
        .build().create(AdviceAPI::class.java)
}