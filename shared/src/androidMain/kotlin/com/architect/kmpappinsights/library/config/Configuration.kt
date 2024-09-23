package com.architect.kmpappinsights.library.config

import com.architect.kmpappinsights.library.ApplicationInsights
import java.util.concurrent.atomic.AtomicInteger

class Configuration(
    override var endpointUrl: String? = DEFAULT_ENDPOINT_URL,
    override var sessionIntervalMs: Long = DEFAULT_SESSION_INTERVAL,
    override var maxBatchCount: Int = DEBUG_MAX_BATCH_COUNT,
    override var maxBatchIntervalMs: Int = DEFAULT_MAX_BATCH_INTERVAL_MS
) : ISenderConfig, ISessionConfig, IQueueConfig {

    private val senderReadTimeoutMs: AtomicInteger

    /**
     * The timeout for connecting to the data collector endpoint
     */
    private val senderConnectTimeoutMs: AtomicInteger

    /**
     * Constructs a new INSTANCE of a config
     */
    init {
        // Initialize default values for queue config
        //TODO: If running on a device with developer mode enabled, the default values will be set (move to getter)


        this.maxBatchCount = DEFAULT_MAX_BATCH_COUNT
        this.maxBatchIntervalMs = DEFAULT_MAX_BATCH_INTERVAL_MS

        // Initialize default values for sender config
        this.endpointUrl = DEFAULT_ENDPOINT_URL
        this.senderReadTimeoutMs = AtomicInteger(DEFAULT_SENDER_READ_TIMEOUT)
        this.senderConnectTimeoutMs = AtomicInteger(DEFAULT_SENDER_CONNECT_TIMEOUT)

        // Initialize default values for session config
        this.sessionIntervalMs = DEFAULT_SESSION_INTERVAL.toLong()
    }

    override var senderReadTimeout: Int
        /**
         * Get the timeout for reading the response from the data collector endpoint.
         *
         * @return configured timeout in ms for reading
         */
        get() = senderReadTimeoutMs.get()
        /**
         * Set the timeout for reading the response from the data collector endpoint.
         *
         * @param senderReadTimeout the timeout for reading the response from the endpoint
         */
        set(senderReadTimeout) {
            senderReadTimeoutMs.set(senderReadTimeout)
        }

    override var senderConnectTimeout: Int
        /**
         * Get the timeout for connecting to the data collector endpoint.
         *
         * @return configured timeout in ms for sending
         */
        get() = senderConnectTimeoutMs.get()
        /**
         * Set the timeout for connecting to the data collector endpoint.
         *
         * @param senderConnectTimeout the timeout for connecting to the data collector endpoint in Ms
         */
        set(senderConnectTimeout) {
            senderConnectTimeoutMs.set(senderConnectTimeout)
        }

    companion object {
        // Default values for queue config
        const val DEBUG_MAX_BATCH_COUNT: Int = 5
        const val DEBUG_MAX_BATCH_INTERVAL_MS: Int = 3 * 1000
        const val DEFAULT_MAX_BATCH_COUNT: Int = 100
        const val DEFAULT_MAX_BATCH_INTERVAL_MS: Int = 15 * 1000

        // Default values for sender config
        const val DEFAULT_ENDPOINT_URL: String = "https://dc.services.visualstudio.com/v2/track"
        const val DEFAULT_SENDER_READ_TIMEOUT: Int = 10 * 1000
        const val DEFAULT_SENDER_CONNECT_TIMEOUT: Int = 15 * 1000

        // Default values for session config
        protected const val DEFAULT_SESSION_INTERVAL: Long = 20 * 1000 // 20 seconds
    }
}
