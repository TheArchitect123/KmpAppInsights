package com.architect.kmpappinsights.library

import android.os.AsyncTask
import android.os.Looper
import com.architect.kmpappinsights.library.config.ISenderConfig
import com.architect.kmpappinsights.logging.InternalLogging
import java.io.BufferedReader
import java.io.File
import java.io.IOException
import java.io.InputStreamReader
import java.io.OutputStreamWriter
import java.io.Writer
import java.net.HttpURLConnection
import java.net.URL
import java.util.Locale
import java.util.concurrent.atomic.AtomicInteger
import java.util.zip.GZIPOutputStream
import kotlin.concurrent.Volatile

/**
 * This singleton class sends data to the endpoint.
 */
internal class Sender protected constructor(
    /**
     * The configuration for this sender.
     */
    protected val config: ISenderConfig
) {
    /**
     * Persistence object used to reserve, free, or delete files.
     */
    var persistence: Persistence?

    /**
     * Thread safe counter to keep track of num of operations
     */
    private val operationsCount = AtomicInteger(0)

    /**
     * Restrict access to the default constructor
     *
     * @param config the telemetryconfig object used to configure the telemetry module
     */
    init {
        this.persistence = Persistence.getInstance()
    }

    fun triggerSending() {
        Util.executeTask(createSenderTask())
    }

    private fun createSenderTask(): AsyncTask<Void?, Void?, Void?> {
        return object : AsyncTask<Void?, Void?, Void?>() {
            override fun doInBackground(vararg params: Void?): Void? {
                sendNextFile()
                return null
            }
        }
    }

    fun sendNextFile() {
        //as sendNextFile() NOT guarranteed to be executed from a background thread, we need to
        //create an async task if necessary
        if (Looper.myLooper() == Looper.getMainLooper()) {
            InternalLogging.info(TAG, "Kick of new async task", "")
            Util.executeTask(createSenderTask())
        } else {
            if (runningRequestCount() < SENDER_COUNT) {
                operationsCount.getAndIncrement()
                println("Sending Operation Instance " + runningRequestCount())
                // Send the persisted data
                if (this.persistence != null) {
                    val pc = persistence!!
                    val fileToSend = pc.nextAvailableFile()
                    if (fileToSend != null) {
                        send(fileToSend)
                    }
                }
                operationsCount.getAndDecrement()
            } else {
                InternalLogging.info(TAG, "We have already 10 pending reguests", "")
            }
        }
    }


    protected fun send(fileToSend: File?) {
        val persistedData: String = persistence!!.load(fileToSend)
        if (!persistedData.isEmpty()) {
            try {
                this.sendRequestWithPayload(persistedData, fileToSend)
            } catch (e: IOException) {
                InternalLogging.warn(TAG, "Couldn't send request with IOException: $e")
            }
        } else {
            persistence!!.deleteFile(fileToSend)
        }
    }

    protected fun runningRequestCount(): Int {
        return operationsCount.get()
    }

    @Throws(IOException::class)
    protected fun sendRequestWithPayload(payload: String?, fileToSend: File?) {
        var writer: Writer? = null
        val url = URL(config.endpointUrl)
        val connection = url.openConnection() as HttpURLConnection
        connection.readTimeout = config.senderReadTimeout
        connection.connectTimeout = config.senderConnectTimeout
        connection.requestMethod = "POST"
        connection.setRequestProperty("Content-Type", "application/x-json-stream")
        connection.doOutput = true
        connection.doInput = true
        connection.useCaches = false

        try {
            if (ApplicationInsights.isDeveloperMode && !payload.isNullOrBlank()) {
                InternalLogging.info(TAG, "Logging payload", payload)
            }

            writer = getWriter(connection)
            writer.write(payload)
            writer.flush()

            // Starts the query
            connection.connect()

            // read the response code while we're ready to catch the IO exception
            val responseCode = connection.responseCode

            // process the response
            onResponse(connection, responseCode, payload, fileToSend)
        } catch (e: IOException) {
            InternalLogging.warn(TAG, "Couldn't send data with IOException: $e")
            if (this.persistence != null) {
                InternalLogging.info(
                    TAG,
                    "Persisting because of IOException: ",
                    "We're probably offline =)"
                )
                val pc = persistence!!
                pc.makeAvailable(fileToSend) //send again later
            }
        } finally {
            if (writer != null) {
                try {
                    writer.close()
                } catch (e: IOException) {
                    // no-op
                }
            }
        }
    }

    /**
     * Callback for the http response from the sender
     *
     * @param connection   a connection containing a response
     * @param responseCode the response code from the connection
     * @param payload      the payload which generated this response
     * @param fileToSend   reference to the file we want to send
     */
    protected fun onResponse(
        connection: HttpURLConnection,
        responseCode: Int,
        payload: String?,
        fileToSend: File?
    ) {
        InternalLogging.info(TAG, "response code", responseCode.toString())

        val isRecoverableError = isRecoverableError(responseCode)
        if (isRecoverableError) {
            this.onRecoverable(payload, fileToSend)
        } else {
            //delete in case of success or unrecoverable errors
            if (this.persistence != null) {
                val pc = persistence!!
                pc.deleteFile(fileToSend)
            }

            //trigger send next file or log unexpected responses
            val builder = StringBuilder()
            if (isExpected(responseCode)) {
                this.onExpected(connection, builder)
                sendNextFile()
            } else {
                this.onUnexpected(connection, responseCode, builder)
            }
        }
    }

    protected fun isRecoverableError(responseCode: Int): Boolean {
        val recoverableCodes: List<Int> = mutableListOf(408, 429, 500, 503, 511)
        return recoverableCodes.contains(responseCode)
    }

    protected fun isExpected(responseCode: Int): Boolean {
        return (199 < responseCode && responseCode <= 203)
    }

    /**
     * Process the expected response. If {code:TelemetryChannelConfig.isDeveloperMode}, read the
     * response and log it.
     *
     * @param connection a connection containing a response
     * @param builder    a string builder for storing the response
     */
    protected fun onExpected(connection: HttpURLConnection, builder: StringBuilder) {
        if (ApplicationInsights.isDeveloperMode) {
            this.readResponse(connection, builder)
        }
    }

    /**
     * @param connection   a connection containing a response
     * @param responseCode the response code from the connection
     * @param builder      a string builder for storing the response
     */
    protected fun onUnexpected(
        connection: HttpURLConnection,
        responseCode: Int,
        builder: StringBuilder
    ) {
        val message = String.format(Locale.ROOT, "Unexpected response code: %d", responseCode)
        builder.append(message)
        builder.append("\n")

        // log the unexpected response
        InternalLogging.warn(TAG, message)

        // attempt to read the response stream
        this.readResponse(connection, builder)
    }

    /**
     * Writes the payload to disk if the response code indicates that the server or network caused
     * the failure instead of the client.
     *
     * @param payload    the payload which generated this response
     * @param fileToSend reference to the file we sent
     */
    protected fun onRecoverable(payload: String?, fileToSend: File?) {
        if (!payload.isNullOrBlank()) {
            InternalLogging.info(
                TAG,
                "Recoverable error (probably a server error), persisting data",
                payload
            )
        }
        if (this.persistence != null) {
            val pc = persistence!!
            pc.makeAvailable(fileToSend)
        }
    }

    /**
     * Reads the response from a connection.
     *
     * @param connection the connection which will read the response
     * @param builder    a string builder for storing the response
     */
    protected fun readResponse(connection: HttpURLConnection, builder: StringBuilder) {
        var reader: BufferedReader? = null
        try {
            var inputStream = connection.errorStream
            if (inputStream == null) {
                inputStream = connection.inputStream
            }

            if (inputStream != null) {
                val streamReader = InputStreamReader(inputStream, "UTF-8")
                reader = BufferedReader(streamReader)
                var responseLine = reader.readLine()
                while (responseLine != null) {
                    builder.append(responseLine)
                    responseLine = reader.readLine()
                }
            } else {
                builder.append(connection.responseMessage)
            }
        } catch (e: IOException) {
            InternalLogging.warn(TAG, e.toString())
        } finally {
            if (reader != null) {
                try {
                    reader.close()
                } catch (e: IOException) {
                    InternalLogging.warn(TAG, e.toString())
                }
            }
        }
    }

    /**
     * Gets a writer from the connection stream (allows for test hooks into the write stream)
     *
     * @param connection the connection to which the stream will be flushed
     * @return a writer for the given connection stream
     * @throws java.io.IOException Exception thrown by GZIP (used in SDK 19+)
     */

    @Throws(IOException::class)
    protected fun getWriter(connection: HttpURLConnection): Writer {
        // GZIP if we are running SDK 19 or higher
        connection.addRequestProperty("Content-Encoding", "gzip")
        connection.setRequestProperty("Content-Type", "application/x-json-stream")
        val gzip = GZIPOutputStream(connection.outputStream, true)
        return OutputStreamWriter(gzip)
    }

    /**
     * Set the instance, used for tests
     *
     * @param instance the test instance to use here
     */
    protected fun setInstance(instance: Sender?) {
        Companion.instance = instance
    }

    companion object {
        private const val TAG = "Sender"

        private const val SENDER_COUNT = 3

        /**
         * Synchronization LOCK for setting static config.
         */
        private val LOCK = Any()

        /**
         * Volatile boolean for double checked synchronize block.
         */
        @Volatile
        private var isSenderLoaded = false

        /**
         * The shared Sender instance.
         */
        private var instance: Sender? = null

        /**
         * Initialize the INSTANCE of sender.
         */
        fun initialize(config: ISenderConfig) {
            // note: isSenderLoaded must be volatile for the double-checked LOCK to work
            if (!isSenderLoaded) {
                synchronized(LOCK) {
                    if (!isSenderLoaded) {
                        isSenderLoaded = true
                        instance = Sender(config)
                    }
                }
            }
        }

        /**
         * @return the INSTANCE of the sender calls initialize before that.
         */
        fun getInstance(): Sender? {
            if (instance == null) {
                InternalLogging.error(TAG, "getSharedInstance was called before initialization")
            }
            return instance
        }
    }
}
