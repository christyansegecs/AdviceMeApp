package com.chris.adviceapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.chris.adviceapp.repository.AdviceRepository
import com.chris.adviceapp.util.AdviceState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch

class AdviceViewModel constructor(private val repository: AdviceRepository)  : ViewModel() {

    val adviceStateFlow:MutableStateFlow<AdviceState> = MutableStateFlow(AdviceState.Empty)
    val _adviceStateFlow: StateFlow<AdviceState> = adviceStateFlow

    fun getAdvice() = viewModelScope.launch {
        adviceStateFlow.value = AdviceState.onLoading
        repository.getAdvice()
            .catch { e ->
                adviceStateFlow.value = AdviceState.onError(e)
            }.collect{ data ->
                adviceStateFlow.value = AdviceState.onSuccess(data)
            }
    }

}