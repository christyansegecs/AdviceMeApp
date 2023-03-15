package com.chris.adviceapp

import android.app.Application
import com.chris.adviceapp.di.appModules
import org.koin.core.context.startKoin

class AdviceApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        startKoin {
            modules(
                appModules
            )
        }
    }
}