package com.architect.kmpappinsights

actual class InsightsClient {
    actual companion object {
        actual fun configureInsightsClient(instrumentationKey: String, allowPageTracking: Boolean) {

        }

        actual fun writeAvailability(message: EventTypeMap, eventName: String) {
        }

        actual fun writeDependency(message: EventTypeMap, eventName: String) {
        }

        actual fun writeCustomEvent(message: EventTypeMap, eventName: String) {
        }

        actual fun writePageView(message: EventTypeMap, eventName: String) {
        }

        actual fun writeRequest(message: EventTypeMap, eventName: String) {
        }

        actual fun writeTrace(message: EventTypeMap, eventName: String) {
        }

        actual fun writeException(ex: Exception, message: EventTypeMap) {
        }
    }
}