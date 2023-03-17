package com.chris.adviceapp.di.modules

import com.chris.adviceapp.repository.AdviceAPIRepository
import com.chris.adviceapp.repository.FirebaseRepository
import com.chris.adviceapp.repository.IMG4MeAPIRepository
import com.chris.adviceapp.viewmodel.AdviceViewModel
import com.chris.adviceapp.viewmodel.FirebaseViewModel
import com.chris.adviceapp.viewmodel.ImageViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val viewModelsModule = module {
    viewModel { provideAdviceAPIViewModel(get()) }
    viewModel { provideAuthViewModel(get()) }
    viewModel { provideImageViewModel(get()) }
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

fun provideImageViewModel(iMG4MeAPIRepository: IMG4MeAPIRepository): ImageViewModel {
    return ImageViewModel(
        iMG4MeAPIRepository
    )
}