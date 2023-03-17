package com.chris.adviceapp.util

import com.chris.adviceapp.model.Advice
import com.chris.adviceapp.model.Image
import retrofit2.Response

sealed class ImageState {
    object onLoading : ImageState()
    class onError(val msg: Throwable) : ImageState()
    class onSuccess(val data: Response<Advice>) : ImageState()
    object Empty : ImageState()
}