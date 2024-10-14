package com.architect.kmpappinsights

import io.ktor.client.plugins.logging.Logger
import co.touchlab.kermit.Logger as LOGCAT

class InsightsLogger : Logger {
    private val loggerWithTag by lazy {
        LOGCAT.withTag("APP_INSIGHTS_KMP")
    }

    override fun log(message: String) {
        loggerWithTag.i(message)

    }

    fun logError(message: String) {
        loggerWithTag.e(message)
    }


}