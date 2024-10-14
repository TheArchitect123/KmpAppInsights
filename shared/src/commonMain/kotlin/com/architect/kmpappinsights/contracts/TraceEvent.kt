package com.architect.kmpappinsights.contracts

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class TraceEvent(
    @SerialName("name") val name: String,
    @SerialName("time") val time: String,
    @SerialName("iKey") val insightsKey: String,
    val data: CustomTraceBaseData
)


// Trace Events
@Serializable
data class CustomTraceBaseData(
    @SerialName("baseType") val type: String,
    @SerialName("baseData") val customData : BaseEventTraceCustomData,
)

@Serializable
data class BaseEventTraceCustomData(
    @SerialName("properties") val eventProperties: Map<String, String>? = null,
    @SerialName("ver") val version: String,
    @SerialName("message") val message: String,
    @SerialName("severityLevel") val level: Int,
)

enum class TraceSeverityLevel(level: Int) {
    Verbose(0),
    Information(1),
    Warning(2),
    Error(3),
    Critical(4)
}