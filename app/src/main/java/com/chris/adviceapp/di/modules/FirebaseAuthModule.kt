package com.chris.adviceapp.di.modules
import com.google.firebase.auth.FirebaseAuth
import org.koin.dsl.module


val firebaseAuthModule = module {
    single{
        FirebaseAuth.getInstance()
    }
}