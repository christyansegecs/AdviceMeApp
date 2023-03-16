package com.chris.adviceapp.di.modules

import com.chris.adviceapp.repository.AdviceAPIRepository
import com.chris.adviceapp.repository.FirebaseRepository
import com.chris.adviceapp.viewmodel.AdviceViewModel
import com.chris.adviceapp.viewmodel.FirebaseViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val viewModelsModule = module {
    viewModel { provideAdviceAPIViewModel(get()) }
    viewModel { provideAuthViewModel(get()) }
}

fun provideAdviceAPIViewModel(adviceAPIRepository: AdviceAPIRepository): AdviceViewModel {
    return AdviceViewModel(
        adviceAPIRepository
    )
}

fun provideAuthViewModel(firebaseRepository: FirebaseRepository): FirebaseViewModel {
    return FirebaseViewModel(
        firebaseRepository
    )
}