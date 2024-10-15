package com.architect.kmpappinsights.services

import com.architect.kmpappinsights.contracts.ExceptionStackTraceDetailsInfo
import platform.Foundation.NSSetUncaughtExceptionHandler

actual class ExceptionFormatter {
    actual companion object {
        actual fun getStackException(ex: Exception): ExceptionStackTraceDetailsInfo {
            TODO()
            
        }
    }
}


