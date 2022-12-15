package com.chris.adviceapp.repository

import com.chris.adviceapp.api.AdviceService

class AdviceRepository constructor (private val retrofitService: AdviceService) {

    suspend fun getAdvice1() = retrofitService.getAdvice()

}