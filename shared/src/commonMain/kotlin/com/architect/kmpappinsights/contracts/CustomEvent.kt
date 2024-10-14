package com.architect.kmpappinsights.contracts

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CustomEvent(
    @SerialName("name") val name: String,
    @SerialName("time") val time: String,
    @SerialName("iKey") val insightsKey: String,
    val data: CustomBaseData
)




