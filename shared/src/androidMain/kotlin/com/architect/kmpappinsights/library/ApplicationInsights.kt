package com.architect.kmpappinsights.library

import android.app.Application
import android.content.Context
import android.content.pm.PackageManager
import android.util.Log
import com.architect.kmpappinsights.contracts.User
import com.architect.kmpappinsights.library.config.Configuration
import com.architect.kmpappinsights.logging.InternalLogging
import java.lang.ref.WeakReference
import java.util.Collections
import java.util.concurrent.atomic.AtomicBoolean

enum class ApplicationInsights {
    INSTANCE;

    /**
     * The configuration of the SDK.
     */
    private val configuration: Configuration = Configuration()

    /**
     * A flag, which determines if sending telemetry data should be disabled. Default is false.
     */
    private var telemetryDisabled = false

    /**
     * A flag, which determines if auto page views should be disabled from the start.
     * Default is false.
     */
    private var autoPageViewsDisabled = false

    /**
     * A flag, which determines if auto session management should be disabled from the start.
     * Default is false.
     */
    private var autoSessionManagementDisabled = false

    /**
     * A flag, which determines if auto appearance should be disabled from the start.
     * Default is false.
     */
    private var autoAppearanceDisabled = false

    /**
     * The instrumentation key associated with the app.
     */
    private var instrumentationKey: String? = null

    /**
     * The weakContext which contains additional information for the telemetry data sent out.
     */
    private var telemetryContext: TelemetryContext? = null

    /**
     * A custom user object for sending telemetry data. Replaces
     * userId as we allow more configuration of the user object
     */
    private var user: User? = null

    /**
     * The weakContext associated with Application Insights.
     */
    private var weakContext: WeakReference<Context>? = null

    /**
     * The application needed for auto collecting telemetry data
     */
    private var weakApplication: WeakReference<Application>? = null

    /**
     * Properties associated with this telemetryContext.
     */
    private var commonProperties: Map<String, String> = Collections.synchronizedMap(HashMap())

    /**
     * The type of channel to use for logging
     */
    private var channelType: ChannelType

    /**
     * Create ApplicationInsights instance
     */
    init {
        this.channelType = ChannelType.Default
    }

    /**
     * Configure Application Insights
     * Note: This should be called before start
     *
     * @param context            the application context associated with Application Insights
     * @param instrumentationKey the instrumentation key associated with the app
     */
    private fun setupInstance(
        context: Context?,
        application: Application,
        instrumentationKey: String?
    ) {
        if (!isConfigured) {
            if (context != null) {
                this.weakContext = WeakReference(context)
                this.weakApplication = WeakReference(application)
                isConfigured = true
                this.instrumentationKey = instrumentationKey

                if (this.instrumentationKey == null) {
                    this.instrumentationKey = readInstrumentationKey(context)
                }

                if (this.user == null) {
                    //in case the dev uses deprecated method to set the user's ID
                    this.user = User()
                }
                TelemetryContext.initialize(context, this.instrumentationKey, this.user)
                this.telemetryContext = TelemetryContext.sharedInstance
                InternalLogging.info(TAG, "ApplicationInsights has been setup correctly.", "")
            } else {
                InternalLogging.warn(
                    TAG, "ApplicationInsights could not be setup correctly " +
                            "because the given weakContext was null"
                )
            }
        }
    }

    /**
     * Start ApplicationInsights
     * Note: This should be called after [.isConfigured]
     */
    private fun startInstance() {
        if (!isConfigured) {
            InternalLogging.warn(
                TAG, "Could not start Application Insights since it has not been " +
                        "setup correctly."
            )
            return
        }
        if (!isSetupAndRunning) {
            val context = weakContext!!.get()

            initializePipeline(context)

            Sender.getInstance()!!.triggerSending()
            InternalLogging.info(TAG, "ApplicationInsights has been started.", "")
            isSetupAndRunning = true
        }
    }

    /**
     * Makes sure Persistence, Sender, ChannelManager, TelemetryClient and AutoCollection are initialized
     * Call this before starting AutoCollection
     *
     * @param context application context
     */
    private fun initializePipeline(context: Context?) {
        EnvelopeFactory.initialize(telemetryContext, this.commonProperties)

        Persistence.initialize(context)
        Sender.initialize(this.configuration)
        ChannelManager.initialize(channelType)

        // Initialize Telemetry
        var application: Application? = null
        if (this.weakApplication != null) {
            application = weakApplication!!.get()
        }
        TelemetryClient.initialize(!this.telemetryDisabled, application)
        TelemetryClient.startAutoCollection(
            this.telemetryContext,
            this.configuration,
            !this.autoAppearanceDisabled,
            !this.autoPageViewsDisabled,
            !this.autoSessionManagementDisabled
        )
    }

