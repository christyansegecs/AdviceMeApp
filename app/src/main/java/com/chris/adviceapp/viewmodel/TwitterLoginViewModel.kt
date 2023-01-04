package com.chris.adviceapp.viewmodel

import android.app.Activity
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.OAuthProvider

class TwitterLoginViewModel : ViewModel() {

    var authResponse = MutableLiveData<AuthResult?>()

    fun signInWithTwitter(activity : Activity, firebaseAuth: FirebaseAuth) {
        Log.d("Twitter","entrou na viewmodel")
        val pendingAuthResult: Task<AuthResult>? = firebaseAuth.pendingAuthResult
        if (pendingAuthResult != null) {
            pendingAuthResult
                .addOnSuccessListener { authResult -> authResponse.postValue(authResult) }
                .addOnFailureListener{ Log.e("twitter", "pending result failed!", it) }
            authResponse.postValue(null)
        } else {
            val oAuthProvider :  OAuthProvider.Builder = OAuthProvider.newBuilder("twitter.com")
            firebaseAuth.startActivityForSignInWithProvider(activity, oAuthProvider.build())
                .addOnSuccessListener { authResult -> authResponse.postValue(authResult) }
                .addOnFailureListener { Log.e("twitter", "failed to authenticate!", it) }
            authResponse.postValue(null)

        }
    }

}