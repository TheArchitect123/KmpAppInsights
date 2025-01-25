package com.architect.kmpappinsights.services

import com.architect.kmpappinsights.InsightsClient
import kotlin.experimental.ExperimentalNativeApi
import kotlin.system.exitProcess

actual class CrashReporting {
    actual companion object {
        @OptIn(ExperimentalNativeApi::class)
        actual fun registerForCrashReporting() {
            setUnhandledExceptionHook { crashDetails ->
                InsightsClient.uploadAppCrashLog(Exception(crashDetails))
                exitProcess(1) // close the app after launching background process
            }
        }
    }
}