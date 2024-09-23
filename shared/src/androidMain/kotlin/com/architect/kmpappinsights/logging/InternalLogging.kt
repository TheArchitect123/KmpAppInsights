package com.architect.kmpappinsights.logging

import android.util.Log
import com.architect.kmpappinsights.library.ApplicationInsights.Companion.isDeveloperMode
import com.architect.kmpappinsights.logging.InternalLogging

object InternalLogging {
    private val PREFIX: String = InternalLogging::class.java.getPackage().name

    /**
     * Inform SDK users about SDK activities. This has 3 parameters to avoid the string
     * concatenation when verbose mode is disabled.
     *
     * @param tag     the log context
     * @param message the log message
     * @param payload the payload for the message
     */
    fun info(tag: String, message: String, payload: String) {
        if (isDeveloperMode) {
            Log.i(PREFIX + " " + tag, "$message:$payload")
        }
    }

    /**
     * Inform SDK users about SDK activities.
     *
     * @param tag     the log context
     * @param message the log message
     */
    fun info(tag: String, message: String?) {
        if (isDeveloperMode) {
            Log.i(PREFIX + " " + tag, message!!)
        }
    }


    /**
     * Warn SDK users about non-critical SDK misuse
     *
     * @param tag     the log context
     * @param message the log message
     */
    fun warn(tag: String, message: String?) {
        if (isDeveloperMode) {
            Log.w(PREFIX + " " + tag, message!!)
        }
    }

    /**
     * Log critical SDK error
     *
     * @param tag     the log context
     * @param message the log message
     */
    fun error(tag: String, message: String?) {
        Log.e(PREFIX + " " + tag, message!!)
    }
}