    /**
     * Reads the instrumentation key from AndroidManifest.xml if it is available
     *
     * @param context the application weakContext to check the manifest from
     * @return the instrumentation key configured for the application
     */
    private fun readInstrumentationKey(context: Context?): String? {
        var iKey = ""
        if (context != null) {
            try {
                val bundle = context
                    .packageManager
                    .getApplicationInfo(context.packageName, PackageManager.GET_META_DATA)
                    .metaData
                if (bundle != null) {
                    iKey = bundle.getString("com.architect.kmpappinsights.instrumentationKey", "")
                } else {
                    logInstrumentationInstructions()
                }
            } catch (exception: PackageManager.NameNotFoundException) {
                logInstrumentationInstructions()
                Log.v(TAG, exception.toString())
            }
        }

        return iKey
    }

    val context: Context?
        /**
         * Returns the application reference that Application Insights needs.
         *
         * @return the Context that's used by the Application Insights SDK
         */
        get() {
            var context: Context? = null
            if (this.weakContext != null) {
                context = weakContext!!.get()
            }

            return context
        }

    companion object {
        /**
         * The tag for logging.
         */
        private const val TAG = "ApplicationInsights"

        /**
         * A flag which determines, if developer mode (logging) should be enabled.
         */
        private val DEVELOPER_MODE = AtomicBoolean(Util.isEmulator || Util.isDebuggerAttached)

        /**
         * Flag that indicates that the user has called a setup-method before
         */
        private var isConfigured = false

        /**
         * Flag that indicates that the pipeline (Channel, Persistence, etc.) have been setup
         */
        private var isSetupAndRunning = false

        /**
         * Configure Application Insights
         * Note: This should be called before start
         *
         * @param application the application context the application needed for auto collecting telemetry data
         */
        fun setup(context: Context?, application: Application) {
            INSTANCE.setupInstance(context, application, null)
        }

        /**
         * Configure Application Insights
         * Note: This should be called before start
         *
         * @param context            the application context associated with Application Insights
         * @param application        the application needed for auto collecting telemetry data
         * @param instrumentationKey the instrumentation key associated with the app
         */
        fun setup(context: Context?, application: Application, instrumentationKey: String?) {
            INSTANCE.setupInstance(context, application, instrumentationKey)
        }

        /**
         * Start ApplicationInsights
         * Note: This should be called after [.isConfigured]
         */
        fun start() {
            INSTANCE.startInstance()
        }

        /**
         * Triggers persisting and if applicable sending of queued data
         * note: this will be called
         * [Configuration.maxBatchIntervalMs] after
         * tracking any telemetry so it is not necessary to call this in most cases.
         */
        fun sendPendingData() {
            if (!isSetupAndRunning) {
                InternalLogging.warn(
                    TAG, "Could not set send pending data, because " +
                            "ApplicationInsights has not been started, yet."
                )
                return
            }
            ChannelManager.getInstance()!!.getChannel()!!.synchronize()
        }

        /**
         * Enables all auto-collection features. Call this before
         * [ApplicationInsights.start] or when ApplicationInsights is already running to change
         * AutoCollection settings at runtime.
         * Requires ApplicationInsights to be setup with an Application object
         */
        fun enableAutoCollection() {
            enableAutoAppearanceTracking()
            enableAutoPageViewTracking()
            enableAutoSessionManagement()
        }

        /**
         * disables all auto-collection features
         */
        fun disableAutoCollection() {
            disableAutoAppearanceTracking()
            disableAutoPageViewTracking()
            disableAutoSessionManagement()
        }

        /**
         * Enable auto page view tracking before calling [ApplicationInsights.start] or
         * at runtime. This feature only works if ApplicationInsights has been setup
         * with an application.
         */
        fun enableAutoPageViewTracking() {
            if (isSetupAndRunning) {
                TelemetryClient.getInstance()!!.enableAutoPageViewTracking()
            } else {
                INSTANCE.autoPageViewsDisabled = false
            }
        }

        /**
         * Disable auto page view tracking before calling [ApplicationInsights.start] or
         * at runtime. This feature only works if ApplicationInsights has been setup
         * with an application.
         */
        fun disableAutoPageViewTracking() {
            if (isSetupAndRunning) {
                TelemetryClient.getInstance()!!.disableAutoPageViewTracking()
            } else {
                INSTANCE.autoPageViewsDisabled = true
            }
        }

        /**
         * Enable auto session management tracking before calling [ApplicationInsights.start] or
         * at runtime. This feature only works if ApplicationInsights has been setup
         * with an application.
         */
        fun enableAutoSessionManagement() {
            if (isSetupAndRunning) {
                TelemetryClient.getInstance()!!.enableAutoSessionManagement()
            } else {
                INSTANCE.autoSessionManagementDisabled = false
            }
        }

        /**
         * Disable auto session management tracking before calling [ApplicationInsights.start] or
         * at runtime. This feature only works if ApplicationInsights has been setup
         * with an application.
         */
        fun disableAutoSessionManagement() {
            if (isSetupAndRunning) {
                TelemetryClient.getInstance()!!.disableAutoSessionManagement()
            } else {
                INSTANCE.autoSessionManagementDisabled = true
            }
        }

        /**
         * Enable auto appearance management tracking before calling [ApplicationInsights.start] or
         * at runtime. This feature only works if ApplicationInsights has been setup
         * with an application.
         */
        fun enableAutoAppearanceTracking() {
            if (isSetupAndRunning) {
                TelemetryClient.getInstance()!!.enableAutoAppearanceTracking()
            } else {
                INSTANCE.autoAppearanceDisabled = false
            }
        }

        /**
         * Disable auto appearance management tracking before calling [ApplicationInsights.start] or
         * at runtime. This feature only works if ApplicationInsights has been setup
         * with an application.
         */
        fun disableAutoAppearanceTracking() {
            if (isSetupAndRunning) {
                TelemetryClient.getInstance()!!.disableAutoAppearanceTracking()
            } else {
                INSTANCE.autoAppearanceDisabled = true
            }
        }

        /**
         * Enable / disable tracking of telemetry data.
         *
         * @param disabled if set to true, the telemetry feature will be disabled
         */
        fun setTelemetryDisabled(disabled: Boolean) {
            if (!isConfigured) {
                InternalLogging.warn(
                    TAG, "Could not enable/disable telemetry, because " +
                            "ApplicationInsights has not been setup correctly."
                )
                return
            }
            if (isSetupAndRunning) {
                InternalLogging.warn(
                    TAG, "Could not enable/disable telemetry, because " +
                            "ApplicationInsights has already been started."
                )
                return
            }
            INSTANCE.telemetryDisabled = disabled
        }

        /**
         * Gets the properties which are common to all telemetry sent from this client.
         *
         * @return common properties for this telemetry client
         */
        fun getCommonProperties(): Map<String, String> {
            return INSTANCE.commonProperties
        }

        /**
         * Sets properties which are common to all telemetry sent form this client.
         *
         * @param commonProperties a dictionary of properties to log with all telemetry.
         */
        fun setCommonProperties(commonProperties: Map<String, String>) {
            if (!isConfigured) {
                InternalLogging.warn(
                    TAG, "Could not set common properties, because " +
                            "ApplicationInsights has not been setup correctly."
                )
                return
            }
            if (isSetupAndRunning) {
                InternalLogging.warn(
                    TAG, "Could not set common properties, because " +
                            "ApplicationInsights has already been started."
                )
                return
            }
            INSTANCE.commonProperties = commonProperties
        }

        @JvmStatic
        var isDeveloperMode: Boolean
            /**
             * Check if developerMode is activated
             * @return flag indicating activated developer mode
             */
            get() = DEVELOPER_MODE.get()
            /**
             * Activates the developer mode which. It enables extensive logging as well as use different
             * settings for batching. Batch Size in debug mode is 5 and sending interval is 3s.
             * @param developerMode if true, developer mode will be activated
             */
            set(developerMode) {
                DEVELOPER_MODE.set(developerMode)
            }

        /* Writes instructions on how to configure the instrumentation key.
        */
        private fun logInstrumentationInstructions() {
            val instructions = """
                 No instrumentation key found.
                 Set the instrumentation key in AndroidManifest.xml
                 """.trimIndent()
            val manifestSnippet = """<meta-data
android:name="com.architect.kmpappinsights.instrumentationKey"
android:value="${"$"}{AI_INSTRUMENTATION_KEY}" />"""
            InternalLogging.error(
                "MissingInstrumentationkey", """
     $instructions
     $manifestSnippet
     """.trimIndent()
            )
        }

        /**
         * Gets the configuration for the ApplicationInsights instance
         *
         * @return the instance ApplicationInsights configuration
         */
        fun getTelemetryContext(): TelemetryContext? {
            if (!isConfigured) {
                InternalLogging.warn(
                    TAG, "Global telemetry context has not been set up, yet. " +
                            "You need to call setup() first."
                )
                return null
            }
            return INSTANCE.telemetryContext
        }

        /**
         * Force Application Insights to create a new session with a custom sessionID.
         *
         * @param sessionId a custom session ID used of the session to create
         */
        fun renewSession(sessionId: String?) {
            if (!INSTANCE.telemetryDisabled && INSTANCE.telemetryContext != null) {
                val telemetry = INSTANCE.telemetryContext!!
                telemetry.renewSessionId(sessionId)
            }
        }

        /**
         * Sets the channel type to be used for logging
         *
         * @param channelType The channel type to use
         */
        fun setChannelType(channelType: ChannelType) {
            if (isSetupAndRunning) {
                InternalLogging.warn(
                    TAG, "Cannot set channel type, because " +
                            "ApplicationInsights has already been started."
                )
                return
            }

            INSTANCE.channelType = channelType
        }

        /**
         * Gets the currently used channel type
         *
         * @return The current channel type.
         */
        fun getChannelType(): ChannelType {
            return INSTANCE.channelType
        }


        /**
         * Get the instrumentation key associated with this app.
         *
         * @return the Application Insights instrumentation key set for this app
         */
        fun getInstrumentationKey(): String? {
            return INSTANCE.instrumentationKey
        }
    }
}
