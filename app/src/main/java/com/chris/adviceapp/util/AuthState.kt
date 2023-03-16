package com.chris.adviceapp.util

import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult

sealed class AuthState {
    object onLoading : AuthState()
    class onError(val msg:Throwable) : AuthState()
    class onSuccess(val data: Task<AuthResult>) : AuthState()
    object Empty : AuthState()
}