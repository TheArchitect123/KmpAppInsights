package com.architect.kmpappinsights.contracts

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ExceptionEvent(
    @SerialName("name") val name: String,
    @SerialName("time") val time: String,
    @SerialName("iKey") val insightsKey: String,
    val data: BaseEventExceptionCustomData
)


@Serializable
data class BaseEventExceptionCustomData(
    @SerialName("baseType") val type: String,
    @SerialName("baseData") val excData: ExceptionInfo,
)

@Serializable
data class ExceptionInfo(
    @SerialName("properties") val eventProperties: Map<String, String>? = null,
    @SerialName("ver") val version: Int,
    @SerialName("exceptions") val exception: List<ExceptionDetailsInfo>,
)


@Serializable
data class ExceptionDetailsInfo(
    @SerialName("id") val uniqueId: Int,
    @SerialName("message") val eventName: String,
    @SerialName("typeName") val type: String,
    @SerialName("hasFullStack") val hasStack: Boolean,
    @SerialName("parsedStack") val parsedStacks: List<ExceptionStackTraceDetailsInfo>,
)

@Serializable
data class ExceptionStackTraceDetailsInfo(
    val level: Int,
    val method: String,
    val assembly: String,
    val fileName: String,
    val line: Long
)