package com.architect.kmpappinsights.library

import android.app.Activity
import android.app.Application
import android.app.Application.ActivityLifecycleCallbacks
import android.content.ComponentCallbacks2
import android.content.res.Configuration
import android.os.Bundle
import com.architect.kmpappinsights.library.config.ISessionConfig
import com.architect.kmpappinsights.logging.InternalLogging
import java.util.Date
import java.util.concurrent.atomic.AtomicInteger
import java.util.concurrent.atomic.AtomicLong
import kotlin.concurrent.Volatile

internal class AutoCollection protected constructor(
    /**
     * The configuration for tracking sessions
     */
    protected var config: ISessionConfig, telemetryContext: TelemetryContext?
) : ActivityLifecycleCallbacks, ComponentCallbacks2 {
    /**
     * The activity counter
     */
    protected val activityCount: AtomicInteger = AtomicInteger(0)

    /**
     * The timestamp of the last activity
     */
    protected val lastBackground: AtomicLong

    /**
     * The telemetryContext which is needed to renew a session
     */
    protected var telemetryContext: TelemetryContext?

    /**
     * Create a new INSTANCE of the autocollection event tracking
     *
     * @param config           the session configuration for session tracking
     * @param telemetryContext the context, which is needed to renew sessions
     */
    init {
        this.lastBackground = AtomicLong(
            time
        )
        this.telemetryContext = telemetryContext
    }

    /**
     * This is called each time an activity is created.
     *
     * @param activity           the Android Activity that's created
     * @param savedInstanceState the bundle
     */
    override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
        // unused but required to implement ActivityLifecycleCallbacks
        //NOTE:
        //We first implemented Session management here. This callback doesn't work for the starting
        //activity of the app because the SDK will be setup and initialized in the onCreate, so
        //we don't get the very first call to an app activity's onCreate.
        //This is why the logic was moved to onActivityResumed below
    }

    /**
     * This is called each time an activity becomes visible
     *
     * @param activity the activity which entered the foreground
     */
    override fun onActivityStarted(activity: Activity) {
        // unused but required to implement ActivityLifecycleCallbacks
    }

    /**
     * This is called each time an activity has been started or was resumed after pause
     *
     * @param activity the activity which left the foreground
     */
    override fun onActivityResumed(activity: Activity) {
        synchronized(LOCK) {
            sessionManagement()
            sendPagewView(activity)
        }
    }

    private fun sendPagewView(activity: Activity) {
        if (isAutoPageViewsEnabled) {
            InternalLogging.info(TAG, "New Pageview")
            val pageViewOp =
                TrackDataOperation(TrackDataOperation.DataType.PAGE_VIEW, activity.javaClass.name)
            Thread(pageViewOp).start()
        }
    }

    private fun sessionManagement() {
        val count = activityCount.getAndIncrement()
        if (count == 0) {
            if (isAutoSessionManagementEnabled) {
                InternalLogging.info(TAG, "Starting & tracking session")
                val sessionOp: TrackDataOperation =
                    TrackDataOperation(TrackDataOperation.DataType.NEW_SESSION)
                Thread(sessionOp).start()
            } else {
                InternalLogging.info(TAG, "Session management disabled by the developer")
            }
            if (isAutoAppearanceTrackingEnabled) {
                //TODO track cold start as soon as it's available in new Schema.
            }
        } else {
            //we should already have a session now
            //check if the session should be renewed
            val now = this.time
            val then = lastBackground.getAndSet(this.time)
            val shouldRenew = ((now - then) >= config.sessionIntervalMs)
            InternalLogging.info(
                TAG,
                "Checking if we have to renew a session, time difference is: " + (now - then)
            )

            if (isAutoSessionManagementEnabled && shouldRenew) {
                InternalLogging.info(TAG, "Renewing session")
                telemetryContext!!.renewSessionId()
                val sessionOp: TrackDataOperation =
                    TrackDataOperation(TrackDataOperation.DataType.NEW_SESSION)
                Thread(sessionOp).start()
            }
        }
    }

    /**
     * This is called each time an activity leaves the foreground
     *
     * @param activity the activity which was paused
     */
    override fun onActivityPaused(activity: Activity) {
        //set backgrounding in onTrimMemory
    }

    override fun onActivityStopped(activity: Activity) {
        // unused but required to implement ActivityLifecycleCallbacks
    }

    override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {
        // unused but required to implement ActivityLifecycleCallbacks
    }

    override fun onActivityDestroyed(activity: Activity) {
        // unused but required to implement ActivityLifecycleCallbacks
    }

    override fun onTrimMemory(level: Int) {
        if (level == ComponentCallbacks2.TRIM_MEMORY_UI_HIDDEN) {
            InternalLogging.info(TAG, "UI of the app is hidden")
            InternalLogging.info(TAG, "Setting background time")
            lastBackground.set(this.time)
        }
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        when (newConfig.orientation) {
            Configuration.ORIENTATION_PORTRAIT -> InternalLogging.info(
                TAG,
                "Device Orientation is portrait"
            )

            Configuration.ORIENTATION_LANDSCAPE -> InternalLogging.info(
                TAG,
                "Device Orientation is landscape"
            )

            Configuration.ORIENTATION_UNDEFINED -> InternalLogging.info(
                TAG,
                "Device Orientation is undefinded"
            )

            else -> {}
        }
    }

    override fun onLowMemory() {
        // unused but required to implement ComponentCallbacks
    }

    protected val time: Long
        /**
         * Test hook to get the current time
         *
         * @return the current time in milliseconds
         */
        get() = Date().time

    companion object {
        /**
         * Volatile boolean for double checked synchronize block
         */
        @Volatile
        private var isLoaded = false

        /**
         * Synchronization LOCK for setting static context
         */
        private val LOCK = Any()

        /**
         * The singleton INSTANCE of this class
         */
        private var instance: AutoCollection? = null

        /**
         * The tag for logging
         */
        private const val TAG = "AutoCollection"

        /**
         * A flag which determines whether auto page view tracking has been enabled or not.
         */
        protected var isAutoPageViewsEnabled: Boolean = false
            private set

        /**
         * A flag which determines whether session management has been enabled or not.
         */
        protected var isAutoSessionManagementEnabled: Boolean = false
            private set

        /**
         * A flag that determines whether we want to auto-track events for foregrounding backgrounding
         */
        protected var isAutoAppearanceTrackingEnabled: Boolean = false
            private set

        /**
         * A flag that indicates if componentcallbacks have been registered
         */
        protected var isHasRegisteredComponentCallbacks: Boolean = false
            private set

        /**
         * A flag that indicates if lifecyclecallbacks have been already registered
         */
        protected var isHasRegisteredLifecycleCallbacks: Boolean = false
            private set

        /**
         * Initialize the INSTANCE of Autocollection event tracking.
         *
         * @param telemetryContext the context, which is needed to renew sessions
         * @param config           the session configuration for session tracking
         */
        fun initialize(telemetryContext: TelemetryContext?, config: ISessionConfig) {
            // note: isLoaded must be volatile for the double-checked LOCK to work
            if (!isLoaded) {
                synchronized(LOCK) {
                    if (!isLoaded) {
                        isLoaded = true
                        isHasRegisteredComponentCallbacks = false
                        isHasRegisteredLifecycleCallbacks = false
                        instance = AutoCollection(config, telemetryContext)
                    }
                }
            }
        }

        /**
         * @return the INSTANCE of autocollection event tracking or null if not yet initialized
         */
        protected fun getInstance(): AutoCollection? {
            if (instance == null) {
                InternalLogging.error(TAG, "getSharedInstance was called before initialization")
            }

            return instance
        }

        /**
         * Enables lifecycle event tracking for the provided application
         *
         * @param application the application object
         */

        private fun registerActivityLifecycleCallbacks(application: Application?) {
            if (!isHasRegisteredLifecycleCallbacks) {
                if ((application != null) && Util.isLifecycleTrackingAvailable) {
                    application.registerActivityLifecycleCallbacks(getInstance())
                    isHasRegisteredLifecycleCallbacks = true
                    InternalLogging.info(TAG, "Registered activity lifecycle callbacks")
                }
            }
        }

        /**
         * Register for component callbacks to enable persisting when backgrounding on devices with API-level 14+
         * and persisting when receiving onMemoryLow() on devices with API-level 1+
         *
         * @param application the application object
         */
        private fun registerForComponentCallbacks(application: Application?) {
            if (!isHasRegisteredComponentCallbacks) {
                if ((application != null) && Util.isLifecycleTrackingAvailable) {
                    application.registerComponentCallbacks(getInstance())
                    isHasRegisteredComponentCallbacks = true
                    InternalLogging.info(TAG, "Registered component callbacks")
                }
            }
        }

        /**
         * Enables page view event tracking for the provided application
         *
         * @param application the application object
         */
        
        fun enableAutoPageViews(application: Application?) {
            if (application != null && Util.isLifecycleTrackingAvailable) {
                synchronized(LOCK) {
                    registerActivityLifecycleCallbacks(application)
                    isAutoPageViewsEnabled = true
                }
            }
        }

        /**
         * Disables page view event tracking for the provided application*
         */
        
        fun disableAutoPageViews() {
            if (Util.isLifecycleTrackingAvailable) {
                synchronized(LOCK) {
                    isAutoPageViewsEnabled = false
                }
            }
        }

        /**
         * Enables session event tracking for the provided application
         *
         * @param application the application object
         */
        
        fun enableAutoSessionManagement(application: Application?) {
            if (application != null && Util.isLifecycleTrackingAvailable) {
                synchronized(LOCK) {
                    registerForComponentCallbacks(application)
                    registerActivityLifecycleCallbacks(application)
                    isAutoSessionManagementEnabled = true
                }
            }
        }

        /**
         * Disables session event tracking for the provided application
         */
        
        fun disableAutoSessionManagement() {
            if (Util.isLifecycleTrackingAvailable) {
                synchronized(LOCK) {
                    isAutoSessionManagementEnabled = false
                }
            }
        }

        /**
         * Enables auto appearance event tracking for the provided application
         *
         * @param application the application object
         */
        
        fun enableAutoAppearanceTracking(application: Application?) {
            if (application != null && Util.isLifecycleTrackingAvailable) {
                synchronized(LOCK) {
                    registerForComponentCallbacks(application)
                    registerActivityLifecycleCallbacks(application)
                    isAutoAppearanceTrackingEnabled = true
                }
            }
        }


        /**
         * Disables auto appearance event tracking for the provided application
         */
        
        fun disableAutoAppearanceTracking() {
            if (Util.isLifecycleTrackingAvailable) {
                synchronized(LOCK) {
                    isAutoAppearanceTrackingEnabled = false
                }
            }
        }
    }
}
