package com.architect.kmpappinsights.services

import com.architect.kmpappinsights.contracts.ExceptionStackTraceDetailsInfo
import com.architect.kmpappinsights.contracts.TraceSeverityLevel

actual class ExceptionFormatter {
    actual companion object {
        actual fun getStackException(ex: Exception): ExceptionStackTraceDetailsInfo {
            if(ex.stackTrace.isNotEmpty()) {
                val stack = ex.stackTrace.first()
                return ExceptionStackTraceDetailsInfo(
                    level = TraceSeverityLevel.Error.ordinal,
                    method = stack.methodName,
                    fileName = "Unknown",
                    line = stack.lineNumber.toLong(),
                    assembly = stack.className
                )
            }

            return ExceptionStackTraceDetailsInfo(
                level = TraceSeverityLevel.Error.ordinal,
                method = "Unknown",
                fileName = "Unknown",
                line = 0,
                assembly = "Unknown"
            )
        }
    }
}

