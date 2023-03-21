package com.chris.adviceapp.repository

import com.chris.adviceapp.usermodel.AdviceFirebase
import com.chris.adviceapp.usermodel.User
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.flow.Flow

interface FirebaseRepository {

    suspend fun signIn(email: String, password: String): Flow<Task<AuthResult>>

    suspend fun getCurrentUser() : FirebaseUser?

    suspend fun sendNewPasswordToEmail(email: String)

    suspend fun saveUser(user: User)

    suspend fun saveAdvice(advice: AdviceFirebase)
}