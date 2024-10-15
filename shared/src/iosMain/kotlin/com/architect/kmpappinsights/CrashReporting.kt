package com.architect.kmpappinsights.services

import kotlinx.cinterop.ExperimentalForeignApi
import platform.Foundation.NSSetUncaughtExceptionHandler

actual class CrashReporting {
    actual companion object {
        @OptIn(ExperimentalForeignApi::class)
        actual fun registerForCrashReporting() {

        }
    }
}