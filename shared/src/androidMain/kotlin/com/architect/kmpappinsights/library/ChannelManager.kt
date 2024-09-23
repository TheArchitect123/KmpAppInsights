package com.architect.kmpappinsights.library

import com.architect.kmpappinsights.logging.InternalLogging
import com.microsoft.cll.android.AndroidCll
import com.microsoft.telemetry.IChannel
import kotlin.concurrent.Volatile

/**
 * A single class that manages the different types of channels we support
 */
class ChannelManager protected constructor(channelType: com.architect.kmpappinsights.library.ChannelType?) {
    /**
     * A Singleton instance of an IChannel set by default to Channel but can
     * be override using setChannel
     */
    private var channel: IChannel? = null

    /**
     * Instantiates a new INSTANCE of ChannelManager
     */
    init {
        Channel.initialize(ApplicationInsights.configuration)
        setChannel(channelType)
    }

    /**
     * Returns the current channel
     * @return The channel that is currently in use
     */
    fun getChannel(): IChannel? {
        return channel
    }

    /**
     * Sets the current channel to use
     * @param channelType The new channel to use
     */
    protected fun setChannel(channelType: com.architect.kmpappinsights.library.ChannelType?) {
        if (channelType == null) {
            InternalLogging.warn(TAG, "ChannelType is null, setting up using default channel type")
            this.channel = createDefaultChannel()
            return
        }

        when (channelType) {
            com.architect.kmpappinsights.library.ChannelType.Default -> this.channel =
                createDefaultChannel()

            com.architect.kmpappinsights.library.ChannelType.CommonLoggingLibraryChannel -> this.channel =
                createTelemetryClientChannel()
        }
    }

    /**
     * Resets this instance of the channel manager
     */
    protected fun reset() {
        if (channel != null) {
            channel = null
        }

        if (instance != null) {
            instance = null
        }

        isInitialized = false
    }

    /**
     * Creates a channel of default type
     * @return The new default channel
     */
    private fun createDefaultChannel(): IChannel? {
        var defaultChannel =
            Channel.getInstance()
        if (defaultChannel == null) {
            Channel.initialize(
                ApplicationInsights.configuration
            )
            defaultChannel =
                Channel.getInstance()
        }

        return defaultChannel
    }

    /**
     * Creates a channel of the Telemetry Client type
     * @return The new Telemetry Client channel
     */
    private fun createTelemetryClientChannel(): IChannel {
        val iKey =
            if (ApplicationInsights.Companion.getInstrumentationKey() == null) "" else ApplicationInsights.Companion.getInstrumentationKey()!!
        val cll = AndroidCll.initialize(
            iKey,
            ApplicationInsights.INSTANCE.context,
            ApplicationInsights.configuration.endpointUrl
        ) as AndroidCll
        cll.useLagacyCS(true)
        return cll
    }

    companion object {
        private const val TAG = "ChannelManager"

        /**
         * Volatile boolean for double checked synchronize block
         */
        @Volatile
        private var isInitialized = false

        /**
         * Synchronization LOCK for setting static context
         */
        private val LOCK = Any()

        /**
         * The singleton INSTANCE of this class
         */
        private var instance: ChannelManager? = null

        /**
         * Initializes the ChannelManager to it's default IChannel instance
         */
        fun initialize(channelType: com.architect.kmpappinsights.library.ChannelType?) {
            if (!isInitialized) {
                synchronized(LOCK) {
                    if (!isInitialized) {
                        isInitialized = true
                        instance = ChannelManager(channelType)
                    }
                }
            }
        }

        /**
         * @return the INSTANCE of ChannelManager or null if not yet initialized
         */
        fun getInstance(): ChannelManager? {
            if (instance == null) {
                InternalLogging.error(TAG, "getSharedInstance was called before initialization")
            }

            return instance
        }
    }
}
