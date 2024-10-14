package com.architect.kmpappinsights.contracts

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
class RequestEvent(
    @SerialName("name") val name: String,
    @SerialName("time") val time: String,
    @SerialName("iKey") val insightsKey: String,
    val data: RequestCustomBaseData
)

@Serializable
class RequestStorageData(
    val requestUrl: String,
    val source: String,
    val responseCode: String,
    val durationMs : Long,
)


@Serializable
data class RequestCustomBaseData(
    @SerialName("baseType") val type: String,
    @SerialName("baseData") val customData: BaseRequestCustomData,
)

@Serializable
data class BaseRequestCustomData(
    val id: Int,
    @SerialName("properties") val eventProperties: Map<String, String>? = null,
    @SerialName("ver") val version: Int,
    @SerialName("name") val eventName: String,
    val source: String,
    val url: String,
    val responseCode: String,
    val duration: String,
)