package com.chris.adviceapp.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.chris.adviceapp.repository.FirebaseRepository
import com.chris.adviceapp.usermodel.AdviceFirebase
import com.chris.adviceapp.usermodel.User
import com.chris.adviceapp.util.AuthState
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class FirebaseViewModel(
    private val repository: FirebaseRepository
)  : ViewModel() {

    private val authStateFlow: MutableStateFlow<AuthState> = MutableStateFlow(AuthState.Empty)
    val _authStateFlow: StateFlow<AuthState> = authStateFlow
    val currentUserLiveData = MutableLiveData<FirebaseUser?>()

    fun signIn(email: String, password: String) {
        viewModelScope.launch {
                repository.signIn(email, password)
                    .onStart { authStateFlow.value = AuthState.onLoading }
                    .catch { e -> authStateFlow.value = AuthState.onError(e) }
                    .collect{ data -> authStateFlow.value = AuthState.onSuccess(data) }
        }
    }

    fun getCurrentUser() {
        viewModelScope.launch {
            val response = withContext(Dispatchers.Default) {
                repository.getCurrentUser()
            }
            currentUserLiveData.value = response
        }
    }

    fun sendNewPasswordToEmail(email: String) {
        viewModelScope.launch {
            withContext(Dispatchers.Default) {
                repository.sendNewPasswordToEmail(email)
            }
        }
    }

    fun saveUser(user: User) {
        viewModelScope.launch {
            withContext(Dispatchers.Default) {
                repository.saveUser(user)
            }
        }
    }

    fun saveAdvice(advice: AdviceFirebase) {
        viewModelScope.launch {
            withContext(Dispatchers.Default) {
                repository.saveAdvice(advice)
            }
        }
    }

    fun fetchAdvicesFromDatabase() {
        viewModelScope.launch {
            withContext(Dispatchers.Default) {
                repository.fetchAdvicesFromDatabase()
            }
        }
    }

}