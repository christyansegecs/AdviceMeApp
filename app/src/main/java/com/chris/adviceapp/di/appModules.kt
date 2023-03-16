package com.chris.adviceapp.di

import com.chris.adviceapp.di.modules.*

val appModules = listOf(
    apiModule,
    repositoriesModule,
    viewModelsModule,
    firebaseAuthModule,
    firebaseDatabaseModule
)