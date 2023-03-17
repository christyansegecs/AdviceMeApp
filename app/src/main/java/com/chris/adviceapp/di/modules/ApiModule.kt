package com.chris.adviceapp.di.modules

import com.chris.adviceapp.api.AdviceAPI
import com.chris.adviceapp.api.IMG4MeAPI
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

val apiModule = module {
    single { provideAdviceAPI() }
    single { provideIMG4MeAPI() }
}

fun provideAdviceAPI() : AdviceAPI{
    return Retrofit.Builder()
        .baseUrl("https://api.adviceslip.com/")
        .addConverterFactory(GsonConverterFactory.create())
        .build().create(AdviceAPI::class.java)
}

fun provideIMG4MeAPI() : IMG4MeAPI{
    return Retrofit.Builder()
        .baseUrl("https://api.adviceslip.com/")
        .addConverterFactory(GsonConverterFactory.create())
        .build().create(IMG4MeAPI::class.java)
}