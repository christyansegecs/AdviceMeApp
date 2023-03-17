package com.chris.adviceapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.chris.adviceapp.repository.IMG4MeAPIRepository
import com.chris.adviceapp.util.ImageState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch

class ImageViewModel(
    private val repository: IMG4MeAPIRepository
)  : ViewModel() {

    private val imageStateFlow: MutableStateFlow<ImageState> = MutableStateFlow(ImageState.Empty)
    val _imageStateFlow: StateFlow<ImageState> = imageStateFlow

    fun getPictureFromText() = viewModelScope.launch {
        repository.getPicture()
            .onStart { imageStateFlow.value = ImageState.onLoading }
            .catch { e -> imageStateFlow.value = ImageState.onError(e) }
            .collect{ data -> imageStateFlow.value = ImageState.onSuccess(data) }
    }
}