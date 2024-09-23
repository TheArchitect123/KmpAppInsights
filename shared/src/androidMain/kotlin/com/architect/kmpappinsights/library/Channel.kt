package com.architect.kmpappinsights.library

import com.architect.kmpappinsights.library.ChannelQueue
import com.architect.kmpappinsights.library.EnvelopeFactory
import com.architect.kmpappinsights.library.Persistence
import com.architect.kmpappinsights.library.Sender
import com.architect.kmpappinsights.library.config.IQueueConfig
import com.architect.kmpappinsights.logging.InternalLogging
import com.microsoft.telemetry.Base
import com.microsoft.telemetry.Data
import com.microsoft.telemetry.Domain
import com.microsoft.telemetry.IChannel
import com.microsoft.telemetry.cs2.Envelope
import java.io.IOException
import java.io.StringWriter
import kotlin.concurrent.Volatile

/**
 * This class records telemetry for application insights.
 */
internal class Channel protected constructor() : IChannel {
    /**
     * Test hook to the sender
     */
    protected var queue: ChannelQueue? = null

    /**
     * Persistence used for saving unhandled exceptions.
     */
    private var persistence: Persistence?

    /**
     * Instantiates a new INSTANCE of Channel
     */
    init {
        this.persistence = Persistence.Companion.getInstance()
    }

    /**
     * Persist all pending items.
     */
    override fun synchronize() {
        queue!!.flush()
        if (Sender.Companion.getInstance() != null) {
            Sender.Companion.getInstance()!!.sendNextFile()
        }
    }

    /**
     * Records the passed in data.
     *
     * @param data the base object to record
     */
    override fun log(data: Base, tags: Map<String, String>) {
        if (data is Data<*>) {
            val envelope: Envelope = EnvelopeFactory.Companion.getInstance()!!
                .createEnvelope(data as Data<Domain?>)

            // log to queue
            val serializedEnvelope = serializeEnvelope(envelope)
            queue!!.enqueue(serializedEnvelope)
            InternalLogging.info(TAG, "enqueued telemetry", envelope.name)
        } else {
            InternalLogging.warn(TAG, "telemetry not enqueued, must be of type ITelemetry")
        }
    }

    protected fun serializeEnvelope(envelope: Envelope?): String? {
        try {
            if (envelope != null) {
                val stringWriter = StringWriter()
                envelope.serialize(stringWriter)
                return stringWriter.toString()
            }
            InternalLogging.warn(
                TAG,
                "Envelop wasn't empty but failed to serialize anything, returning null"
            )
            return null
        } catch (e: IOException) {
            InternalLogging.warn(TAG, "Failed to save data with exception: $e")
            return null
        }
    }

    fun processException(data: Data<Domain?>) {
        val envelope: Envelope = EnvelopeFactory.Companion.getInstance()!!
            .createEnvelope(data)

        queue!!.isCrashing = true
        queue!!.flush()

        val serializedEnvelope = serializeEnvelope(envelope)
        val serializedEvelopeArray = arrayOf(serializedEnvelope)

        if (this.persistence != null) {
            InternalLogging.info(TAG, "persisting crash", envelope.toString())
            persistence.persist(serializedEvelopeArray, true)
        } else {
            InternalLogging.info(TAG, "error persisting crash", envelope.toString())
        }
    }

    /**
     * Test hook to set the queue for this channel
     *
     * @param queue the queue to use for this channel
     */
    protected fun setQueue(queue: ChannelQueue?) {
        this.queue = queue
    }

    /**
     * Set the persistence instance used to save unhandled exceptions.
     *
     * @param persistence the persitence instance which should be used
     */
    protected fun setPersistence(persistence: Persistence?) {
        this.persistence = persistence
    }

    companion object {
        private const val TAG = "Channel"

        /**
         * Volatile boolean for double checked synchronize block
         */
        @Volatile
        private var isChannelLoaded = false

        /**
         * Synchronization LOCK for setting static context
         */
        private val LOCK = Any()

        /**
         * The singleton INSTANCE of this class
         */
        private var instance: Channel? = null

        fun initialize(config: IQueueConfig) {
            // note: isPersistenceLoaded must be volatile for the double-checked LOCK to work
            if (!isChannelLoaded) {
                synchronized(LOCK) {
                    if (!isChannelLoaded) {
                        isChannelLoaded = true
                        instance = Channel()
                        instance!!.setQueue(ChannelQueue(config))
                    }
                }
            }
        }

        /**
         * @return the INSTANCE of Channel or null if not yet initialized
         */
        fun getInstance(): IChannel? {
            if (instance == null) {
                InternalLogging.error(TAG, "getSharedInstance was called before initialization")
            }

            return instance
        }
    }
}
