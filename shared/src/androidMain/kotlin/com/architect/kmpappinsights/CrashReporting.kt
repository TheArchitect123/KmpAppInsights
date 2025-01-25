package com.architect.kmpappinsights.services

import com.architect.kmpappinsights.InsightsClient
import java.lang.Exception

actual class CrashReporting {
    actual companion object {
        private val originalHandler = Thread.getDefaultUncaughtExceptionHandler()
        actual fun registerForCrashReporting() {
            Thread.setDefaultUncaughtExceptionHandler { thread, throwable ->
                InsightsClient.uploadAppCrashLog(Exception(throwable))
                originalHandler!!.uncaughtException(thread, throwable) // proceeds with crashing
            }
        }
    }
}