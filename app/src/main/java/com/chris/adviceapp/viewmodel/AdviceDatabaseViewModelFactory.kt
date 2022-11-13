package com.chris.adviceapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.chris.adviceapp.repository.AdviceDatabaseRepository

class AdviceDatabaseViewModelFactory constructor(private val repository: AdviceDatabaseRepository): ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return if (modelClass.isAssignableFrom(AdviceDatabaseViewModel::class.java)) {
            AdviceDatabaseViewModel(this.repository) as T
        } else {
            throw IllegalArgumentException("ViewModel Not Found")
        }
    }
}