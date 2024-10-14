package com.architect.kmpappinsights.contracts

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
class PageEvent(
    @SerialName("name") val name: String,
    @SerialName("time") val time: String,
    @SerialName("iKey") val insightsKey: String,
    val data: BasePageEventCustomData
)


@Serializable
data class BasePageEventSubCustomData(
    @SerialName("properties") val eventProperties: Map<String, String>? = null,
    @SerialName("ver") val version: String,
    @SerialName("name") val eventName: String,
    @SerialName("SessionId") val sessionId: String,
    val url: String,

    )

@Serializable
data class BasePageEventCustomData(
    @SerialName("baseType") val type: String,
    @SerialName("baseData") val customData: BasePageEventSubCustomData,
)



