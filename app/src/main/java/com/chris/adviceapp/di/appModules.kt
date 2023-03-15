package com.chris.adviceapp.di

import com.chris.adviceapp.di.modules.apiModule
import com.chris.adviceapp.di.modules.repositoriesModule
import com.chris.adviceapp.di.modules.viewModelsModule

val appModules = listOf(
    apiModule,
    repositoriesModule,
    viewModelsModule
)