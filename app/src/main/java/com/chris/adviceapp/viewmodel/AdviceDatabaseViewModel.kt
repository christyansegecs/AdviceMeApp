package com.chris.adviceapp.viewmodel

import androidx.lifecycle.*
import com.chris.adviceapp.database.models.Advice
import com.chris.adviceapp.repository.AdviceDatabaseRepository
import kotlinx.coroutines.launch

class AdviceDatabaseViewModel (private val repository: AdviceDatabaseRepository) : ViewModel() {

    val allAdvices: LiveData<List<Advice>> = repository.allAdvices.asLiveData()

    fun insert(advice: Advice) = viewModelScope.launch {
        repository.insert(advice)
    }

    fun delete(advice: Advice) = viewModelScope.launch {
        repository.delete(advice)
    }
}