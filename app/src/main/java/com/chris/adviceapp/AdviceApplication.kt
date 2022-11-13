package com.chris.adviceapp

import android.app.Application
import com.chris.adviceapp.database.AdviceRoomDatabase
import com.chris.adviceapp.repository.AdviceDatabaseRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob

class AdviceApplication : Application() {

    val applicationScope = CoroutineScope(SupervisorJob())

    val database by lazy { AdviceRoomDatabase.getDatabase(applicationScope,this) }
    val repository by lazy { AdviceDatabaseRepository(database.adviceDao()) }

}