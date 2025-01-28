package com.architect.kmpappinsights.services

import com.architect.kmpappinsights.InsightsClient
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlin.experimental.ExperimentalNativeApi
import kotlin.system.exitProcess

actual class CrashReporting {
    actual companion object {
        @OptIn(ExperimentalNativeApi::class)
        actual fun registerForCrashReporting() {
            setUnhandledExceptionHook { crashDetails ->
                GlobalScope.launch {
                    runBlocking {
                        InsightsClient.uploadAppCrashLog(Exception(crashDetails))
                    }

                    // wait for this suspend to finish
                    exitProcess(1) // close the app after launching background process
                }

            }
        }
    }
}