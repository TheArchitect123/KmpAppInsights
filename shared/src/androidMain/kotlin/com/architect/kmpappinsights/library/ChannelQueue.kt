package com.architect.kmpappinsights.library

import com.architect.kmpappinsights.library.Persistence
import com.architect.kmpappinsights.library.config.IQueueConfig
import com.architect.kmpappinsights.logging.InternalLogging
import java.util.LinkedList
import java.util.Timer
import java.util.TimerTask
import kotlin.concurrent.Volatile

/**
 * This singleton class sends data to the endpoint
 */
internal class ChannelQueue(
    /**
     * The configuration for this queue
     */
    protected var config: IQueueConfig
) {
    /**
     * The synchronization LOCK for queueing items
     */
    private val LOCK = Any()

    /**
     * The timer for this queue
     */
    protected val timer: Timer =
        Timer("Application Insights Sender Queue", true)

    /**
     * The linked list for this queue
     */
    protected val list: MutableList<String> = LinkedList()

    /**
     * If true the app is crashing and data should be persisted instead of sent
     */
    @Volatile
    var isCrashing: Boolean = false

    /**
     * All tasks which have been scheduled and not cancelled
     */
    private var scheduledPersistenceTask: TimerTask? = null

    /**
     * Persistence used for saving queue items.
     */
    private var persistence: Persistence?

    /**
     * Prevent external instantiation
     */
    init {
        this.persistence = Persistence.Companion.getInstance()
    }

    /**
     * Adds an item to the sender queue
     *
     * @param serializedItem a serialized telemetry item to enqueue
     * @return true if the item was successfully added to the queue
     */
    fun enqueue(serializedItem: String?): Boolean {
        // prevent invalid argument exception
        if (serializedItem == null) {
            return false
        }

        var success: Boolean
        synchronized(this.LOCK) {
            // attempt to add the item to the queue
            success = list.add(serializedItem)
            if (success) {
                if ((list.size >= config.maxBatchCount) || isCrashing) {
                    // persisting if the queue is full
                    flush()
                } else if (list.size == 1) {
                    schedulePersitenceTask()
                }
            } else {
                InternalLogging.warn(TAG, "Unable to add item to queue")
            }
        }

        return success
    }

    /**
     * Empties the queue and sends all items to persistence
     */
    fun flush() {
        // cancel the scheduled persistence task if it exists
        if (this.scheduledPersistenceTask != null) {
            scheduledPersistenceTask!!.cancel()
        }

        var data: Array<String?>
        synchronized(this.LOCK) {
            if (!list.isEmpty()) {
                data = arrayOfNulls(list.size)
                list.toArray<String>(data)
                list.clear()

                executePersistenceTask(data)
            }
        }
    }

    /**
     * Schedules a persistence task based on max maxBatchIntervalMs.
     *
     * @see com.architect.kmpappinsights.library.ChannelQueue.TriggerPersistTask
     */
    protected fun schedulePersitenceTask() {
        // schedule a FlushTask if this is the first item in the queue
        this.scheduledPersistenceTask = TriggerPersistTask()
        timer.schedule(
            this.scheduledPersistenceTask,
            config.maxBatchIntervalMs
        )
    }

    /**
     * Initiates persisting the content queue.
     */
    protected fun executePersistenceTask(data: Array<String?>?) {
        if (data != null) {
            if (persistence != null) {
                persistence.persist(data, false)
            }
        }
    }

    /**
     * Set the isCrashing flag
     *
     * @param isCrashing if true the app is assumed to be crashing and data will be written to disk
     */
    protected fun setIsCrashing(isCrashing: Boolean) {
        this.isCrashing = isCrashing
    }

    /**
     * Set the persistence instance used to save items.
     *
     * @param persistence the persitence instance which should be used
     */
    protected fun setPersistence(persistence: Persistence?) {
        this.persistence = persistence
    }

    /**
     * Set the config for this queue.
     *
     * @param config a config which contains information about how this queue should behave.
     */
    protected fun setQueueConfig(config: IQueueConfig) {
        this.config = config
    }

    /**
     * A task to initiate queue sendPendingData on another thread
     */
    private inner class TriggerPersistTask
    /**
     * The sender INSTANCE is provided to the constructor as a test hook
     */
        : TimerTask() {
        override fun run() {
            flush()
        }
    }

    companion object {
        /**
         * Logging tag for this class
         */
        private const val TAG = "TelemetryQueue"
    }
}

