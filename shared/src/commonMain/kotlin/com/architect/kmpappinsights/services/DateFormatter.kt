package com.architect.kmpappinsights.services

expect class DateFormatter {
    companion object{
        fun getDateFormatStringFromMilliseconds(ms: Long) : String
    }
}


