package com.architect.kmpappinsights

expect class InsightsClient {
    companion object {
        fun configureInsightsClient(instrumentationKey: String, allowPageTracking: Boolean = true)
        fun writeInformational(message: EventTypeMap, eventName: String)
        fun writeTrace(message: EventTypeMap, eventName: String)
        fun writeException(ex: Exception, eventName: String)
        //fun writeException(message: EventTypeMap, ex: Exception, eventName: String)
    }
}

typealias EventTypeMap = Map<String, String>