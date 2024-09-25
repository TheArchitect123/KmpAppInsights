package com.architect.kmpappinsights

import android.app.Application
import com.architect.androidjavaruntime.library.ApplicationInsights
import com.architect.androidjavaruntime.library.TelemetryClient

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

        actual fun writeAvailability(message: EventTypeMap, eventName: String) {
            telemetryClient!!.trackAvailabilityView(eventName, message)
        }

        actual fun writeDependency(message: EventTypeMap, eventName: String) {
            telemetryClient!!.trackDependencyView(eventName, message)
        }

        actual fun writeCustomEvent(message: EventTypeMap, eventName: String) {
            telemetryClient!!.trackEvent(eventName, message)
        }

        actual fun writePageView(message: EventTypeMap, eventName: String) {
            telemetryClient!!.trackPageView(eventName, message)
        }

        actual fun writeRequest(message: EventTypeMap, eventName: String) {
            telemetryClient!!.trackRequestView(eventName, message)
        }

        actual fun writeTrace(message: EventTypeMap, eventName: String) {
            telemetryClient!!.trackTrace(eventName, message)
        }

        actual fun writeException(ex: Exception, message: EventTypeMap) {
            telemetryClient!!.trackHandledException(ex, message)
        }
    }
}