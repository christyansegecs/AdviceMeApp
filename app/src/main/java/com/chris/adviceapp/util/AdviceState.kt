package com.chris.adviceapp.util

import com.chris.adviceapp.model.Advice
import retrofit2.Response

sealed class AdviceState{
    object onLoading : AdviceState()
    class onError(val msg:Throwable) : AdviceState()
    class onSuccess(val data: Response<Advice>) : AdviceState()
    object Empty : AdviceState()
}
