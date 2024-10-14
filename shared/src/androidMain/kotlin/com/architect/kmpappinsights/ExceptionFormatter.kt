package com.architect.kmpappinsights.services

import com.architect.kmpappinsights.contracts.ExceptionStackTraceDetailsInfo
import com.architect.kmpappinsights.contracts.TraceSeverityLevel

actual class ExceptionFormatter {
    actual companion object {
        actual fun getStackException(ex: Exception): ExceptionStackTraceDetailsInfo {
            val stack = ex.stackTrace.first()
            return ExceptionStackTraceDetailsInfo(
                level = TraceSeverityLevel.Error.ordinal,
                method = stack.methodName,
                fileName = stack.fileName,
                line = stack.lineNumber.toLong(),
                assembly = stack.className
            )
        }
    }
}

