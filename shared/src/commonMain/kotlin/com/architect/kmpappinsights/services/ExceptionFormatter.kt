package com.architect.kmpappinsights.services

import com.architect.kmpappinsights.contracts.ExceptionStackTraceDetailsInfo

expect class ExceptionFormatter {
    companion object {
        fun getStackException(ex: Exception): ExceptionStackTraceDetailsInfo
    }
}