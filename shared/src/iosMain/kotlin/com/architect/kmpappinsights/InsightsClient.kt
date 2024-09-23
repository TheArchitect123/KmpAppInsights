package com.architect.kmpappinsights

actual class InsightsClient {
    actual companion object {

        actual fun configureInsightsClient(instrumentationKey: String, allowPageTracking: Boolean) {

        }

        actual fun writeInformational(message: EventTypeMap, eventName: String) {

        }

        actual fun writeTrace(message: EventTypeMap, eventName: String) {

        }

        actual fun writeException(ex: Exception, eventName: String) {
            //telemetryClient!!.trackEvent(eventName, message)
        }

//        actual fun writeException(message: String, ex: Exception) {
//
//        }
    }
}