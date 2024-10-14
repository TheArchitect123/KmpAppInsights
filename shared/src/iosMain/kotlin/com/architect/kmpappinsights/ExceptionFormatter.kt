package com.architect.kmpappinsights.services

import com.architect.kmpappinsights.contracts.ExceptionStackTraceDetailsInfo

actual class ExceptionFormatter {
    actual companion object {
        actual fun getStackException(ex: Exception): ExceptionStackTraceDetailsInfo {
            TODO()
        }
    }
}


