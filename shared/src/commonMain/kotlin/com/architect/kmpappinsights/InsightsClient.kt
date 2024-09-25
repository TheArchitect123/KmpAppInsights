package com.architect.kmpappinsights

expect class InsightsClient {
    companion object {
        fun configureInsightsClient(instrumentationKey: String, allowPageTracking: Boolean = true)
        fun writeAvailability(message: EventTypeMap, eventName: String)
        fun writeDependency(message: EventTypeMap, eventName: String)
        fun writeCustomEvent(message: EventTypeMap, eventName: String)
        fun writePageView(message: EventTypeMap, eventName: String)
        fun writeRequest(message: EventTypeMap, eventName: String)
        fun writeTrace(message: EventTypeMap, eventName: String)
        fun writeException(ex: Exception, message: EventTypeMap)
    }
}

typealias EventTypeMap = HashMap<String, String>