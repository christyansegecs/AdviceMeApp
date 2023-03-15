package com.chris.adviceapp.di.modules

import com.chris.adviceapp.repository.AdviceAPIRepository
import com.chris.adviceapp.viewmodel.AdviceViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val viewModelsModule = module {
    viewModel { provideUserViewModel(get()) }
}

fun provideUserViewModel(adviceAPIRepository: AdviceAPIRepository): AdviceViewModel {
    return AdviceViewModel(
        adviceAPIRepository
    )
}