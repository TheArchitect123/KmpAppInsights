package com.architect.kmpappinsights

import android.app.Application
import com.architect.kmpappinsights.library.ApplicationInsights
import com.architect.kmpappinsights.library.TelemetryClient

actual class InsightsClient {
    actual companion object {
        private val telemetryClient by lazy {
            TelemetryClient.getInstance()
        }
        private lateinit var appContext: Application
        fun setupAppContext(appContext: Application) {
            this.appContext = appContext
        }

        actual fun configureInsightsClient(instrumentationKey: String, allowPageTracking: Boolean) {
            ApplicationInsights.setup(appContext, appContext, instrumentationKey)
            if (allowPageTracking) {
                ApplicationInsights.enableAutoPageViewTracking()
            }

            ApplicationInsights.start()
        }

        actual fun writeInformational(message: EventTypeMap, eventName: String) {
            telemetryClient!!.trackEvent(eventName, message)
        }

        actual fun writeTrace(message: EventTypeMap, eventName: String) {
            telemetryClient!!.trackTrace(eventName, message)
        }

        actual fun writeException(ex: Exception, eventName: String) {
            //telemetryClient!!.trackEvent(eventName, message)
        }

//        actual fun writeException(message: String, ex: Exception) {
//
//        }
    }
}