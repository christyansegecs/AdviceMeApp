package com.chris.adviceapp.di.modules

import com.chris.adviceapp.api.AdviceAPI
import com.chris.adviceapp.api.IMG4MeAPI
import com.chris.adviceapp.repository.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import org.koin.dsl.module

val repositoriesModule = module {
    single<AdviceAPIRepository> { provideAdviceAPIRepository(get()) }
    single<IMG4MeAPIRepository> { provideIMG4MeAPIRepository(get()) }
    single<FirebaseRepository> { provideAuthRepository(get(), get()) }
}

fun provideAdviceAPIRepository(adviceAPI: AdviceAPI) : AdviceAPIRepositoryImpl {
    return AdviceAPIRepositoryImpl(
        adviceAPI
    )
}

fun provideIMG4MeAPIRepository(iMG4MeAPI: IMG4MeAPI) : IMG4MeAPIRepositoryImpl {
    return IMG4MeAPIRepositoryImpl(
        iMG4MeAPI
    )
}

fun provideAuthRepository(firebaseAuth: FirebaseAuth, database: FirebaseDatabase): FirebaseRepositoryImpl {
    return FirebaseRepositoryImpl(
        firebaseAuth,
        database
    )
}