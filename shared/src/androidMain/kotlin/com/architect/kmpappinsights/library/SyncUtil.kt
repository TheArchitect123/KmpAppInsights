package com.architect.kmpappinsights.library

import android.app.Application
import android.content.ComponentCallbacks2
import android.content.res.Configuration
import com.architect.kmpappinsights.logging.InternalLogging

/**
 * Class that triggers a sync call to the pipeline by using ComponentCallbacks2
 */

internal class SyncUtil private constructor() : ComponentCallbacks2 {
    fun start(application: Application?) {
        if (application != null) {
            application.registerComponentCallbacks(instance)
            InternalLogging.info(TAG, "Started listening to componentcallbacks to trigger sync")
        }
    }

    override fun onTrimMemory(level: Int) {
        if (com.architect.kmpappinsights.library.Util.isLifecycleTrackingAvailable) {
            if (level == ComponentCallbacks2.TRIM_MEMORY_UI_HIDDEN) {
                InternalLogging.info(TAG, "UI of the app is hidden")
                InternalLogging.info(TAG, "Syncing data")
                com.architect.kmpappinsights.library.Channel.Companion.getInstance()!!
                    .synchronize()
            } else if (level == ComponentCallbacks2.TRIM_MEMORY_RUNNING_LOW) {
                InternalLogging.info(TAG, "Memory running low, syncing data")
                com.architect.kmpappinsights.library.Channel.Companion.getInstance()!!
                    .synchronize()
            }
        }
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        // unused but required to implement ComponentCallbacks
    }

    override fun onLowMemory() {
        // unused but required to implement ComponentCallbacks
        InternalLogging.warn(TAG, "Received onLowMemory()-Callback, persisting data")
        com.architect.kmpappinsights.library.Channel.Companion.getInstance()!!.synchronize()
    }

    companion object {
        /**
         * The singleton INSTANCE of this class
         */
        private var instance: SyncUtil? = null

        /**
         * The tag for logging
         */
        private const val TAG = "SyncUtil"

        /**
         * @return the INSTANCE of autocollection event tracking or null if not yet initialized
         */
        fun getInstance(): SyncUtil? {
            if (instance == null) {
                instance = SyncUtil()
            }

            return instance
        }
    }
}
