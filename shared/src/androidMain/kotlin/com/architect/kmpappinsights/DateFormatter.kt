package com.architect.kmpappinsights.services

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

actual class DateFormatter {
    actual companion object {
        actual fun getDateFormatStringFromMilliseconds(ms: Long): String {
            val formatDate = "dd.hh:mm:ss.SSS"
            return SimpleDateFormat(formatDate, Locale.getDefault()).format(Date(ms)).toString()

        }
    }
}


