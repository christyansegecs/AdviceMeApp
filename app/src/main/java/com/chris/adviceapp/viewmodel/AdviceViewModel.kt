package com.chris.adviceapp.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.chris.adviceapp.model.Advice
import com.chris.adviceapp.repository.AdviceRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.launch

class AdviceViewModel constructor(private val repository: AdviceRepository)  : ViewModel() {

    val adviceString = MutableLiveData<Advice?>()

    fun getAdvice() = CoroutineScope(IO).launch {
        val request = repository.getAdvice1()
        adviceString.postValue(request.body())
    }
}
