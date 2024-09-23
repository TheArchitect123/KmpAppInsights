package com.architect.kmpappinsights.library

import android.app.Application
import com.architect.kmpappinsights.contracts.DataPoint.name
import com.architect.kmpappinsights.contracts.DataPoint.value
import com.architect.kmpappinsights.contracts.EventData.name
import com.architect.kmpappinsights.contracts.ExceptionDetails.message
import com.architect.kmpappinsights.contracts.MessageData.message
import com.architect.kmpappinsights.contracts.TelemetryData
import com.architect.kmpappinsights.library.ApplicationInsights
import com.architect.kmpappinsights.library.AutoCollection
import com.architect.kmpappinsights.library.SyncUtil
import com.architect.kmpappinsights.library.TelemetryContext
import com.architect.kmpappinsights.library.TrackDataOperation
import com.architect.kmpappinsights.library.config.Configuration
import com.architect.kmpappinsights.logging.InternalLogging
import java.lang.ref.WeakReference
import java.util.concurrent.ArrayBlockingQueue
import java.util.concurrent.RejectedExecutionHandler
import java.util.concurrent.ThreadFactory
import java.util.concurrent.ThreadPoolExecutor
import java.util.concurrent.TimeUnit
import kotlin.concurrent.Volatile

/**
 * The public API for recording application insights telemetry.
 */
