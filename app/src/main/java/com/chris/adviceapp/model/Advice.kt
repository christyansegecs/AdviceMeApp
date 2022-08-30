package com.chris.adviceapp.model

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class Advice(
    @SerializedName("slip")
    var slip: Slip

) : Serializable

data class Slip(
    @SerializedName("id")
    var id: Int,
    @SerializedName("advice")
    var advice: String
)