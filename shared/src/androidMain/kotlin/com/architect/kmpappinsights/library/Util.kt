package com.architect.kmpappinsights.library

import android.os.AsyncTask
import android.os.Build
import android.os.Debug
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone

internal object Util {
    private val HEX_ARRAY = "0123456789ABCDEF".toCharArray()
    private val DATE_FORMAT: DateFormat =
        SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.ROOT)

    init {
        val timeZone = TimeZone.getTimeZone("UTC")
        DATE_FORMAT.timeZone = timeZone
    }

    /**
     * Convert a date object to an ISO 8601 formatted string
     *
     * @param date the date object to be formatted
     * @return an ISO 8601 string representation of the date
     */
    internal fun dateToISO8601(date: Date?): String {
        var localDate = date
        if (localDate == null) {
            localDate = Date()
        }

        return DATE_FORMAT.format(localDate)
    }

    /**
     * Convert a duration in milliseconds to the Application Insights serialized duration format
     *
     * @param durationMs the duration in milliseconds
     * @return a string representation of the time span
     */
    internal fun msToTimeSpan(durationMs: Long): String {
        var localDurationMs = durationMs
        if (localDurationMs <= 0) {
            localDurationMs = 0
        }

        val ms = localDurationMs % 1000
        val sec = (localDurationMs / 1000) % 60
        val min = (localDurationMs / (1000 * 60)) % 60
        val hour = (localDurationMs / (1000 * 60 * 60)) % 24
        val days = localDurationMs / (1000 * 60 * 60 * 24)
        val result = if (days == 0L) {
            String.format(Locale.ROOT, "%02d:%02d:%02d.%03d", hour, min, sec, ms)
        } else {
            String.format(
                Locale.ROOT,
                "%d.%02d:%02d:%02d.%03d",
                days,
                hour,
                min,
                sec,
                ms
            )
        }

        return result
    }

    /**
     * Get a SHA-256 hash of the input string if the algorithm is available. If the algorithm is
     * unavailable, return empty string.
     *
     * @param input the string to hash.
     * @return a SHA-256 hash of the input or the empty string.
     */
    fun tryHashStringSha256(input: String): String {
        val salt = "oRq=MAHHHC~6CCe|JfEqRZ+gc0ESI||g2Jlb^PYjc5UYN2P 27z_+21xxd2n"
        try {
            // Get a Sha256 digest
            val hash = MessageDigest.getInstance("SHA-256")
            hash.reset()
            hash.update(input.toByteArray())
            hash.update(salt.toByteArray())
            val hashedBytes = hash.digest()

            val hexChars = CharArray(hashedBytes.size * 2)
            for (j in hashedBytes.indices) {
                val v = hashedBytes[j].toInt() and 0xFF
                hexChars[j * 2] = HEX_ARRAY[v ushr 4]
                hexChars[j * 2 + 1] = HEX_ARRAY[v and 0x0F]
            }

            return String(hexChars)
        } catch (e: NoSuchAlgorithmException) {
            // All android devices should support SHA256, but if unavailable return ""
            return ""
        }
    }

    val isEmulator: Boolean
        /**
         * Determines whether the app is running on aan emulator or on a real device.
         *
         * @return YES if the app is running on an emulator, NO if it is running on a real device
         */
        get() = Build.BRAND.equals("generic", ignoreCase = true)

    internal val isDebuggerAttached: Boolean
        /**
         * Determines whether a debugger is attached while running the app.
         *
         * @return YES the debugger is attached, otherwise NO
         */
        get() = Debug.isDebuggerConnected()

    val isLifecycleTrackingAvailable: Boolean
        /**
         * Determines if Lifecycle Tracking is available for the current user or not.
         *
         * @return YES if app runs on at least OS 4.0
         */
        get() = (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH)

    /**
     * Executes an async task depending on the os version the app runs on
     */
    fun executeTask(asyncTask: AsyncTask<Void?, *, *>) {
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.HONEYCOMB_MR1) {
            asyncTask.execute()
        } else {
            asyncTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR)
        }
    }
}
