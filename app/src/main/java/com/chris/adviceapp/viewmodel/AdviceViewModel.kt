package com.chris.adviceapp.viewmodel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.chris.adviceapp.model.Advice
import com.chris.adviceapp.repository.AdviceRepository
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AdviceViewModel constructor(private val repository: AdviceRepository)  : ViewModel() {

    val adviceString = MutableLiveData<Advice>()
    val errorMessage = MutableLiveData<String>()

    fun getAdvice() {

        val request = repository.getAdvice1()
        request.enqueue(object : Callback<Advice> {
            override fun onResponse(call: Call<Advice>, response: Response<Advice>) {
                if (response.isSuccessful) {
                    adviceString.postValue(response.body())
                    Log.d("apiOnResponse", response.body().toString())
                } else{
                    errorMessage.postValue("Erro no Aconselhamento ${response.code()}")
                }
            }

            override fun onFailure(call: Call<Advice>, t: Throwable) {
                errorMessage.postValue(t.message)
                Log.d("apiResponse", "Falha: ${request}")
                t.printStackTrace();
            }
        })
    }

}
