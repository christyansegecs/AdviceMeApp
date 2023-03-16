package com.chris.adviceapp.di.modules

import com.chris.adviceapp.api.AdviceAPI
import com.chris.adviceapp.repository.AdviceAPIRepository
import com.chris.adviceapp.repository.AdviceAPIRepositoryImpl
import com.chris.adviceapp.repository.FirebaseRepository
import com.chris.adviceapp.repository.FirebaseRepositoryImpl
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import org.koin.dsl.module

val repositoriesModule = module {
    single<AdviceAPIRepository> { provideAdviceAPIRepository(get()) }
    single<FirebaseRepository> { provideAuthRepository(get(), get()) }
}

fun provideAdviceAPIRepository(adviceAPI: AdviceAPI): AdviceAPIRepositoryImpl {
    return AdviceAPIRepositoryImpl(
        adviceAPI
    )
}

fun provideAuthRepository(firebaseAuth: FirebaseAuth, database: FirebaseDatabase): FirebaseRepositoryImpl {
    return FirebaseRepositoryImpl(
        firebaseAuth,
        database
    )
}