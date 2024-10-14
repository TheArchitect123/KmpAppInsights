package com.architect.kmpappinsights.contracts

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CustomBaseData(
    @SerialName("baseType") val type: String,
    @SerialName("baseData") val customData : BaseEventCustomData,
)

@Serializable
data class BaseEventCustomData(
    @SerialName("properties") val eventProperties: Map<String, String>? = null,
    @SerialName("ver") val version: String,
    @SerialName("name") val eventName: String,
)