package com.chris.adviceapp.di.modules

import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import org.koin.dsl.module

val firebaseDatabaseModule = module {
    single{
        Firebase.database
    }
}