class TelemetryClient protected constructor(
    /**
     * A flag, which determines telemetry data can be tracked.
     */
    private val telemetryEnabled: Boolean
) {
    /**
     * The configuration of the SDK.
     */
    private val config: Configuration? = null

    /**
     * Executor service for running track operations on several threads.
     */
    private var threadPoolExecutor: ThreadPoolExecutor? = null

    /**
     * A flag, which determines if auto page views should be disabled.
     * Default is true.
     */
    private var autoPageViewsDisabled = true

    /**
     * A flag, which determines if auto session management should be disabled.
     * Default is true.
     */
    private var autoSessionManagementDisabled = true

    /**
     * A flag, which determines if auto appearance should be disabled.
     * Default is true.
     */
    private var autoAppearanceDisabled = true

    /**
     * The application needed for auto collecting telemetry data
     */
    private var weakApplication: WeakReference<Application?>? = null

    /**
     * Restrict access to the default constructor
     *
     * @param telemetryEnabled YES if tracking telemetry data manually should be enabled
     */
    init {
        configThreadPool()
    }

    private fun configThreadPool() {
        val corePoolSize = 5
        val maxPoolSize = 10
        val queueSize = 5
        val timeout: Long = 1
        val queue = ArrayBlockingQueue<Runnable>(queueSize)
        val threadFactory = ThreadFactory { r ->
            val thread = Thread(r)
            thread.isDaemon = false
            thread
        }
        val handler = RejectedExecutionHandler { r, executor ->
            InternalLogging.error(
                TAG,
                "too many track() calls at a time"
            )
        }
        threadPoolExecutor = ThreadPoolExecutor(
            corePoolSize,
            maxPoolSize,
            timeout,
            TimeUnit.SECONDS,
            queue,
            threadFactory,
            handler
        )
    }

    /**
     * Sends information about any object that extend TelemetryData interface to Application Insights.
     * For most use-cases, the other tracking methods will be sufficient. Providing this generic method
     * for very specific uses.
     *
     * @param telemetry an object that extends TelemetryData
     */
    fun track(telemetry: TelemetryData?) {
        if (isTelemetryEnabled()) {
            threadPoolExecutor!!.execute(TrackDataOperation(telemetry))
        }
    }

    /**
     * Sends information about an event to Application Insights.
     *
     * @param eventName    The name of the event
     * @param properties   Custom properties associated with the event. Note: values set here will
     * supersede values set in [ApplicationInsights.setCommonProperties].
     * @param measurements Custom measurements associated with the event.
     */
    /**
     * `properties` defaults to `null`.
     * `measurements` defaults to `null`.
     *
     * @see TelemetryClient.trackEvent
     */
    /**
     * `measurements` defaults to `null`.
     *
     * @see TelemetryClient.trackEvent
     */
    @JvmOverloads
    fun trackEvent(
        eventName: String?,
        properties: Map<String, String>? = null,
        measurements: Map<String, Double>? = null
    ) {
        if (isTelemetryEnabled()) {
            threadPoolExecutor!!.execute(
                TrackDataOperation(
                    com.architect.kmpappinsights.library.TrackDataOperation.DataType.EVENT,
                    eventName, properties, measurements
                )
            )
        }
    }

    /**
     * Sends tracing information to Application Insights.
     *
     * @param message    The message associated with this trace.
     * @param properties Custom properties associated with the event. Note: values set here will
     * supersede values set in [ApplicationInsights.setCommonProperties].
     */
    /**
     * `measurements` defaults to `null`.
     *
     * @see TelemetryClient.trackTrace
     */
    @JvmOverloads
    fun trackTrace(message: String?, properties: Map<String, String>? = null) {
        if (isTelemetryEnabled()) {
            threadPoolExecutor!!.execute(
                TrackDataOperation(
                    com.architect.kmpappinsights.library.TrackDataOperation.DataType.TRACE,
                    message, properties, null
                )
            )
        }
    }

    /**
     * Sends information about an aggregated metric to Application Insights. Note: all data sent via
     * this method will be aggregated. To log non-aggregated data use
     * [TelemetryClient.trackEvent] with measurements.
     *
     * @param name  The name of the metric
     * @param value The value of the metric
     */
    /**
     * Sends information about an aggregated metric to Application Insights. Note: all data sent via
     * this method will be aggregated. To log non-aggregated data use
     * [TelemetryClient.trackEvent] with measurements.
     *
     * @param name  The name of the metric
     * @param value The value of the metric
     */
    @JvmOverloads
    fun trackMetric(name: String?, value: Double, properties: Map<String, String>? = null) {
        if (isTelemetryEnabled()) {
            threadPoolExecutor!!.execute(
                TrackDataOperation(
                    com.architect.kmpappinsights.library.TrackDataOperation.DataType.METRIC,
                    name,
                    value,
                    properties
                )
            )
        }
    }

    /**
     * @see TelemetryClient.trackPageView
     */
    /**
     * `properties` defaults to `null`.
     * `measurements` defaults to `null`.
     *
     * @see TelemetryClient.trackPageView
     */
    /**
     * `measurements` defaults to `null`.
     *
     * @see TelemetryClient.trackPageView
     */
    @JvmOverloads
    fun trackPageView(
        pageName: String?,
        properties: Map<String, String>? = null,
        measurements: Map<String, Double>? = null
    ) {
        if (isTelemetryEnabled()) {
            threadPoolExecutor!!.execute(
                TrackDataOperation(
                    com.architect.kmpappinsights.library.TrackDataOperation.DataType.PAGE_VIEW,
                    pageName, properties, measurements
                )
            )
        }
    }


    /**
     * Sends information about a new Session to Application Insights.
     */
    fun trackNewSession() {
        if (isTelemetryEnabled()) {
            threadPoolExecutor!!.execute(TrackDataOperation(com.architect.kmpappinsights.library.TrackDataOperation.DataType.NEW_SESSION))
        }
    }

    /**
     * Determines, whether tracking telemetry data is enabled or not.
     *
     * @return YES if telemetry data can be tracked
     */
    protected fun isTelemetryEnabled(): Boolean {
        if (!this.telemetryEnabled) {
            InternalLogging.warn(
                TAG, "Could not track telemetry item, because telemetry " +
                        "feature is disabled."
            )
        }
        return this.telemetryEnabled
    }

    /**
     * Enable auto page view tracking. This feature only works if
     * [TelemetryClient.initialize] has been called before.
     */
    fun enableAutoPageViewTracking() {
        synchronized(LOCK) {
            if (isAutoCollectionPossible("Auto PageViews") && this.autoPageViewsDisabled) {
                this.autoPageViewsDisabled = false
                AutoCollection.Companion.enableAutoPageViews(application)
            }
        }
    }

    /**
     * Disable auto page view tracking. This feature only works if
     * [TelemetryClient.initialize] has been called before.
     */
    fun disableAutoPageViewTracking() {
        synchronized(LOCK) {
            if (isAutoCollectionPossible("Auto PageViews") && !this.autoPageViewsDisabled) {
                this.autoPageViewsDisabled = true
                AutoCollection.Companion.disableAutoPageViews()
            }
        }
    }

    /**
     * Enable auto session management tracking. This feature only works if
     * [TelemetryClient.initialize] has been called before.
     */
    fun enableAutoSessionManagement() {
        synchronized(LOCK) {
            if (isAutoCollectionPossible("Session Management") && this.autoSessionManagementDisabled) {
                this.autoSessionManagementDisabled = false
                AutoCollection.Companion.enableAutoSessionManagement(application)
            }
        }
    }

    /**
     * Disable auto session management tracking. This feature only works if
     * [TelemetryClient.initialize] has been called before.
     */
    fun disableAutoSessionManagement() {
        synchronized(LOCK) {
            if (isAutoCollectionPossible("Session Management") && !this.autoSessionManagementDisabled) {
                this.autoSessionManagementDisabled = true
                AutoCollection.Companion.disableAutoSessionManagement()
            }
        }
    }

    /**
     * Enable auto appearance management tracking. This feature only works if
     * [TelemetryClient.initialize] has been called before.
     */
    fun enableAutoAppearanceTracking() {
        synchronized(LOCK) {
            if (isAutoCollectionPossible("Auto Appearance") && this.autoAppearanceDisabled) {
                this.autoAppearanceDisabled = false
                AutoCollection.Companion.enableAutoAppearanceTracking(application)
            }
        }
    }

    /**
     * Disable auto appearance management tracking. This feature only works if
     * [TelemetryClient.initialize] has been called before.
     */
    fun disableAutoAppearanceTracking() {
        synchronized(LOCK) {
            if (isAutoCollectionPossible("Auto Appearance") && !this.autoAppearanceDisabled) {
                this.autoAppearanceDisabled = true
                AutoCollection.Companion.disableAutoAppearanceTracking()
            }
        }
    }

    protected fun startSyncWhenBackgrounding() {
        if (!com.architect.kmpappinsights.library.Util.isLifecycleTrackingAvailable()) {
            return
        }

        val app = application
        if (app != null) {
            SyncUtil.Companion.getInstance()!!.start(app)
        } else {
            InternalLogging.warn(
                TAG, "Couldn't turn on SyncUtil because given application " +
                        "was null"
            )
        }
    }

    /**
     * Will check if autocollection is possible
     *
     * @param featureName The name of the feature which will be logged in case autocollection is not
     * possible
     * @return a flag indicating if autocollection features can be activated
     */
    private fun isAutoCollectionPossible(featureName: String): Boolean {
        if (!com.architect.kmpappinsights.library.Util.isLifecycleTrackingAvailable()) {
            InternalLogging.warn(
                TAG, "AutoCollection feature " + featureName +
                        " can't be enabled/disabled, because " +
                        "it is not supported on this OS version."
            )
            return false
        } else if (application == null) {
            InternalLogging.warn(
                TAG, "AutoCollection feature " + featureName +
                        " can't be enabled/disabled, because " +
                        "ApplicationInsights has not been setup with an application."
            )
            return false
        } else {
            return true
        }
    }

    private val application: Application?
        /**
         * Get the reference to the Application (used for life-cycle tracking)
         *
         * @return the reference to the application that was used during initialization of the SDK
         */
        get() {
            var application: Application? = null
            if (weakApplication != null) {
                application = weakApplication!!.get()
            }

            return application
        }

    companion object {
        private const val TAG = "TelemetryClient"

        /**
         * The shared TelemetryClient instance.
         */
        private var instance: TelemetryClient? = null

        /**
         * Volatile boolean for double checked synchronize block
         */
        @Volatile
        private var isTelemetryClientLoaded = false

        /**
         * Synchronization LOCK for setting static context
         */
        private val LOCK = Any()

        /**
         * Initialize the INSTANCE of the telemetryclient
         *
         * @param telemetryEnabled YES if tracking telemetry data manually should be enabled
         * @param application      application used for auto collection features
         */
        fun initialize(telemetryEnabled: Boolean, application: Application?) {
            if (!isTelemetryClientLoaded) {
                synchronized(LOCK) {
                    if (!isTelemetryClientLoaded) {
                        isTelemetryClientLoaded = true
                        instance = TelemetryClient(telemetryEnabled)
                        instance!!.weakApplication = WeakReference(application)
                    }
                }
            }
        }

        /**
         * Start auto collection features.
         */
        fun startAutoCollection(
            context: TelemetryContext?,
            config: Configuration,
            autoAppearanceEnabled: Boolean,
            autoPageViewsEnabled: Boolean,
            autoSessionManagementEnabled: Boolean
        ) {
            AutoCollection.Companion.initialize(context, config)

            if (autoAppearanceEnabled) {
                instance!!.enableAutoAppearanceTracking()
            }
            if (autoSessionManagementEnabled) {
                instance!!.enableAutoSessionManagement()
            }
            if (autoPageViewsEnabled) {
                instance!!.enableAutoPageViewTracking()
            }

            getInstance()!!.startSyncWhenBackgrounding()
        }

        /**
         * @return the INSTANCE of persistence or null if not yet initialized
         */
        fun getInstance(): TelemetryClient? {
            if (instance == null) {
                InternalLogging.error(TAG, "getSharedInstance was called before initialization")
            }
            return instance
        }
    }
}
