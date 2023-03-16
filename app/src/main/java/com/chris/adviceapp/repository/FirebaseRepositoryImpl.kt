package com.chris.adviceapp.repository

import com.chris.adviceapp.usermodel.AdviceFirebase
import com.chris.adviceapp.usermodel.User
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext

class FirebaseRepositoryImpl(
    private val firebaseAuth: FirebaseAuth,
    private val database: FirebaseDatabase
    ) : FirebaseRepository {

    override suspend fun signIn(email: String, password: String): Flow<Task<AuthResult>> = flow {
        emit(firebaseAuth.signInWithEmailAndPassword(email, password))
    }.flowOn(Dispatchers.IO)

    override suspend fun getCurrentUser(): FirebaseUser? {
        val user = firebaseAuth
        return withContext(Dispatchers.Default) {
            user.currentUser
        }
    }

    override suspend fun sendNewPasswordToEmail(email: String) {
        firebaseAuth.sendPasswordResetEmail(email)
    }

    override suspend fun saveUser(user: User) {
        val auth = FirebaseAuth.getInstance()
        val userAuth = auth.currentUser
        database.getReference("/users/${userAuth?.uid}").setValue(user)
    }

    override suspend fun saveAdvice(advice: AdviceFirebase) {
        val auth = FirebaseAuth.getInstance()
        val userAuth = auth.currentUser
        database.getReference("/users/${userAuth?.uid}")
            .child("Advices")
            .push()
            .setValue(advice)
    }

    override suspend fun fetchAdvicesFromDatabase() {
        val auth = FirebaseAuth.getInstance()
        val userAuth = auth.currentUser

        return withContext(Dispatchers.Default) {
            database.getReference("/users/${userAuth?.uid}")
                .child("Advices")
        }
    }

}