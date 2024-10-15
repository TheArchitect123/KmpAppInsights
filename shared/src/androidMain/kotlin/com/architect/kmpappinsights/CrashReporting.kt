package com.architect.kmpappinsights.services

import com.architect.kmpappinsights.InsightsClient
import java.lang.Exception

actual class CrashReporting{
    actual companion object {
        actual fun registerForCrashReporting() {
            val originalHandler = Thread.getDefaultUncaughtExceptionHandler()
            Thread.setDefaultUncaughtExceptionHandler { thread, throwable ->
                // Log the exception to file or remote service
                InsightsClient.writeException(
                    Exception(throwable.message + "\n" + throwable.stackTraceToString()),
                    mapOf("Crash Log" to throwable.stackTraceToString())
                )

                InsightsClient.forceFlushAllLogs{
                    // after processing the exception on storage, crash the app
                    originalHandler!!.uncaughtException(thread, throwable)
                } // flush all logs on storage
            }
        }
    }
}