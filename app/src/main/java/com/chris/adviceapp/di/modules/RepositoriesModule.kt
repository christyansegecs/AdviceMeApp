package com.chris.adviceapp.di.modules

import com.chris.adviceapp.api.AdviceAPI
import com.chris.adviceapp.repository.AdviceAPIRepository
import com.chris.adviceapp.repository.AdviceAPIRepositoryImpl
import org.koin.dsl.module

val repositoriesModule = module {
    single<AdviceAPIRepository> { provideAdviceAPIRepository(get()) }
}

fun provideAdviceAPIRepository(adviceAPI: AdviceAPI): AdviceAPIRepositoryImpl {
    return AdviceAPIRepositoryImpl(
        adviceAPI
    )
}