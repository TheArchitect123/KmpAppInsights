package com.architect.kmpappinsights.services

import com.architect.kmpappinsights.InsightsClient
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.lang.Exception

actual class CrashReporting {
    actual companion object {
        private val originalHandler = Thread.getDefaultUncaughtExceptionHandler()
        actual fun registerForCrashReporting() {
            Thread.setDefaultUncaughtExceptionHandler { thread, throwable ->
                runBlocking {
                    GlobalScope.launch {
                        InsightsClient.uploadAppCrashLog(Exception(throwable))
                    }
                }

                originalHandler!!.uncaughtException(thread, throwable) // proceeds with crashing
            }
        }
    }
}