package com.chris.adviceapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.chris.adviceapp.repository.AdviceAPIRepository
import com.chris.adviceapp.util.AdviceState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch

class AdviceViewModel(
    private val repository: AdviceAPIRepository
)  : ViewModel() {

    private val adviceStateFlow: MutableStateFlow<AdviceState> = MutableStateFlow(AdviceState.Empty)
    val _adviceStateFlow: StateFlow<AdviceState> = adviceStateFlow

    fun getAdvice() = viewModelScope.launch {
        repository.getAdvice()
            .onStart { adviceStateFlow.value = AdviceState.onLoading }
            .catch { e -> adviceStateFlow.value = AdviceState.onError(e) }
            .collect{ data -> adviceStateFlow.value = AdviceState.onSuccess(data) }
    }

}